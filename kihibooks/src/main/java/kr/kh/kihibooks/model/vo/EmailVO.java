package kr.kh.kihibooks.model.vo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EmailVO {
    private String ev_ur_email;
    private String ev_code;
    private LocalDateTime ev_expried = LocalDateTime.now().plusMinutes(1);
}
