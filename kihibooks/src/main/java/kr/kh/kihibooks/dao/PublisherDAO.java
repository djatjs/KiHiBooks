package kr.kh.kihibooks.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.kh.kihibooks.model.vo.EditorVO;
import kr.kh.kihibooks.model.vo.PublisherIdVO;
import kr.kh.kihibooks.model.vo.PublisherBookKpiVO;
import kr.kh.kihibooks.model.vo.PublisherDailyKpiVO;
import kr.kh.kihibooks.model.vo.PublisherEditorKpiVO;
import kr.kh.kihibooks.model.vo.PublisherKpiVO;
import kr.kh.kihibooks.model.vo.PublisherVO;

public interface PublisherDAO {

    PublisherVO selectPublisher(String pu_name);
    
    boolean insertPublisher(String pu_name, String pu_code);

    List<PublisherVO> selectAllPublishers();

    PublisherIdVO selectPublisherId(PublisherIdVO publisherId);
    
    boolean insertPublisherId(PublisherIdVO publisherId);

    PublisherIdVO selectPublisherIdByNum(int ur_num);

    boolean insertEditor(@Param("userNum")int userNum, @Param("puCode")String puCode);

    String getLatestPuCode();

    boolean deleteEditorByUserNum(int userNum);

    int countEditor(String puCode);

    List<EditorVO> selectEditorList(@Param("puCode") String puCode, @Param("limit") int limit, @Param("offset") int offset);

    int selectEditorCount(@Param("puCode") String puCode);

    List<EditorVO> selectEditors(String pu_code);

    int countEditorHaveBook(int userNum);

    int selectSuperNum(String pu_code);

    PublisherVO selectPublisherByCode(String puCode);

    PublisherKpiVO selectKpiSummary(@Param("puCode") String puCode,
            @Param("piNum") Integer piNum,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt,
            @Param("inactiveBefore") LocalDateTime inactiveBefore);

    List<PublisherBookKpiVO> selectTopBookKpis(@Param("puCode") String puCode,
            @Param("piNum") Integer piNum,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt,
            @Param("limit") int limit);

    List<PublisherDailyKpiVO> selectDailyKpis(@Param("puCode") String puCode,
            @Param("piNum") Integer piNum,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt);

    List<PublisherEditorKpiVO> selectEditorKpis(@Param("puCode") String puCode,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt,
            @Param("inactiveBefore") LocalDateTime inactiveBefore);
}
