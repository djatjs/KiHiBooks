package kr.kh.kihibooks.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.kh.kihibooks.model.vo.BookKeywordVO;
import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.model.vo.BuyListVO;
import kr.kh.kihibooks.model.vo.EpisodeVO;
import kr.kh.kihibooks.model.vo.NoticeVO;
import kr.kh.kihibooks.model.vo.ReviewVO;
import kr.kh.kihibooks.model.vo.UserVO;
import kr.kh.kihibooks.model.vo.WaitForFreeVO;
import kr.kh.kihibooks.service.BookService;
import kr.kh.kihibooks.service.UserService;
import kr.kh.kihibooks.utils.CustomUser;

@Controller
public class BookDetailController {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @GetMapping("/books/{bo_code}")
    public String bookDetail(Model model, @PathVariable String bo_code,
            @AuthenticationPrincipal CustomUser customUser) {
        BookVO book = bookService.getBook(bo_code);
        List<EpisodeVO> epiList = bookService.getEpisodeList(bo_code);
        boolean isFree = false;
        for (EpisodeVO ep : epiList) {
            if (ep.getEp_price() == 0) {
                isFree = true;
            }
        }
        List<ReviewVO> rvList = bookService.getReviewList(bo_code);
        Map<Integer, Double> rating = bookService.calcRating(rvList);
        Map<Integer, Integer> replyCountMap = new HashMap<>();
        for (ReviewVO r : rvList) {
            int oriNum = r.getRv_ori_num();
            if (oriNum != 0) { // 댓글이면
                replyCountMap.put(oriNum, replyCountMap.getOrDefault(oriNum, 0) + 1);
            }
        }
        long freeCount = epiList.stream().filter(e -> e.getEp_price() == 0).count();
        Map<Integer, Integer> likeCountMap = new HashMap<>();
        for (ReviewVO review : rvList) {
            int rvNum = review.getRv_num();
            int likeCount = bookService.getLikeCount(rvNum);
            likeCountMap.put(rvNum, likeCount);
        }
        Set<Integer> likedReviewIds = new HashSet<>();

        Optional<Timestamp> latestDateOpt = epiList.stream()
                .map(EpisodeVO::getEp_date)
                .max(Comparator.naturalOrder());
        String latestDate = latestDateOpt
                .map(ts -> new SimpleDateFormat("yyyy.MM.dd").format(ts))
                .orElse("날짜 없음");
        List<BookVO> abList = bookService.getAuthorAnotherBook(bo_code);
        List<NoticeVO> notiList = bookService.getNoticeList(bo_code);
        List<BookVO> bestList10 = bookService.getBestList(bo_code);
        List<BookVO> bestList5 = bookService.getBestList5(bo_code);
        List<BuyListVO> buyList = new ArrayList<>();
        Set<String> buyCodeSet = new HashSet<>();
        List<BookKeywordVO> kwList = bookService.getKeywordList(bo_code);
        WaitForFreeVO wff = new WaitForFreeVO();
        UserVO user = new UserVO();

        if (customUser != null) {
            int ur_num = customUser.getUser().getUr_num();
            likedReviewIds = bookService.getLikedReview(ur_num);
            buyList = bookService.getBuyList(ur_num, bo_code);
            buyCodeSet = buyList.stream().map(BuyListVO::getBl_ep_code).collect(Collectors.toSet());
            wff = userService.getWff(ur_num, bo_code);
            user = customUser.getUser();
            Integer urItNum = userService.getUrItNum(ur_num, bo_code);
            int itNum = urItNum != null ? urItNum : 0;
            user.setUr_it_num(itNum);
            Integer urNsNum = userService.getUrNsNum(ur_num, bo_code);
            int nsNum = urNsNum != null ? urNsNum : 0;
            user.setUr_ns_num(nsNum);
        }

        model.addAttribute("book", book);
        model.addAttribute("epiList", epiList);
        model.addAttribute("rvList", rvList);
        model.addAttribute("rating", rating);
        model.addAttribute("replyCountMap", replyCountMap);
        model.addAttribute("freeCount", freeCount);
        model.addAttribute("likeCountMap", likeCountMap);
        model.addAttribute("likedReviewIds", likedReviewIds);
        model.addAttribute("latestEpDate", latestDate);
        model.addAttribute("abList", abList);
        model.addAttribute("notiList", notiList);
        model.addAttribute("bestList10", bestList10);
        model.addAttribute("bestList5", bestList5);
        model.addAttribute("buyList", buyList);
        model.addAttribute("buyCodeSet", buyCodeSet);
        model.addAttribute("kwList", kwList);
        model.addAttribute("wff", wff);
        model.addAttribute("user", user);
        model.addAttribute("isFree", isFree);

        return "book/detail";
    }

    @PostMapping("/review/insert")
    @ResponseBody
    public boolean insert(@RequestBody ReviewVO review, @AuthenticationPrincipal CustomUser customUser) {

        int reviewCnt = bookService.countReview(review.getRv_bo_code(), customUser.getUser().getUr_num());
        System.out.println(reviewCnt);

        if (reviewCnt == 0) {
            return bookService.insertReview(review, customUser);
        }

        return false;
    }

    @PostMapping("/rereview/insert")
    @ResponseBody
    public ReviewVO insertReview(@RequestBody ReviewVO review, @AuthenticationPrincipal CustomUser customUser) {
        if (!bookService.insertReReview(review, customUser)) {
            System.out.println("리뷰 등록 실패");
            return null;
        }
        System.out.println("리뷰 등록은 성공함");
        ReviewVO reply = bookService.selectReply(review);
        System.out.println("reply: " + reply);
        return reply;
    }

    @GetMapping("/review/sort")
    public String getSortedReview(@RequestParam String sort, @RequestParam("bo_code") String bo_code, Model model) {
        List<ReviewVO> rvList = bookService.getRvList(sort, bo_code);
        System.out.println(rvList);
        model.addAttribute("rvList", rvList);

        return "book/reviewSort :: rvListFlag";
    }

    @PostMapping("/review/like")
    @ResponseBody
    public Map<String, Object> toggleReviewLike(@RequestBody Map<String, Integer> payload,
            @AuthenticationPrincipal CustomUser customUser) {
        int rvNum = payload.get("rv_num");
        int urNum = customUser.getUser().getUr_num();
        System.out.println(rvNum + urNum);
        boolean liked = bookService.toggleLike(rvNum, urNum);
        Integer likeCount = bookService.getLikeCount(rvNum);
        System.out.println(likeCount);
        Map<String, Object> result = new HashMap<>();

        result.put("liked", liked);
        result.put("likeCount", likeCount);
        System.out.println(result);

        return result;
    }

    @PostMapping("/review/delete")
    @ResponseBody
    public boolean deleteReview(int rv_num) {
        System.out.println(rv_num);
        boolean res = bookService.deleteReview(rv_num);
        System.out.println(res);
        return res;
    }

    @PostMapping("/cart/add")
    @ResponseBody
    public boolean addCart(@RequestBody Map<String, List<String>> payload,
            @AuthenticationPrincipal CustomUser customUser) {
        List<String> epCodes = payload.get("epCodes");
        int urNum = customUser.getUser().getUr_num();

        if (epCodes == null || epCodes.isEmpty()) {
            return false;
        }

        return bookService.addCart(urNum, epCodes);
    }

    @PostMapping("/interest/toggle")
    @ResponseBody
    public boolean toggleInterest(@RequestParam String action, @RequestParam String boCode,
            @AuthenticationPrincipal CustomUser customUser) {
        int ur_num = customUser.getUser().getUr_num();

        boolean res = false;

        if ("add".equals(action)) {
            if (userService.insertInterest(ur_num, boCode)) {
                res = userService.insertNotiSet(ur_num, boCode);
            }
        } else {
            if (userService.deleteInterest(ur_num, boCode)) {
                res = userService.deleteNotiSet(ur_num, boCode);
            }
        }

        return res;
    }

    @PostMapping("/alarm/toggle")
    @ResponseBody
    public boolean toggleNOtiSet(@RequestParam String action, @RequestParam String boCode,
            @AuthenticationPrincipal CustomUser customUser) {
        int ur_num = customUser.getUser().getUr_num();

        boolean res = false;

        if ("add".equals(action)) {
            res = userService.insertNotiSet(ur_num, boCode);
        } else {
            res = userService.deleteNotiSet(ur_num, boCode);
        }

        return res;
    }

    @PostMapping("/view/free")
    public boolean viewFree(@RequestBody String epCode, @AuthenticationPrincipal CustomUser customUser) {
        
        int urNum = customUser.getUser().getUr_num();

        if(bookService.insertBuyList(epCode, urNum)) {
            
        }
    }
}
