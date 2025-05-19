package kr.kh.kihibooks.utils;

import java.util.List;

import kr.kh.kihibooks.pagination.PageInfo;

public class PaginationUtils {
	/**
     * 페이지네이션 정보 생성 메서드
     *
     * @param <T>         리스트 제네릭 타입
     * @param content     현재 페이지에 보여줄 데이터 리스트
     * @param totalCount  전체 데이터 수
     * @param page        현재 페이지 번호
     * @param pageSize    한 페이지당 아이템 수
     * @param blockSize   페이지네이션 블럭 수 (ex: 5 → 1 2 3 4 5)
     * @return PageInfo<T>
     */
    
		 public static <T> PageInfo<T> paginate(List<T> content, int totalCount, int page, int pageSize, int blockSize) {
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        int startPage = ((page - 1) / blockSize) * blockSize + 1;
        int endPage = Math.min(startPage + blockSize - 1, totalPages);

        return new PageInfo<>(content, page, totalPages, startPage, endPage);
    }
		
}
