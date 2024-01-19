package sync.slamtalk.chat.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class ChatRoomDTO implements Serializable {
    private String roomId;
    private String name;
}