package com.hackthon.hackathonapplication.controller;





import com.hackthon.hackathonapplication.entity.CalculationRequest;
import com.hackthon.hackathonapplication.entity.FinalProjection;
import com.hackthon.hackathonapplication.entity.Rule;
import com.hackthon.hackathonapplication.entity.Transaction;
import com.hackthon.hackathonapplication.service.FinanceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/blackrock/challenge/v1")
@Slf4j
@Tag(name = "Black rock :Finance Saving")
public class SavingsController {

    private final FinanceService service;

    public SavingsController(FinanceService service) { this.service = service; }

    @PostMapping("/transactions:validator")
    public Map<String, List<Transaction>> validator(@RequestBody Map<String, Object> body) {
        log.info("request body transactionvalidator :"+body);
        List<Map<String, Object>> txList = (List<Map<String, Object>>) body.get("transactions");
        List<Transaction> valid = new ArrayList<>(), invalid = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (Map<String, Object> m : txList) {
            Transaction tx = new Transaction();
            tx.setAmount(new BigDecimal(m.get("amount").toString()));
            tx.setDate(LocalDateTime.parse(m.get("date").toString().replace(" ", "T")));

            String key = tx.getDate().toString() + "_" + tx.getAmount();
            if (tx.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                tx.setMessage("Negative amounts are not allowed");
                invalid.add(tx);
            } else if (!seen.add(key)) {
                tx.setMessage("Duplicate transaction");
                invalid.add(tx);
            } else {
                service.enrich(tx);
                valid.add(tx);
            }
        }
        log.info("finance validation response :"+valid);
        return Map.of("valid", valid, "invalid", invalid);
    }

    @PostMapping("/calculate")
    public List<FinalProjection> fullCalculation(@RequestBody CalculationRequest req) {
        if (req.getQ() != null) {
            for (int i = 0; i < req.getQ().size(); i++) req.getQ().get(i).setOriginalIndex(i);
        }
        req.getTransactions().forEach(tx -> {
            service.enrich(tx);
            service.applyRules(tx, req.getQ(), req.getP());
        });
        List<FinalProjection> results = new ArrayList<>();
        for (Rule k : req.getK()) {
            BigDecimal totalRemnant = req.getTransactions().stream()
                    .filter(tx -> service.isWithin(tx.getDate(), k))
                    .map(Transaction::getRemnant)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            results.add(service.calculateReturns(totalRemnant, req.getCurrentAge(), req.getMonthlySalary(), k));
        }
        return results;
    }
}
