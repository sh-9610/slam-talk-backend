package sync.slamtalk.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sync.slamtalk.common.BaseException;
import sync.slamtalk.mate.repository.MatePostRepository;
import sync.slamtalk.user.UserRepository;
import sync.slamtalk.user.dto.UserDetailsInfoResponseDto;
import sync.slamtalk.user.dto.UserUpdateNicknameRequestDto;
import sync.slamtalk.user.dto.UserUpdatePositionAndSkillRequestDto;
import sync.slamtalk.user.entity.User;
import sync.slamtalk.user.error.UserErrorResponseCode;

/**
 * 이 서비스는 유저의 crud 와 관련된 클래스입니다.
 * */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MatePostRepository matePostRepository;

    /* 레벨 시스템을 위한 상수 */
    private final Long LEVEL_THRESHOLD = 50L;
    private final Long MATE_LEVEL_SCORE = 5L;
    /**
     * 유저의 마이페이지 보기 조회시 사용되는 서비스
     *
     *
     * */
    public UserDetailsInfoResponseDto userDetailsInfo(
            Long userId,
            User user
    ) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(UserErrorResponseCode.NOT_FOUND_USER));

        // 레벨 score 계산하기
        long levelScore = 0L;

        // Mate 게시판 상태가 Complete
        long mateCompleteParticipationCount = matePostRepository.findMateCompleteParticipationCount(userId);
        levelScore += mateCompleteParticipationCount * MATE_LEVEL_SCORE;

        // todo : teamMatchingCompleteParticipationCount 팀매칭이 완료된 경우의 개수 세기

        // todo : 출석부 개수 counting 하기

        /* 레벨 단위를 나타내는 변수 */
        long level = levelScore / LEVEL_THRESHOLD;

        // 찾고자 하는 유저가 본인일 경우(상세한 개인정보 까지 공개)
        if(user.getId().equals(findUser.getId())){
            return UserDetailsInfoResponseDto.generateMyProfile(
                    user,
                    level,
                    levelScore,
                    mateCompleteParticipationCount
            );
        }

        // 찾고자 하는 유저가 본인이 아닐경우(개인정보 제외하고 공개)
        else return UserDetailsInfoResponseDto.generateOtherUserProfile(
                user,
                level,
                levelScore,
                mateCompleteParticipationCount
        );
    }

    /**
     * 유저 닉네임 변경 로직
     *
     * @param userId 유저아이디,
     * @param userUpdateNicknameRequestDto 유저 닉네임 변경 request dto
     * */
    @Transactional
    public void userUpdateNickname(
            Long userId,
            UserUpdateNicknameRequestDto userUpdateNicknameRequestDto
    ) {
        log.debug("유저 아이디 "+ userId);
        checkNicknameExistence(userUpdateNicknameRequestDto.getNickname());
        userRepository.updateUserNickname(userId, userUpdateNicknameRequestDto.getNickname());
    }

    /**
     * 회원가입 시 중복 닉네임이 존재하는지 검사하는 메서드
     *
     * @param  nickname 유저 닉네임
     * */

    private void checkNicknameExistence(String nickname) {
        String lowercaseNickname = nickname.toLowerCase();
        if (userRepository.findByNickname(lowercaseNickname).isPresent()) {
            log.debug("이미 존재하는 닉네임입니다.");
            throw new BaseException(UserErrorResponseCode.NICKNAME_ALREADY_EXISTS);
        }
    }

    /**
     *  유저의 스킬 레벨 타입과, 농구 포지션을 업데이트하는 서비스
     *
     * @param userId 유저 아이디
     * @param userUpdatePositionAndSkillRequestDto 유저 레벨타입과, 농구포지션으로 요청이온 dto
     * */
    @Transactional
    public void userUpdatePositionAndSkillLevel(
            Long userId,
            UserUpdatePositionAndSkillRequestDto userUpdatePositionAndSkillRequestDto
    ) {
        userRepository.updateUserPositionAndSkillLevel(
                userId,
                userUpdatePositionAndSkillRequestDto.getBasketballSkillLevel(),
                userUpdatePositionAndSkillRequestDto.getBasketballPosition()
        );
    }
}
