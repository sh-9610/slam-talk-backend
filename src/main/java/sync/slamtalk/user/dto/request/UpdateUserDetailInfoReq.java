package sync.slamtalk.user.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import sync.slamtalk.user.entity.UserBasketballPositionType;
import sync.slamtalk.user.entity.UserBasketballSkillLevelType;

@Getter
@AllArgsConstructor
public class UpdateUserDetailInfoReq {
    /* 마이페이지 기능 */
    @Pattern(
            regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9-_]{2,13}$",
            message = "닉네임은 특수문자를 제외한 2~13자리여야 합니다."
    )
    private String nickname;
    private String selfIntroduction;

    /* 정보 수집 부분 */
    private UserBasketballSkillLevelType basketballSkillLevel;
    private UserBasketballPositionType basketballPosition;

}
