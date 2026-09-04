package kr.kh.kihibooks.model.vo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PublisherBookKpiVO {
    private String boCode;
    private String boTitle;
    private String editorName;
    private long salesAmount;
    private int paidPurchaseCount;
    private int buyerCount;
    private double averageRating;
    private LocalDateTime lastEpisodeDate;
}
