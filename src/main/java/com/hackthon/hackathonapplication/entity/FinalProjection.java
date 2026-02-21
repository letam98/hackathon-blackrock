package com.hackthon.hackathonapplication.entity;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class FinalProjection {
    private LocalDateTime start;
    private LocalDateTime end;
    private BigDecimal amountInvested;
    private BigDecimal npsFinalValue;
    private BigDecimal niftyFinalValue;
    private BigDecimal taxBenefit;
}