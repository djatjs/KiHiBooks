package kr.kh.kihibooks.model.vo;
import lombok.Data;

@Data
public class PublisherVO {
    private String pu_code; //db에서 자동으로 생성되도록 설정. 1부터 autoincrement 느낌으로.. 4자리로 둘 예정
    private String pu_name;
    private String ur_email;    // 유저 이메일 (조인해서 가져옴)
}
