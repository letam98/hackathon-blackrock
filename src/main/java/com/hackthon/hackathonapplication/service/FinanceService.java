package com.hackthon.hackathonapplication.service;

import com.hackthon.hackathonapplication.entity.FinalProjection;
import com.hackthon.hackathonapplication.entity.Rule;
import com.hackthon.hackathonapplication.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface FinanceService {
    public void enrich(Transaction tx);
    public void applyRules(Transaction tx, List<Rule> qRules, List<Rule> pRules);
    public FinalProjection calculateReturns(BigDecimal principal, int age, BigDecimal monthlySalary, Rule k);

    public boolean isWithin(LocalDateTime date, Rule rule);
}
