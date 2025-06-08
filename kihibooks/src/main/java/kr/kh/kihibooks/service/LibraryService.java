package kr.kh.kihibooks.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import kr.kh.kihibooks.model.vo.EpisodeVO;
import kr.kh.kihibooks.model.vo.InterestVO;
import kr.kh.kihibooks.model.vo.LibraryVO;
import kr.kh.kihibooks.dao.BookDAO;
import kr.kh.kihibooks.dao.LibraryDAO;

@Service
public class LibraryService {
    @Autowired
    LibraryDAO libraryDAO;

    @Autowired
    BookDAO bookDao;
    
    @Value("${spring.path.upload}")
    String uploadPath;

    public List<LibraryVO> getMyBooks(int ur_num) {
        return libraryDAO.selectMyBooks(ur_num);
    }

    public List<InterestVO> getMyInterests(int ur_num) {
        return libraryDAO.selectMyInterests(ur_num);
    }

    public List<InterestVO> getInterestListForPage(int ur_num, int pageSize, int offset) {
        return libraryDAO.selectInterestListForPage(ur_num, pageSize, offset);
    }

    public List<LibraryVO> getBookListForPage(int ur_num, int pageSize, int offset) {
        return libraryDAO.selectBookListForPage(ur_num, pageSize, offset);
    }

    public Resource loadEpubFileAsResource(String epCode) {
        try {
            String epubRelativePath = getEpubFileName(epCode);
            if (epubRelativePath == null) { return null; }

            Path filePath = Paths.get(this.uploadPath).resolve(epubRelativePath).normalize();

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getEpubFileName(String epCode) {
        EpisodeVO episode = bookDao.getEpisodeByCode(epCode);
        return episode != null ? episode.getEp_file_name() : null;
    }
}
