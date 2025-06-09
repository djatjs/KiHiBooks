package kr.kh.kihibooks.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.service.GenreService;
import kr.kh.kihibooks.service.KeywordService;

@Controller
public class GenreController {

    @Autowired
    GenreService genreService;

    @Autowired
    KeywordService keywordService;

    @GetMapping("/test")
    public String testPage(Model model) {
        return "genre/genre-template"; // 또는 "genre/rofan"
    }
    @GetMapping("/genre/{genreKeyword}")
    public String genreMain(@PathVariable String genreKeyword, Model model) {
        int genreCode = mapGenreKeywordToCode(genreKeyword);

        System.out.println("✅ [GenreController] genreKeyword = " + genreKeyword + ", genreCode = " + genreCode);

        try {
            List<BookVO> newBooks = genreService.getNewBooksByGenre(genreCode);
            System.out.println("📘 newBooks: " + (newBooks != null ? newBooks.size() : "null"));

            List<BookVO> bestBooks = genreService.getBestBooksByGenre(genreCode);
            System.out.println("🔥 bestBooks: " + (bestBooks != null ? bestBooks.size() : "null"));

            List<BookVO> waitFreeBooks = genreService.getWaitFreeBooksByGenre(genreCode);
            System.out.println("🕒 waitFreeBooks: " + (waitFreeBooks != null ? waitFreeBooks.size() : "null"));

            List<BookVO> realtimeBooks = genreService.getRealtimeRankingByGenre(genreCode);
            System.out.println("📊 realtimeBooks: " + (realtimeBooks != null ? realtimeBooks.size() : "null"));

            List<?> keywordList = keywordService.getRandomKeywordsByGenre(genreCode);
            System.out.println("🔑 keywordList: " + (keywordList != null ? keywordList.size() : "null"));

            model.addAttribute("genreCode", genreCode);
            model.addAttribute("genreName", getGenreName(genreCode));
            model.addAttribute("newBooks", newBooks);
            model.addAttribute("bestBooks", bestBooks);
            model.addAttribute("waitFreeBooks", waitFreeBooks);
            model.addAttribute("realtimeBooks", realtimeBooks);
            model.addAttribute("keywordList", keywordList);
        } catch (Exception e) {
            System.out.println("❌ 예외 발생");
            e.printStackTrace();
            model.addAttribute("error", "장르 데이터를 불러오는 중 오류가 발생했습니다.");
        }

        return "genre/genre-template";
    }

    private int mapGenreKeywordToCode(String keyword) {
        return switch (keyword.toLowerCase()) {
            case "romance" -> 1;
            case "rofan" -> 2;
            case "fantasy" -> 3;
            case "martial" -> 4;
            default -> throw new IllegalArgumentException("❌ Unknown genre keyword: " + keyword);
        };
    }

    private String getGenreName(int genreCode) {
        return switch (genreCode) {
            case 1 -> "로맨스";
            case 2 -> "로판";
            case 3 -> "판타지";
            case 4 -> "무협";
            default -> "기타";
        };
    }

}
