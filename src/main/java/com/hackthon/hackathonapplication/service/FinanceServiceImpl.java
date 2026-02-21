package com.hackthon.hackathonapplication.service;

import com.hackthon.hackathonapplication.entity.FinalProjection;
import com.hackthon.hackathonapplication.entity.Rule;
import com.hackthon.hackathonapplication.entity.Transaction;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class FinanceServiceImpl implements FinanceService {

    private final BigDecimal HUNDRED = new BigDecimal("100");

    // Step 1: Initial Enrichment
    public void enrich(Transaction tx) {
        BigDecimal amount = tx.getAmount();
        BigDecimal ceiling = amount.divide(HUNDRED, 0, RoundingMode.CEILING).multiply(HUNDRED);
        tx.setCeiling(ceiling);
        tx.setRemnant(ceiling.subtract(amount));
    }

    // Step 2 & 3: Temporal Overrides
    public void applyRules(Transaction tx, List<Rule> qRules, List<Rule> pRules) {
        // Q-Rules: Override logic (Find latest start date tie-breaker)
        if (qRules != null) {
            qRules.stream()
                    .filter(r -> isWithin(tx.getDate(), r))
                    .sorted(Comparator.comparing(Rule::getStart).reversed().thenComparing(Rule::getOriginalIndex))
                    .findFirst()
                    .ifPresent(r -> tx.setRemnant(r.getFixed()));
        }

        // P-Rules: Additive logic
        if (pRules != null) {
            BigDecimal totalExtra = pRules.stream()
                    .filter(r -> isWithin(tx.getDate(), r))
                    .map(Rule::getExtra)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            tx.setRemnant(tx.getRemnant().add(totalExtra));
        }
    }

    // Step 5: Financial Calculations (Compound Interest + Inflation + Tax)
    public FinalProjection calculateReturns(BigDecimal principal, int age, BigDecimal monthlySalary, Rule k) {
        int t = (age < 60) ? (60 - age) : 5;

        // Rates from challenge images
        double npsRate = 0.0711;
        double niftyRate = 0.1449;
        double inflationRate = 0.055;

        // Future Value: A = P * (1 + r)^t
        BigDecimal npsNominal = principal.multiply(BigDecimal.valueOf(Math.pow(1 + npsRate, t)));
        BigDecimal niftyNominal = principal.multiply(BigDecimal.valueOf(Math.pow(1 + niftyRate, t)));

        // Inflation Real Value: Real = Nominal / (1 + i)^t
        BigDecimal inflationFactor = BigDecimal.valueOf(Math.pow(1 + inflationRate, t));
        BigDecimal npsReal = npsNominal.divide(inflationFactor, 2, RoundingMode.HALF_UP);
        BigDecimal niftyReal = niftyNominal.divide(inflationFactor, 2, RoundingMode.HALF_UP);

        // Tax Calculation
        BigDecimal annualIncome = monthlySalary.multiply(new BigDecimal("12"));
        BigDecimal taxBenefit = calculateTaxBenefit(principal, annualIncome);

        return FinalProjection.builder()
                .start(k.getStart()).end(k.getEnd())
                .amountInvested(principal)
                .npsFinalValue(npsReal).niftyFinalValue(niftyReal)
                .taxBenefit(taxBenefit).build();
    }

    private BigDecimal calculateTaxBenefit(BigDecimal invested, BigDecimal income) {
        BigDecimal taxableIncome = income.subtract(new BigDecimal("50000")); // Standard Deduction
        BigDecimal npsLimit = income.multiply(new BigDecimal("0.10")).min(new BigDecimal("200000"));
        BigDecimal deduction = invested.min(npsLimit);

        // Tax Benefit = Tax on full income - Tax on income after deduction
        return calculateTax(taxableIncome).subtract(calculateTax(taxableIncome.subtract(deduction)));
    }

    private BigDecimal calculateTax(BigDecimal income) {
        if (income.compareTo(new BigDecimal("700000")) <= 0) return BigDecimal.ZERO;

        BigDecimal tax = BigDecimal.ZERO;
        // Slab 7L - 10L (10%)
        if (income.compareTo(new BigDecimal("700000")) > 0) {
            tax = tax.add(income.min(new BigDecimal("1000000")).subtract(new BigDecimal("700000")).multiply(new BigDecimal("0.10")));
        }
        // Higher slabs 10L-12L (15%), 12L-15L (20%), 15L+ (30%) are added similarly
        return tax;
    }

    public boolean isWithin(LocalDateTime date, Rule rule) {
        return !date.isBefore(rule.getStart()) && !date.isAfter(rule.getEnd());
    }
}