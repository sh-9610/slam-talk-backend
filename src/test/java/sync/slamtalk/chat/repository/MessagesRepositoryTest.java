package sync.slamtalk.chat.repository;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sync.slamtalk.chat.entity.ChatRoom;
import sync.slamtalk.chat.entity.Messages;
import sync.slamtalk.chat.entity.RoomType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Slf4j
class MessagesRepositoryTest {

    @Autowired
    private MessagesRepository messagesRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private EntityManager entityManager;


    @Test
    void findByChatRoomId() {
        // 테스트 데이터 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .name("TEST ROOM")
                .roomType(RoomType.TOGETHER)
                .build();

        // 테스트 데이터 저장
        entityManager.persist(chatRoom);

        Messages messages1 = Messages.builder()
                .chatRoom(chatRoom)
                .content("안녕ㅎㅎ")
                .creation_time(LocalDateTime.now().toString())
                .build();
        entityManager.persist(messages1);

        Messages messages2 = Messages.builder()
                .chatRoom(chatRoom)
                .content("반가워")
                .creation_time(LocalDateTime.now().toString())
                                        .build();
        entityManager.persist(messages2);

        Optional<ChatRoom> chatroom = chatRoomRepository.findById(chatRoom.getId());

        Assertions.assertThat(chatroom).isPresent();
        Assertions.assertThat(chatroom.get()).isEqualTo(chatRoom);

    }

    @Test
    void findLatestByChatRoomId() {
        //test 데이터 생성
        ChatRoom chatRoomBasket = ChatRoom.builder()
                .roomType(RoomType.BASKETBALL)
                .name("농구장채팅")
                .build();
        ChatRoom chatRoomTogether = ChatRoom.builder()
                .roomType(RoomType.TOGETHER)
                .name("같이하기")
                .build();
        ChatRoom chatRoomDirect = ChatRoom.builder()
                .roomType(RoomType.DIRECT)
                .name("일대일")
                .build();
        //EntityManager 저장
        entityManager.persist(chatRoomBasket);
        entityManager.persist(chatRoomTogether);
        entityManager.persist(chatRoomDirect);


        //test 데이터 생성
        Messages messages1 = Messages.builder()
                .creation_time(LocalDateTime.now().toString())
                .chatRoom(chatRoomBasket)
                .content("농구하자~")
                .build();

        Messages messages2 = Messages.builder()
                .creation_time(LocalDateTime.now().toString())
                .chatRoom(chatRoomBasket)
                .content("그래 몇시에 만나~")
                .build();
        Messages messages3 = Messages.builder()
                .creation_time(LocalDateTime.now().toString())
                .chatRoom(chatRoomDirect)
                .content("안녕하세요 유저입니다")
                .build();
        Messages messages4 = Messages.builder()
                .creation_time(LocalDateTime.now().toString())
                .chatRoom(chatRoomDirect)
                .content("안녕하세요 처음 뵙겠습니다")
                .build();
        Messages messages5 = Messages.builder()
                .creation_time(LocalDateTime.now().toString())
                .chatRoom(chatRoomDirect)
                .content("ㅋㅋㅋㅋ하이욤")
                .build();
        Messages messages6 = Messages.builder()
                .creation_time(LocalDateTime.now().toString())
                .chatRoom(chatRoomBasket)
                .content("4시쯤 어때?")
                .build();
        Messages messages7 = Messages.builder()
                .creation_time(LocalDateTime.now().toString())
                .chatRoom(chatRoomBasket)
                .content("좋지좋지~~!")
                .build();
        //EntityManager 저장
        entityManager.persist(messages1);
        entityManager.persist(messages2);
        entityManager.persist(messages3);
        entityManager.persist(messages4);
        entityManager.persist(messages5);
        entityManager.persist(messages6);
        entityManager.persist(messages7);


        //검증
        // 테스트할 채팅방의 ID
//        Long chatRoomId = chatRoomBasket.getId();
//
//        // PageRequest 생성 (첫 번째 페이지, 페이지 당 한 개의 요소)
//        Pageable pageable = PageRequest.of(0, 1);
//
//        // 가장 최근 메시지 가져오기
//        Page<Messages> latestMessagePage = messagesRepository.findLatestByChatRoomId(chatRoomId, pageable);
//
//        // 결과 검증
//        assertFalse(latestMessagePage.isEmpty(), "결과가 비어있지 않아야 합니다.");
//        assertEquals(1, latestMessagePage.getContent().size(), "정확히 하나의 메시지가 있어야 합니다.");

        // 내림차순(가장최근꺼부터 출력)
        List<Messages> allByChatRoom = messagesRepository.findAllByChatRoom(chatRoomBasket.getId());

        // 오름차순(가장오래된거부터 출력)
        List<Messages> byChatRoomId = messagesRepository.findByChatRoomId(chatRoomBasket.getId());

        Messages messages = allByChatRoom.get(0);
        assertTrue(messages.getId().equals(messages7.getId()));

        for(Messages m : allByChatRoom){
            System.out.println(m.getContent()+m.getCreation_time());
        }


    }
}