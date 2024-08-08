package com.project.project1.chat;

import com.project.project1.member.Member;
import com.project.project1.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final SimpMessagingTemplate template;
    private final ChatService chatService;
    private final MemberService memberService;

    @GetMapping("/api/items")
    public List<Message> getItems() {
        // 실제 데이터 리스트를 반환합니다
        return chatService.getMessages();
    }

    @MessageMapping("/updateItems")
    public void updateItems(List<String> items) {
        // 이 메서드는 클라이언트로부터 받은 데이터로 목록을 업데이트하고, 모든 클라이언트에 새로운 목록을 전송합니다.
        template.convertAndSend("/topic/items", items);
    }


    @MessageMapping("/sendMessage") // 클라이언트가 /app/sendMessage로 메시지를 보내면
    @SendTo("/topic/messages") // 메시지를 /topic/messages로 브로드캐스팅
    public MessageDTO sendMessage(String message, Principal principal) throws Exception {

        Message messageEntity;
        if (message.equals("/in")){
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setUsername(principal.getName());
            messageDTO.setContent("이(가) 입장했습니다.");
            return messageDTO;

        }
        Member member = memberService.getMember(principal.getName());
        messageEntity = chatService.makeMessage(
                member,
                message
        );
        chatService.addMessage(messageEntity);

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setUsername(messageEntity.getSender().getUsername());
        messageDTO.setContent(messageEntity.getContent());
        messageDTO.setColor(member.getColor());
        return messageDTO;
    }
}
