package kr.kh.kihibooks.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.kh.kihibooks.model.vo.BuyListVO;
import kr.kh.kihibooks.model.vo.EmailVO;
import kr.kh.kihibooks.model.vo.EpisodeVO;
import kr.kh.kihibooks.model.vo.OrderListVO;
import kr.kh.kihibooks.model.vo.OrderVO;
import kr.kh.kihibooks.model.vo.UserVO;
import kr.kh.kihibooks.model.vo.WaitForFreeVO;

public interface UserDAO {
    boolean insertEV(EmailVO email);

    EmailVO selectEV(String email);

    void deleteEV(String email);

    int selectCode(@Param("email") String email, @Param("userCode") String userCode);

    UserVO selectEmail(@Param("email") String email);

    UserVO selectEmailIncludeDel(@Param("email") String email);

    int selectNickName(@Param("nickName") String nickName);

    boolean insertUserWithPw(UserVO user);

    boolean insertUserWithoutPw(UserVO user);

    boolean updatePw(UserVO user);

    UserVO selectUserByNickName(@Param("nickName") String nickName);

    boolean updateAthourityToPublisher(@Param("userNum") int userNum);

    boolean updateAuthorityToUser(@Param("userNum") int userNum);

    List<EpisodeVO> getCartEpList(int ur_num);

    int deleteCart(int ur_num, String ep_code);

    WaitForFreeVO getWff(int ur_num, String bo_code);

    int insertBuyList(BuyListVO buyList);

    int selectLastBlNum(int ur_num);

    List<String> getBlEpCodesByUser(int ur_num);

    EpisodeVO getEpisodeByCode(String ep_code);

    boolean updateNickname(int ur_num, String ur_nickname);

    Integer getUrItNum(int ur_num, String bo_code);

    Integer getUrNsNum(int ur_num, String bo_code);

    boolean insertInterest(int ur_num, String bo_code);

    boolean deleteInterest(int ur_num, String bo_code);

    boolean insertNotiSet(int ur_num, String bo_code);

    boolean deleteNotiSet(int ur_num, String bo_code);

    int countTodayOrders();

    void insertOrder(OrderVO order);

    void insertFreeOrder(OrderVO order);

    boolean updateUserDeleted(String ur_email);

    void insertOrderList(OrderListVO order);

    void deleteOrderList(int ur_num);

    List<String> selectOrderList(int ur_num);

    OrderVO selectByOdId(String od_id);

    boolean updatePointOrder(String od_id);

    void updateUsePoint(int ur_num, int usePoint);

    void updateChargePoint(int ur_num, int chargeAmount);

    boolean updateChargeOrder(String od_id);

    List<String> selectEpCodesByBlId(String bl_id);

    List<BuyListVO> selectBuyList(int ur_num);

    List<BuyListVO> selectBList(int ur_num);

    Integer selectTotalByOdId(String bl_id);

    List<String> getBlIds(int ur_num);

    BuyListVO selectBuyListUrNum(int ur_num, String bl_id);

    int selectUrNumByEmail(String ur_email);

    Integer countCart(int ur_num);

    Integer countLib(int ur_num);

    void updatePoint(int ur_num, int totalCredit);

}