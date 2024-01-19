package sync.slamtalk.mate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import sync.slamtalk.common.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@Table(name = "matepost")
public class MatePost extends BaseEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "mate_post_id")
        private long matePostId;

//        @ManyToOne(fetch = FetchType.LAZY)
//        @JoinColumn(nullable = false, name="user_id")
        @Column(nullable = false, name="user_id")
        private long userId; // 글 작성자 아이디 * 매핑 필요 (임시로 long으로 설정)

        @Column(nullable = false, name="user_nickname")
        private String userNickname; // 글 작성자 닉네임

        @Column(nullable = true, name="location_detail")
        private String locationDetail; // 상세 시합 장소

        @Column(nullable = false)
        private String title; // 글 제목

        @Column(nullable = false)
        private String content; // 글 내용

        @Column(nullable = false, name="skill_level_type")
        @Enumerated(EnumType.STRING)
        private SkillLevelType skillLevel; // 원하는 스킬 레벨 "BEGINNER", "INTERMEDIATE", "MASTER", "IRRELEVANT"

        @Column(nullable = false, name="scheduled_time")
        private LocalDateTime scheduledTime; // 예정된 시간

        @Column(nullable = true, name="chat_room_id") // 채팅방 아이디 * 매핑 필요
        private long chatRoomId;

        @Column(nullable = false, name="soft_delete")
        private boolean softDelete; // 삭제 여부

        @Column(nullable = false, name="recruitment_status_type")
        @Enumerated(EnumType.STRING)
        private RecruitmentStatusType recruitmentStatus; // 모집 마감 여부 "RECRUITING", "COMPLETED", "CANCEL"

        @Column(nullable = false, name="max_participants")
        private int maxParticipants; // 최대 참여 인원

        @Column(nullable = false, name="current_participants")
        private int currentParticipants; // 현재 참여 인원

        @Column(nullable = false, name="max_participants_forward")
        private int maxParticipantsForwards; // 포워드 최대 참여 인원

        @Column(nullable = false, name="current_participants_forward")
        private int currentParticipantsForwards; // 포워드 현재 참여 인원

        @Column(nullable = false, name="max_participants_center")
        private int maxParticipantsCenters; // 센터 최대 참여 인원

        @Column(nullable = false, name="current_participants_center")
        private int currentParticipantsCenters; // 센터 현재 참여 인원

        @Column(nullable = false, name="max_participants_guard")
        private int maxParticipantsGuards; // 가드 최대 참여 인원

        @Column(nullable = false, name="current_participants_guard")
        private int currentParticipantsGuards; // 가드 현재 참여 인원

        @Column(nullable = false, name="max_participants_others")
        private int maxParticipantsOthers; // 모집 포지션 무관 최대 참여 인원

        @Column(nullable = false, name="current_participants_others")
        private int currentParticipantsOthers; // 모집 포지션 무관 현재 참여 인원

//        @JsonIgnore
//        @OneToMany(mappedBy = "matePost", cascade = CascadeType.ALL)
//        private List<Participant> participants; // 참여자 목록


        public MatePost() {

        }

        @Builder
        public MatePost(long userId, String userNickname, String title, String content, SkillLevelType skillLevel, LocalDateTime scheduledTime, String locationDetail, long chatRoomId, boolean softDelete, RecruitmentStatusType recruitmentStatus, int maxParticipants, int maxParticipantsForwards, int maxParticipantsCenters, int maxParticipantsGuards, int maxParticipantsOthers) {
                this.userId = userId;
                this.userNickname = userNickname;
                this.title = title;
                this.content = content;
                this.skillLevel = skillLevel;
                this.scheduledTime = scheduledTime;
                this.locationDetail = locationDetail;
                this.chatRoomId = chatRoomId;
                this.softDelete = softDelete;
                this.recruitmentStatus = recruitmentStatus;
                this.maxParticipants = maxParticipants;
                this.currentParticipants = 0;
                this.maxParticipantsForwards = maxParticipantsForwards;
                this.currentParticipantsForwards = 0;
                this.maxParticipantsCenters = maxParticipantsCenters;
                this.currentParticipantsCenters = 0;
                this.maxParticipantsGuards = maxParticipantsGuards;
                this.currentParticipantsGuards = 0;
                this.maxParticipantsOthers = maxParticipantsOthers;
                this.currentParticipantsOthers = 0;
        }
}
