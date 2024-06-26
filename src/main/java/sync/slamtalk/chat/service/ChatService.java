package sync.slamtalk.chat.service;

import sync.slamtalk.chat.dto.request.ChatCreateDTO;
import sync.slamtalk.chat.dto.request.ChatMessageDTO;
import sync.slamtalk.chat.dto.response.ChatRoomDTO;
import sync.slamtalk.chat.entity.ChatRoom;
import sync.slamtalk.chat.entity.Messages;
import sync.slamtalk.chat.entity.UserChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatService {


    // 채팅방 생성
    long createChatRoom(ChatCreateDTO chatCreateDTO);


    // 농구장 채팅방 생성
    long createBasketballChatRoom(ChatCreateDTO chatCreateDTO);


    // 메세지 저장
    Long saveMessage(ChatMessageDTO chatMessageDTO);


    // 정상적으로 존재하는 채팅방인지 확인
    Optional<ChatRoom> isExistChatRoom(Long ChatRoomId);


    // 사용자 채팅방에 있는 채팅방인지 확인(구독여부 확인)
    Optional<UserChatRoom> isExistUserChatRoom(Long userId, Long ChatRoomId);


    // 채팅방 나갈때 readIndex 저장
    void saveReadIndex(Long userId, Long chatRoomId, Long readIndex);

    // 특정 방에서 주고 받은 모든 메세지 가져오기
    // 페이징 방식에 의존
    //      1. 사용자가 입장한 시점의 채팅방 메세지부터 모두 내려주기
    //      2. 사용자가 마지막으로 읽은 메세지 이후부터 내려주기
    List<ChatMessageDTO> getChatMessages(Long chatRoomId, Long messageId);


    // 과거 내역 요청
    List<ChatMessageDTO> getPreviousChatMessages(Long userId, Long chatRoomId, Long lastMessageId);


    // 사용자 채팅리스트 가져오기
    List<ChatRoomDTO> getChatLIst(Long userId);


    // 특정방의 가장 마지막 메세지 가져오기
    Messages getLastMessageFromChatRoom(Long chatRoomId);


    // 특정방을 나갈 때 userChatRoom softDelete
    Optional<UserChatRoom> exitRoom(Long userId, Long chatRoomId);


    // 첫방문/재방문 검사
    Optional<Boolean> isVisitedFirst(Long userId, Long roomId);


    // Redis 조회 후 DB 조회
    Optional<List<ChatMessageDTO>> redisFirstDataBaseLater(Long userId, Long chatRoomId, Long lastMessageId);

}
