package kr.kh.kihibooks.model.vo;

import lombok.Data;

@Data
public class PublisherEditorKpiVO {
    private int piNum;
    private String editorName;
    private int bookCount;
    private int publishingBookCount;
    private int inactiveBookCount;
    private long salesAmount;
    private int paidPurchaseCount;
    private int buyerCount;
    private double averageRating;
}
