package kr.kh.kihibooks.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.kh.kihibooks.dao.KeywordDAO;
import kr.kh.kihibooks.model.vo.KeywordCategoryVO;
import kr.kh.kihibooks.model.vo.KeywordVO;

@Service
public class KeywordService {

	@Autowired
	KeywordDAO keywordDAO;

	public List<KeywordCategoryVO> getAllKeywordCategories() {
    return keywordDAO.selectKeywordCategories();
	}

	public List<KeywordVO> getSelectedKeywordsPreserveOrder(List<String> keywordIds){
		if(keywordIds == null || keywordIds.isEmpty()){
			return new ArrayList<>();
		}
		return keywordDAO.selectKeywordsByIds(keywordIds);
	}
	
}
