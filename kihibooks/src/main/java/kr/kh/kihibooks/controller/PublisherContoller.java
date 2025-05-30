package kr.kh.kihibooks.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import kr.kh.kihibooks.model.vo.KeywordVO;
import kr.kh.kihibooks.model.vo.PublisherVO;
import kr.kh.kihibooks.model.vo.UserVO;
import kr.kh.kihibooks.pagination.PageInfo;
import kr.kh.kihibooks.service.BookService;
import kr.kh.kihibooks.service.KeywordService;
import kr.kh.kihibooks.service.PublisherService;
import kr.kh.kihibooks.service.UserService;
import kr.kh.kihibooks.utils.CustomUser;
import kr.kh.kihibooks.utils.PageConstants;
import kr.kh.kihibooks.utils.PaginationUtils;


@Controller
public class PublisherContoller {

    private final BookService bookService;

    @Autowired
    UserService userService;

    @Autowired
    PublisherService publisherService;

    @Autowired
    KeywordService keywordService;

    PublisherContoller(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/publisher/dashboard")
    public String publisherDashboard() {
        return "/publisher/publisherDashboard";
    }

    @GetMapping("/publisher/editors")
    public String editors(@RequestParam(defaultValue = "1") int page,
                        Model model, Authentication auth) {
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
    public boolean addEditor(@RequestParam int userNum, String puCode) {
        try {
            return publisherService.addEditor(userNum, puCode);
        } catch (Exception e) {
            System.out.println("트랜잭션 실패: " + e.getMessage());
            return false;
        }
    }

    @ResponseBody
    @PostMapping("/publisher/deleteEditor")
    public boolean deleteEditor(@RequestParam int userNum) {
        System.out.println(userNum);
        try {
            return publisherService.deleteEditor(userNum);
        } catch (Exception e) {
            System.out.println("트랜잭션 실패: " + e.getMessage());
            return false;
        }
    }

    @GetMapping("/editor/myContent")
    public String myContent(@AuthenticationPrincipal CustomUser customUser, Model model) {
        //등록한 작품 가져오기 (+ 출판사명(publisher), 작가명(author))
        List<BookVO> books = bookService.getEditorsBookList(customUser.getPi_num());
        //System.out.println(books);
        
        model.addAttribute("user", customUser.getUser());
        model.addAttribute("books", books);
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
    public String registerNewWorkPost(BookVO book, @RequestParam("bo_keywords") List<String> keywordCodes, String pu_code) {
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
    
    @GetMapping("/editor/manageEpisode/{bo_code}")
    public String manageEpisodeEpisode(@PathVariable String bo_code, Model model) {
        BookVO book = bookService.getBook(bo_code);
        List<EpisodeVO> epiList = bookService.getEpisodeList(bo_code);
        model.addAttribute("bo_code", bo_code);
        model.addAttribute("book", book);
        model.addAttribute("epiList", epiList);
        return "/publisher/editor_manageEpisode";
    }

    @GetMapping("/editor/updateBookInfo/{bo_code}")
    public String updateBookInfo(@AuthenticationPrincipal CustomUser customUser, @PathVariable String bo_code, Model model) {
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
    public String updateBookInfoPost(@AuthenticationPrincipal CustomUser customUser, @PathVariable String bo_code, @RequestParam("bo_keywords") List<String> bo_keywords, BookVO book, String pu_code)  {
        //받은 값 확인
        System.out.println("선택한 키워드 : "+bo_keywords);
        System.out.println("수정된 도서 정보 : "+book);
        //도서 수정 작업
        if(!bookService.updateBookInfo(book, bo_keywords)){
            return "redirect:/editor/updateBookInfo/"+bo_code;
        }
        return "redirect:/editor/myContent";
    }

    @GetMapping("/editor/registerEpisode/{bo_code}")
    public String registerEpisode(@PathVariable String bo_code, Model model) {
        model.addAttribute("bo_code", bo_code);
        return "/publisher/editor_registerEpisode";
    }
    @PostMapping("/editor/registerEpisode/{bo_code}")
    public String registerEpisodePost(@PathVariable String bo_code, EpisodeVO ep, MultipartFile epubFile, MultipartFile coverImage) {
        System.out.println(ep);
        if(bookService.insertEpisode(ep, bo_code, epubFile, coverImage)){
            return "redirect:/editor/manageEpisode/"+bo_code;
        }
        return "redirect:/editor/registerEpisode/"+bo_code;
    }
    
    @GetMapping("/editor/updateEpisode/{ep_code}")
    public String updateEpisode(@PathVariable String ep_code, Model model) {
        EpisodeVO episode = bookService.getEpisodeByCode(ep_code);
        System.out.println(episode);
        model.addAttribute("episode", episode);

        return "/publisher/editor_updateEpisode";
    }
    @PostMapping("/editor/updateEpisode/{ep_code}")
    public String updateEpisodePost(@PathVariable String ep_code, EpisodeVO ep, MultipartFile epubFile, MultipartFile coverImage) {
        System.out.println(ep);
        String bo_code = ep.getEp_bo_code();
        if(bookService.updateEpisode(ep, ep_code, bo_code, epubFile, coverImage)){
            return "redirect:/editor/manageEpisode/"+bo_code;
        }
        return "redirect:/editor/updateEpisode/"+ep_code;
    }
    @ResponseBody
    @PostMapping("/editor/deleteEpisode")
    public boolean deleteEpisode(@RequestParam String ep_code) {
        return bookService.deleteEpisode(ep_code);
    }
    
}
