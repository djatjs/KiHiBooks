package kr.kh.kihibooks.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.model.vo.KeywordVO;
import kr.kh.kihibooks.pagination.PageInfo;
import kr.kh.kihibooks.service.BookService;
import kr.kh.kihibooks.service.GenreService;
import kr.kh.kihibooks.service.KeywordService;

@Controller
public class GenreController {

    @Autowired
    GenreService genreService;

    @Autowired
    BookService bookService;

    @Autowired
    KeywordService keywordService;

    @GetMapping("/genre/{genreName}")
    public String genrePage(@PathVariable("genreName") String genreName,
                            @RequestParam(value = "tab", defaultValue = "main") String tab,
                            @RequestParam(value = "page", defaultValue = "1") int page,
                            @RequestParam(value = "range", defaultValue = "day") String range,
                            @RequestParam(value = "sort", defaultValue = "recent") String sort,
                            @RequestParam(value = "adultYN", required = false) String adultYN,
                            @RequestParam(value = "finished", required = false) String finished,
                            @RequestParam(value = "keyword", required = false) String keyword,
                            HttpServletRequest request,
                            Model model) {

        int mcCode = mapGenreNameToCode(genreName);
        model.addAttribute("mcCode", mcCode);
        model.addAttribute("genreKeyword", genreName);
        model.addAttribute("tab", tab);
        model.addAttribute("currentUri", request.getRequestURI());

        String extraParams = "";
        if (adultYN != null) extraParams += "&adultYN=" + adultYN;
        if (finished != null) extraParams += "&finished=" + finished;
        model.addAttribute("extraParams", extraParams);

        if (tab.equals("new")) {
            PageInfo<BookVO> pageInfo = bookService.getNewBooksByGenre(mcCode, page, sort, adultYN, finished);
            model.addAttribute("pageInfo", pageInfo);
            model.addAttribute("sort", sort);
            model.addAttribute("adultYN", adultYN);
            model.addAttribute("finished", finished);

            System.out.println("finished = " + finished);

            return "genre/genre-template-new";
        }

        if (tab.equals("best")) {
            PageInfo<BookVO> pageInfo = bookService.getBestBooksByGenre(mcCode, range, page, adultYN, finished);
            model.addAttribute("pageInfo", pageInfo);
            model.addAttribute("range", range);
            model.addAttribute("adultYN", adultYN);
            model.addAttribute("finished", finished);
            return "genre/genre-template-best";
        }

        if (tab.equals("wait")) {
            PageInfo<BookVO> pageInfo = bookService.getWaitFreeBooksByGenre(mcCode, page, sort, keyword);
            model.addAttribute("pageInfo", pageInfo);
            model.addAttribute("sort", sort);
            model.addAttribute("keyword", keyword);
            return "genre/genre-template-wait";
        }

        // 기본 탭
        model.addAttribute("realtimeBooks", bookService.getRealtimeRankingBooks(mcCode));
        model.addAttribute("waitFreeBooks", bookService.getWaitFreeBooks(mcCode));
        model.addAttribute("bestBooks", bookService.getBestBooks(mcCode));
        List<BookVO> newBooks = bookService.getNewBooks(mcCode);
        for (BookVO book : newBooks) {
            System.out.println("[NEW] " + book.getBo_code() + " → 썸네일: " + book.getBo_thumbnail());
        }
        model.addAttribute("newBooks", bookService.getNewBooks(mcCode));
        model.addAttribute("keywordList", genreService.getRandomKeywords());

        return "genre/genre-template";
    }



    private int mapGenreNameToCode(String genreName) {
        return switch (genreName.toLowerCase()) {
            case "romance" -> 1;
            case "rofan" -> 2;
            case "fantasy" -> 3;
            case "martial" -> 4;
            default -> 0;
        };
    }
}
