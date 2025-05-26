package kr.kh.kihibooks.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.model.vo.EditorVO;
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
        // System.out.println(customUser);
        model.addAttribute("user", customUser.getUser());
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
    public String registerNewWorkPost(BookVO book, @RequestParam("bo_keywords") List<String> keywordCodes) {
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
        System.out.println("작가번호 : "+authorNum);
        //2. 책
        System.out.println(book.getBo_title());
        //작가 번호 : authorNum
        if(!bookService.addBook(book)){
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
    
    
}
