package kr.kh.kihibooks.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.model.vo.KeywordCategoryVO;
import kr.kh.kihibooks.model.vo.KeywordVO;
import kr.kh.kihibooks.model.vo.SubCategoryVO;
import kr.kh.kihibooks.pagination.PageInfo;
import kr.kh.kihibooks.service.BookService;
import kr.kh.kihibooks.service.KeywordService;
import kr.kh.kihibooks.service.UserService;

@Controller
public class BookController {

	@Autowired
	private BookService bookService;
	@Autowired
	private UserService userService;

	@Autowired
	private KeywordService keywordService;

	@GetMapping("/realtime")
	@ResponseBody
	public Map<String, Object> getTopBooks(@RequestParam("mcCode") int mcCode) {
		List<BookVO> books = bookService.getTopBooks(mcCode);
		String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		Map<String, Object> map = new HashMap<>();
		map.put("books", books);
		map.put("lastUpdated", now);

		return map;
	}

	@GetMapping("/book/keyword")
	public String keywordSearchPage(
			@RequestParam(value = "keywordIds", required = false) List<String> keywordIds,
			@RequestParam(defaultValue = "recent") String sort,
			@RequestParam(defaultValue = "1") int page,
			Model model,
			HttpServletRequest request) {
		// 1. 키워드 카테고리 + 키워드 리스트
		List<KeywordCategoryVO> keywordCategories = keywordService.getAllKeywordCategories();
		model.addAttribute("keywordCategories", keywordCategories);
		model.addAttribute("selectedKeywordIds", keywordIds != null ? keywordIds : new ArrayList<>());

		// 2. 검색 결과 도서 리스트
		PageInfo<BookVO> pageInfo = bookService.getBooksByKeywords(keywordIds, sort, page);
		model.addAttribute("bookList", pageInfo.getContent());
		model.addAttribute("pageInfo", pageInfo);
		model.addAttribute("selectedKeywordIds", keywordIds);

		System.out.println("bookList size: " + pageInfo.getContent().size()); // 책 리스트 사이즈 확인
		System.out.println("bookList: " + pageInfo.getContent());

		List<KeywordVO> selectedKeywords = keywordService.getSelectedKeywordsPreserveOrder(keywordIds);
		model.addAttribute("selectedKeywords", selectedKeywords);

		// 3. Ajax 요청 여부 판별 (헤더로 구분)
		String requestedWith = request.getHeader("X-Requested-With");
		if ("XMLHttpRequest".equals(requestedWith)) {
			// Ajax 요청에 대해서만 갱신된 HTML 반환
			return "book/keyword :: bookResultFragment";
		}

		// 최초 접속 or 전체 페이지 렌더링
		return "book/keyword";
	}

	@GetMapping("/book/keyword/updated")
	public String updatedKeywordSearchPage(
			@RequestParam(value = "keywordIds", required = false) List<String> keywordIds,
			@RequestParam(defaultValue = "recent") String sort,
			@RequestParam(defaultValue = "1") int page,
			Model model,
			HttpServletRequest request) {
		// 1. 키워드 카테고리 + 키워드 리스트
		List<KeywordCategoryVO> keywordCategories = keywordService.getAllKeywordCategories();
		model.addAttribute("keywordCategories", keywordCategories);
		model.addAttribute("selectedKeywordIds", keywordIds != null ? keywordIds : new ArrayList<>());

		// 2. 검색 결과 도서 리스트
		PageInfo<BookVO> pageInfo = bookService.getBooksByKeywords(keywordIds, sort, page);
		model.addAttribute("bookList", pageInfo.getContent());
		model.addAttribute("pageInfo", pageInfo);
		model.addAttribute("selectedKeywordIds", keywordIds);

		System.out.println("bookList size: " + pageInfo.getContent().size()); // 책 리스트 사이즈 확인
		System.out.println("bookList: " + pageInfo.getContent());

		List<KeywordVO> selectedKeywords = keywordService.getSelectedKeywordsPreserveOrder(keywordIds);
		model.addAttribute("selectedKeywords", selectedKeywords);

		// Ajax 요청에 대해 fragment만 리턴
		return "book/keyword :: bookResultFragment";
	}

	////////////////////////////////////////// 준호 영역 끝 //////////////////////////////////////////

	@GetMapping("/library/recents")
	public String recentList(Model model, Integer ur_num) {
		List<BookVO> list = bookService.getBookList(ur_num == null ? 0 : ur_num);
		model.addAttribute("bookList", list);
		return "user/recentList";
	}

	@ResponseBody
	@GetMapping("/book/getSubCategory")
	public List<SubCategoryVO> getSubCategory(@RequestParam int mainCategoryValue) {
		if (mainCategoryValue == 0) {
			return null;
		}
		List<SubCategoryVO> subCategories = bookService.getSubCategory(mainCategoryValue);
		if (subCategories != null && !subCategories.isEmpty()) {
			return subCategories;
		}
		return null;
	}
}
