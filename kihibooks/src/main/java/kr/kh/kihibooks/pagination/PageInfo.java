package kr.kh.kihibooks.pagination;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageInfo<T> {
    private List<T> content;      // 현재 페이지 데이터
    private int currentPage;      // 현재 페이지 번호
    private int totalPages;       // 전체 페이지 수
    private int startPage;        // 페이지네이션 시작 번호
    private int endPage;          // 페이지네이션 끝 번호
}
