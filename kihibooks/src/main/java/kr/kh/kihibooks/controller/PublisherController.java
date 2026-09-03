package kr.kh.kihibooks.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.model.vo.EditorVO;
import kr.kh.kihibooks.model.vo.EpisodeVO;
import kr.kh.kihibooks.model.vo.KeywordCategoryVO;
import kr.kh.kihibooks.model.vo.NoticeVO;
import kr.kh.kihibooks.model.vo.PublisherBookKpiVO;
import kr.kh.kihibooks.model.vo.PublisherDailyKpiVO;
import kr.kh.kihibooks.model.vo.PublisherEditorKpiVO;
import kr.kh.kihibooks.model.vo.PublisherKpiVO;
import kr.kh.kihibooks.model.vo.PublisherVO;
import kr.kh.kihibooks.model.vo.UserVO;
import kr.kh.kihibooks.pagination.PageInfo;
import kr.kh.kihibooks.service.BookService;
import kr.kh.kihibooks.service.KeywordService;
import kr.kh.kihibooks.service.PublisherService;
import kr.kh.kihibooks.service.UserService;
import kr.kh.kihibooks.utils.CustomUser;
import kr.kh.kihibooks.utils.PaginationUtils;


@Controller
public class PublisherController {

    private final BookService bookService;

    @Autowired
    UserService userService;

    @Autowired
    PublisherService publisherService;

    @Autowired
    KeywordService keywordService;

    PublisherController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/publisher/dashboard")
    public String publisherDashboard(@AuthenticationPrincipal CustomUser customUser,
            @RequestParam(value = "days", defaultValue = "30") int requestedDays,
            Model model) {
        int days = requestedDays == 7 || requestedDays == 90 ? requestedDays : 30;
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(days - 1L);
        LocalDateTime startAt = startDate.atStartOfDay();
        LocalDateTime endAt = today.plusDays(1).atStartOfDay();
        LocalDateTime previousStartAt = startAt.minusDays(days);
        LocalDateTime inactiveBefore = today.minusDays(14).atStartOfDay();
        boolean isSuper = "SUPER".equals(customUser.getAuthority());
        Integer piNum = isSuper ? null : customUser.getPi_num();

        PublisherKpiVO kpi = publisherService.getKpiSummary(
                customUser.getPu_code(), piNum, startAt, endAt, inactiveBefore);
        PublisherKpiVO previousKpi = publisherService.getKpiSummary(
                customUser.getPu_code(), piNum, previousStartAt, startAt, inactiveBefore);
        List<PublisherBookKpiVO> topBooks = publisherService.getTopBookKpis(
                customUser.getPu_code(), piNum, startAt, endAt, 5);
        List<PublisherBookKpiVO> managedBooks = isSuper ? List.of() : publisherService.getTopBookKpis(
                customUser.getPu_code(), piNum, startAt, endAt, 100);
        List<PublisherDailyKpiVO> dailyKpis = publisherService.getDailyKpis(
                customUser.getPu_code(), piNum, startAt, endAt);
        PublisherVO publisher = publisherService.getPublisherByCode(customUser.getPu_code());

        long dailyMaxSales = dailyKpis.stream()
                .mapToLong(PublisherDailyKpiVO::getSalesAmount).max().orElse(0);
        long topMaxSales = topBooks.stream()
                .mapToLong(PublisherBookKpiVO::getSalesAmount).max().orElse(0);

        model.addAttribute("publisherName", publisher == null ? "출판사" : publisher.getPu_name());
        model.addAttribute("isSuper", isSuper);
        model.addAttribute("dashboardRole", isSuper ? "SUPER" : "EDITOR");
        model.addAttribute("scopeLabel", piNum == null ? "출판사 전체" : "내 담당 작품");
        model.addAttribute("selectedDays", days);
        model.addAttribute("periodLabel", startDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                + " — " + today.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
        model.addAttribute("lastUpdated", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")));
        model.addAttribute("kpi", kpi);
        model.addAttribute("salesChange", publisherService.calculateChangeRate(kpi.getSalesAmount(), previousKpi.getSalesAmount()));
        model.addAttribute("purchaseChange", publisherService.calculateChangeRate(kpi.getPaidPurchaseCount(), previousKpi.getPaidPurchaseCount()));
        model.addAttribute("buyerChange", publisherService.calculateChangeRate(kpi.getBuyerCount(), previousKpi.getBuyerCount()));
        model.addAttribute("reviewChange", publisherService.calculateChangeRate(kpi.getReviewCount(), previousKpi.getReviewCount()));
        model.addAttribute("ratingChange", Math.round((kpi.getAverageRating() - previousKpi.getAverageRating()) * 10.0) / 10.0);
        model.addAttribute("topBooks", topBooks);
        model.addAttribute("managedBooks", managedBooks);
        model.addAttribute("attentionBooks", managedBooks.stream()
                .filter(book -> book.getLastEpisodeDate() == null
                        || book.getLastEpisodeDate().isBefore(inactiveBefore))
                .toList());
        model.addAttribute("dailyKpis", dailyKpis);
        model.addAttribute("dailyMaxSales", dailyMaxSales);
        model.addAttribute("topMaxSales", topMaxSales);

        if (isSuper) {
            model.addAttribute("editorCount", publisherService.getEditorCount(customUser.getPu_code()));
            List<PublisherEditorKpiVO> editorKpis = publisherService.getEditorKpis(
                    customUser.getPu_code(), startAt, endAt, inactiveBefore);
            long topBookSales = topBooks.stream().mapToLong(PublisherBookKpiVO::getSalesAmount).max().orElse(0);
            double topBookShare = kpi.getSalesAmount() == 0 ? 0
                    : Math.round(topBookSales * 1000.0 / kpi.getSalesAmount()) / 10.0;
            model.addAttribute("editorKpis", editorKpis);
            model.addAttribute("topBookShare", topBookShare);
            model.addAttribute("salesPerBuyer", kpi.getBuyerCount() == 0 ? 0
                    : kpi.getSalesAmount() / kpi.getBuyerCount());
        }
        return "/publisher/publisherDashboard";
    }

    @GetMapping("/publisher/editors")
    public String editors(@RequestParam(value = "page", defaultValue = "1") int page, Model model, Authentication auth) {
        CustomUser user = (CustomUser) auth.getPrincipal();
        String puCode = user.getPu_code();

        int totalCount = publisherService.getEditorCount(puCode); // 전체 수
        // int pageSize = PageConstants.PAGE_SIZE;
        int pageSize = 2;
        // int blockSize = PageConstants.BLOCK_SIZE;
        int blockSize = 2;
        int offset = (page - 1) * pageSize;

        List<EditorVO> editorList = publisherService.getEditorList(puCode, pageSize, offset);
        PageInfo<EditorVO> pageInfo = PaginationUtils.paginate(editorList, totalCount, page, pageSize, blockSize);

        model.addAttribute("pageInfo", pageInfo);
        return "publisher/manageEditors";
    }

    
    @ResponseBody
    @PostMapping("/publisher/searchUser")
    public UserVO searchUser(@RequestBody String searchInput) {
        // searchInput을 DB에서 검색해서 있으면 반환
        UserVO user = userService.getUserByNickName(searchInput);
        if (user == null) {
            return null;
        }
        return user;
    }

    @ResponseBody
    @PostMapping("/publisher/addEditor")
    public boolean addEditor(@RequestParam("userNum") int userNum, @RequestParam("puCode") String puCode) {
        try {
            return publisherService.addEditor(userNum, puCode);
        } catch (Exception e) {
            System.out.println("트랜잭션 실패: " + e.getMessage());
            return false;
        }
    }

    @ResponseBody
    @PostMapping("/publisher/deleteEditor")
    public boolean deleteEditor(@RequestParam("userNum") int userNum) {
        try {
            return publisherService.deleteEditor(userNum);
        } catch (Exception e) {
            System.out.println("트랜잭션 실패: " + e.getMessage());
            return false;
        }
    }

    @GetMapping("/editor/myContent")
    public String myContent(@RequestParam(value = "page", defaultValue = "1") int page, @AuthenticationPrincipal CustomUser customUser, Model model) {
        //등록한 작품 가져오기 (+ 출판사명(publisher), 작가명(author))
        List<BookVO> bookList = bookService.getEditorsBookList(customUser.getPi_num());
        
        int totalCount = bookList.size(); // 전체 수
        // int pageSize = PageConstants.PAGE_SIZE;
        int pageSize = 5;
        // int blockSize = PageConstants.BLOCK_SIZE;
        int blockSize = 5;
        int offset = (page - 1) * pageSize;

        List<BookVO> books = bookService.getEditorsBookListToPage(customUser.getPi_num(), pageSize, offset);
        PageInfo<BookVO> pageInfo = PaginationUtils.paginate(books, totalCount, page, pageSize, blockSize);

        model.addAttribute("pageInfo", pageInfo);


        model.addAttribute("user", customUser.getUser());
        // model.addAttribute("books", books);
        return "/publisher/editor_myContent";
    }
    
    @GetMapping("/editor/registerNew")
    public String registerNewWork(@AuthenticationPrincipal CustomUser customUser, Model model) {
        // System.out.println(customUser);
        //모든 키워드 다 가져오기
        List <KeywordCategoryVO> keywordList = keywordService.getAllKeywordCategories();
        model.addAttribute("user", customUser);
        model.addAttribute("keywordList", keywordList);
        // System.out.println(keywordList);

        return "/publisher/editor_registerNew";
    }

    @PostMapping("/editor/registerNew")
    public String registerNewWorkPost(BookVO book, @RequestParam("bo_keywords") List<String> keywordCodes, @RequestParam("pu_code") String pu_code) {
        System.out.println(book);
        
        if(book == null || book.getBo_author() == null || book.getBo_title() == null || book.getBo_sc_code()== null || book.getBo_title().isBlank()){
            return "redirect:/editor/registerNew";
        }
        //1. 작가
        System.out.println(book.getBo_author());
        int authorNum = bookService.getAuthorNum(book.getBo_author());;
        if(authorNum == 0){
            authorNum = bookService.addAuthor(book.getBo_author());
            if(authorNum == 0){
                return "redirect:/editor/myContent";
            }
        }
        book.setBo_au_num(authorNum);
        //2. 책
        if(!bookService.addBook(book, pu_code)){
            return "redirect:/editor/registerNew";
        }
        // 3. bo_code 다시 받아오기
        String bo_code = bookService.getBookCode(book.getBo_au_num(), book.getBo_title(), book.getBo_pi_num());
        System.out.println(bo_code);

        //4. 책코드와 키워드 리시트를 활용하여 키워드 테이블에 추가
        System.out.println(keywordCodes);
        if(!bookService.addBookKeyword(bo_code, keywordCodes)){
            return "redirect:/editor/registerNew";
        }
        
        return "redirect:/editor/myContent";
    }
    
    @PreAuthorize("@ss.canAccessBook(#bo_code)")
    @GetMapping("/editor/manageEpisode/{bo_code}")
    public String manageEpisodeEpisode(@PathVariable("bo_code") String bo_code, Model model) {
        BookVO book = bookService.getBook(bo_code);
        List<EpisodeVO> epiList = bookService.getEpisodeList(bo_code);
        model.addAttribute("bo_code", bo_code);
        model.addAttribute("book", book);
        model.addAttribute("epiList", epiList);
        return "/publisher/editor_manageEpisode";
    }

    @PreAuthorize("@ss.canAccessBook(#bo_code)")
    @GetMapping("/editor/updateBookInfo/{bo_code}")
    public String updateBookInfo(@AuthenticationPrincipal CustomUser customUser, @PathVariable("bo_code") String bo_code, Model model) {
        BookVO book = bookService.getBook(bo_code);
        List<EditorVO> editors = publisherService.getEditorList(customUser.getPu_code());
        List <KeywordCategoryVO> keywordList = keywordService.getAllKeywordCategories();
        List <KeywordCategoryVO> selectedKeywordList = keywordService.getSelectedKeywordList(bo_code);
        model.addAttribute("user", customUser); //출판사 코드 가져오려고 꼼수 부림
        model.addAttribute("editors", editors); //담당 에디터 이름 가져오려고 씀
        model.addAttribute("book", book); //도서 정보
        model.addAttribute("keywordList", keywordList); //전체 키워드
        model.addAttribute("selectedKeywordList", selectedKeywordList); //선택된 키워드
        return "/publisher/editor_updateBook";
    }
    @PostMapping("/editor/updateBookInfo/{bo_code}")
    public String updateBookInfoPost(@AuthenticationPrincipal CustomUser customUser, @PathVariable("bo_code") String bo_code, @RequestParam("bo_keywords") List<String> bo_keywords, BookVO book, @RequestParam("pu_code") String pu_code)  {
        //받은 값 확인
        System.out.println("선택한 키워드 : "+bo_keywords);
        System.out.println("수정된 도서 정보 : "+book);
        //도서 수정 작업
        if(!bookService.updateBookInfo(book, bo_keywords)){
            return "redirect:/editor/updateBookInfo/"+bo_code;
        }
        return "redirect:/editor/myContent";
    }

    @PreAuthorize("@ss.canAccessBook(#bo_code)")
    @GetMapping("/editor/registerEpisode/{bo_code}")
    public String registerEpisode(@PathVariable("bo_code") String bo_code, Model model) {
        model.addAttribute("bo_code", bo_code);
        return "/publisher/editor_registerEpisode";
    }
    @PostMapping("/editor/registerEpisode/{bo_code}")
    public String registerEpisodePost(@PathVariable("bo_code") String bo_code, EpisodeVO ep, @RequestParam("epubFile") MultipartFile epubFile, @RequestParam("coverImage") MultipartFile coverImage) {
        if(bookService.insertEpisode(ep, bo_code, epubFile, coverImage)){
            return "redirect:/editor/manageEpisode/"+bo_code;
        }
        return "redirect:/editor/registerEpisode/"+bo_code;
    }
    
    @GetMapping("/editor/updateEpisode/{ep_code}")
    public String updateEpisode(@PathVariable("ep_code") String ep_code, Model model) {
        EpisodeVO episode = bookService.getEpisodeByCode(ep_code);
        model.addAttribute("episode", episode);

        return "/publisher/editor_updateEpisode";
    }
    @PostMapping("/editor/updateEpisode/{ep_code}")
    public String updateEpisodePost(@PathVariable("ep_code") String ep_code, EpisodeVO ep, @RequestParam("epubFile") MultipartFile epubFile, @RequestParam("coverImage") MultipartFile coverImage) {
        String bo_code = ep.getEp_bo_code();
        if(bookService.updateEpisode(ep, ep_code, bo_code, epubFile, coverImage)){
            return "redirect:/editor/manageEpisode/"+bo_code;
        }
        return "redirect:/editor/updateEpisode/"+ep_code;
    }

    @ResponseBody
    @PostMapping("/editor/bookFinToY")
    public boolean bookFinToY(@RequestParam("bo_code") String bo_code) {
        if(bo_code == null || bo_code.isEmpty()){return false;}
        return bookService.bookFinToY(bo_code);
    }
    @ResponseBody
    @PostMapping("/editor/bookFinToN")
    public boolean bookFinToN(@RequestParam("bo_code") String bo_code) {
        if(bo_code == null || bo_code.isEmpty()){return false;}
        return bookService.bookFinToN(bo_code);
    }

    @PreAuthorize("@ss.canAccessBook(#bo_code)")
    @GetMapping("/editor/manageNotice/{bo_code}")
    public String myContent(@AuthenticationPrincipal CustomUser customUser,
                            @PathVariable("bo_code") String bo_code,
                            Model model,
                            @RequestParam(value = "page", defaultValue = "1") int page) {
        
        BookVO book = bookService.getBook(bo_code);
        List<EpisodeVO> epiList = bookService.getEpisodeList(bo_code);

        int totalCount = bookService.getNoticeCount(bo_code);
        int pageSize = 5;
        int blockSize = 3;
        int offset = (page - 1) * pageSize;

        List<NoticeVO> noticeList = bookService.getNoticeListForPage(bo_code, pageSize, offset);
        PageInfo<NoticeVO> pageInfo = PaginationUtils.paginate(noticeList, totalCount, page, pageSize, blockSize);

        model.addAttribute("bo_code", bo_code);
        model.addAttribute("book", book);
        model.addAttribute("epiList", epiList);
        model.addAttribute("pageInfo", pageInfo);
        
        return "/publisher/editor_manageNotice";
    }

    @PreAuthorize("@ss.canAccessBook(#bo_code)")
    @GetMapping("/editor/registerNotice/{bo_code}")
    public String registerNotice(@PathVariable("bo_code") String bo_code, @AuthenticationPrincipal CustomUser customUser, Model model) {
        model.addAttribute("pi_num", customUser.getPi_num());
        model.addAttribute("bo_code", bo_code);
        return "/publisher/editor_registerNotice";
    }
    @PostMapping("/editor/registerNotice/{bo_code}")
    public String registerNotice(@PathVariable("bo_code") String bo_code, NoticeVO nt) {
        if(bookService.insertNotice(nt)){
            return "redirect:/editor/manageNotice/"+bo_code;
        }
        return "redirect:/editor/registerNotice/"+bo_code;
    }

    @PreAuthorize("#pu_code == authentication.principal.pu_code")
    @GetMapping("/publisher/manageEditorsBook/{pu_code}")
    public String manageEditorsBook(@PathVariable("pu_code") String pu_code, Model model) {
        List<BookVO> books = bookService.getPublishersBookList(pu_code);
        List<EditorVO> editors = publisherService.getEditorList(pu_code);
        model.addAttribute("books",books);
        model.addAttribute("editors",editors);
        return "/publisher/manageEditorsBook";
    }

    @ResponseBody
    @GetMapping("/publisher/checkHaveBook")
    public boolean checkHaveBook(@RequestParam("userNum") int userNum) {
        return publisherService.checkHaveBook(userNum);
    }
    
    @ResponseBody
    @PostMapping("/publisher/changeEditor")
    public boolean changeEditor (@RequestParam("bo_code") String bo_code, @RequestParam("pi_num") int pi_num) {
        if(bo_code == null || pi_num == 0){
            return false;
        }
        return bookService.changeEditor(bo_code, pi_num);
    }
    @ResponseBody
    @PostMapping("/publisher/keepBook")
    public boolean keepBook (@RequestParam("bo_code") String bo_code, @AuthenticationPrincipal CustomUser customUser) {
        if(bo_code == null){
            return false;
        }
        return bookService.keepBook(bo_code, customUser.getPu_code());
    }
    
    @GetMapping("/editor/updateNotice/{nt_num}")
    public String updateNotice(@PathVariable("nt_num") int nt_num, Model model) {
        NoticeVO notice = bookService.getNotice(nt_num);
        model.addAttribute("notice", notice);
        return "/publisher/editor_updateNotice";
    }
    @PostMapping("/editor/updateNotice/{nt_num}")
    public String updateNoticePost(@PathVariable("nt_num") int nt_num, NoticeVO nt) {
        if(bookService.updateNotice(nt)){
            return "redirect:/editor/manageNotice/"+nt.getNt_bo_code();
        }
        return "redirect:/editor/updateNotice/"+nt_num;
    }

    @ResponseBody
    @PostMapping("/editor/deleteNotice")
    public boolean postMethodName(@RequestParam("nt_num") int nt_num) {
        if(nt_num == 0){
            return false;
        }
        return bookService.deleteNotice(nt_num);
    }
    

}
