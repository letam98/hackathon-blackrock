package com.hackthon.hackathonapplication.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Rule {
    private int originalIndex; // For tie-breaking
    private BigDecimal fixed;  // Q-Rule
    private BigDecimal extra;  // P-Rule
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime start;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime end;
}