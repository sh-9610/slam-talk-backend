package sync.slamtalk.chat.dto;

import sync.slamtalk.common.ResponseCodeDetails;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

public enum ChatErrorResponseCode implements ResponseCodeDetails {

    // 채팅
    CHAT_FAIL(SC_BAD_REQUEST, 4021, "Failed Chatting"),
    CHAT_LIST_NOT_FOUND(SC_NOT_FOUND, 4022, "ChatList Not Found"),
    CHAT_ROOM_NOT_FOUND(SC_NOT_FOUND, 4023, "ChatRoom Not Found"),

    CHAT_TARGET_NOT_FOUND(SC_NOT_FOUND, 4033, "Partners Not Found"),

    CHAT_ROOM_EXIST_ALREADY(SC_BAD_REQUEST, 4034, "ChatRoom Exist Alradey"),

    CHAT_ROOM_NO_HISTORY_YET(SC_NOT_FOUND, 4035, "ChatRoom History Not Found"),
    ;


    private final int code;
    private final int status;
    private final String message;


    ChatErrorResponseCode(int code, int status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
