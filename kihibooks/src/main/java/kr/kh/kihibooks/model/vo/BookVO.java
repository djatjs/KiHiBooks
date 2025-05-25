package kr.kh.kihibooks.model.vo;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

@Data
public class BookVO {
    private String bo_code;
    private String bo_title;
    private String bo_description;
    private String bo_fin;
    private int bo_total_episode;
    private int bo_total_rating;
    private int bo_review_count;
    private int bo_free_episode;
    private String bo_serial_schedule;
    private String bo_wait_for_free;
    private int bo_wff_date;
    private Timestamp bo_publish_date;
    private String bo_adult;
    private String bo_sc_code;
    private int bo_pi_num;
    private int bo_au_num;
    private String bo_author;
    private String bo_publisher;
    private String bo_main_cate;
    private String bo_sub_cate;
    
    private List<KeywordVO> keywordList;
}
