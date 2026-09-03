package kr.kh.kihibooks.model.vo;

import lombok.Data;

@Data
public class PublisherKpiVO {
    private long salesAmount;
    private int paidPurchaseCount;
    private int buyerCount;
    private int reviewCount;
    private double averageRating;
    private int totalBookCount;
    private int publishingBookCount;
    private int completedBookCount;
    private int inactiveBookCount;
    private int unassignedBookCount;
}
