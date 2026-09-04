package kr.kh.kihibooks.model.vo;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PublisherDailyKpiVO {
    private LocalDate salesDate;
    private long salesAmount;
    private int paidPurchaseCount;
}
