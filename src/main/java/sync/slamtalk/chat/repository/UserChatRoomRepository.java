package sync.slamtalk.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sync.slamtalk.chat.entity.RoomType;
import sync.slamtalk.chat.entity.UserChatRoom;

import java.util.List;
import java.util.Optional;

public interface UserChatRoomRepository extends JpaRepository<UserChatRoom, Long> {


    /**
     * UserChatRoom 테이블에서 chatRoomId 로 데이터 조회
     */
    List<UserChatRoom> findByChat_Id(Long chatRoomId);


    /**
     * 특정 User_Id에 해당하는 모든 UserChatRoom 엔터티 조회
     */
    List<UserChatRoom> findByUser_Id(Long userId);


    /**
     * 특정 userId 와 특정 roomId 로 userChatRoom 엔터티 조회
     */
    @Query("select m from UserChatRoom m where m.user.id=:userId and m.chat.id=:roomId")
    Optional<UserChatRoom> findByUserChatroom(Long userId, Long roomId);


    /**
     * 특정 chatRoomName , 특정 chatRoomType 으로 userChatRoom 엔터티 조회
     */
    @Query("select m from UserChatRoom m where m.user.id=:userId and m.chat.name=:roomName and m.chat.roomType=:roomType")
    List<UserChatRoom> findByUserChatroomExist(Long userId, String roomName, RoomType roomType);


    /**
     * TeamMatchingId로 UserChatRoom 조회
     */
    Optional<UserChatRoom> findByTeamMatchingId(Long teamMatchingId);


    /**
     * DirectId로 UserChatRoom 조회
     */
    @Query("select m from UserChatRoom m where m.user.id=:userId and m.directId=:directId")
    List<UserChatRoom> findByDirectId(Long userId, Long directId);


    /**
     * BasketballId로 UserChatRoom 조회
     */
    @Query("SELECT ucr FROM UserChatRoom ucr WHERE ucr.user.id=:userId and ucr.BasketBallId = :basketballId")
    Optional<UserChatRoom> findUserChatRoomByBasketballId(Long userId, @Param("basketballId") Long basketballId);


    /**
     * TogetherId로 UserChatRoom 조회
     */
    Optional<UserChatRoom> findByTogetherId(Long togetherId);
}
