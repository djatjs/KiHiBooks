package kr.kh.kihibooks.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.kh.kihibooks.model.vo.EmailVO;
import kr.kh.kihibooks.model.vo.EpisodeVO;
import kr.kh.kihibooks.model.vo.UserVO;

public interface UserDAO {
    boolean insertEV(EmailVO email);

    EmailVO selectEV(String email);

    void deleteEV(String email);

    int selectCode(@Param("email") String email, @Param("userCode")String userCode);

    UserVO selectEmail(@Param("email") String email);

    int selectNickName(@Param("nickName") String nickName);

    boolean insertUserWithPw(UserVO user);

    boolean insertUserWithoutPw(UserVO user);

    boolean updatePw(UserVO user);

    UserVO selectUserByNickName(@Param("nickName")String nickName);

    boolean updateAthourityToPublisher(@Param("userNum")int userNum);

    boolean updateAuthorityToUser(@Param("userNum")int userNum);

    List<EpisodeVO> getCartEpList(int ur_num);

    int deleteCart(int ur_num, String ep_code);
	
}