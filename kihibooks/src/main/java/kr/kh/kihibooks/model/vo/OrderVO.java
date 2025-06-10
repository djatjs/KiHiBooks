package kr.kh.kihibooks.model.vo;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class OrderVO {
    private String od_id;
    private int od_total_amount;
    private int od_use_point;
    private int od_final_amount;
    private String od_method;
    private LocalDateTime od_created_at;
    private Timestamp od_paid_at;
    private int od_ur_num;
    private boolean od_isFree;
    
    private List<String> epCodes;
}
