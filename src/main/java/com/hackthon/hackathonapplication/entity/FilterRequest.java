package com.hackthon.hackathonapplication.entity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FilterRequest {
    private List<Transaction> transactions;
    private List<Rule> q;
    private List<Rule> p;
    private List<Rule> k;
}