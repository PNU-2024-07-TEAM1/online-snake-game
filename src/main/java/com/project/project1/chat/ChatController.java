package com.project.project1.chat;

import com.project.project1.member.Member;
import com.project.project1.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {
    private final SimpMessagingTemplate template;
    private final MemberService memberService;


    @MessageMapping("/updateItems")
    public void updateItems(List<String> items) {
        // 이 메서드는 클라이언트로부터 받은 데이터로 목록을 업데이트하고, 모든 클라이언트에 새로운 목록을 전송합니다.
        template.convertAndSend("/topic/items", items);
    }


    @MessageMapping("/sendMessage") // 클라이언트가 /app/sendMessage로 메시지를 보내면
    @SendTo("/topic/messages") // 메시지를 /topic/messages로 브로드캐스팅
    public MessageDTO sendMessage(String message, Principal principal) throws Exception {

        if (message.equals("/in")){
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setUsername(principal.getName());
            messageDTO.setContent("이(가) 입장했습니다.");
            return messageDTO;

        }
        Member member = memberService.getMember(principal.getName());

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setUsername(principal.getName());
        messageDTO.setContent(message);
        messageDTO.setColor(member.getColor());
        return messageDTO;
    }
}
