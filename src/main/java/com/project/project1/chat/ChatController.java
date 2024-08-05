package com.project.project1.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
//@RequestMapping("/chat")
public class ChatController {
    private final SimpMessagingTemplate template;
    private final ChatService chatService;

    @GetMapping("/showList")
    public String showList(Model model) {
        List<String> items = Arrays.asList("Item 1", "Item 2", "Item 3", "Item 4");
        model.addAttribute("items", items);
        return "listView"; // Thymeleaf 템플릿 파일명 (listView.html)
    }

    @GetMapping("/api/items")
    public List<String> getItems() {
        // 실제 데이터 리스트를 반환합니다
        return chatService.getMessages();
    }

    @MessageMapping("/updateItems")
    public void updateItems(List<String> items) {
        // 이 메서드는 클라이언트로부터 받은 데이터로 목록을 업데이트하고, 모든 클라이언트에 새로운 목록을 전송합니다.
        template.convertAndSend("/topic/items", items);
    }

    @GetMapping("/test")
    public String test(Model model){
        List<String> messages = chatService.getMessages();
        model.addAttribute("messages", messages);
        return "chat_test";
    }

    @MessageMapping("/sendMessage") // 클라이언트가 /app/sendMessage로 메시지를 보내면
    @SendTo("/topic/messages") // 메시지를 /topic/messages로 브로드캐스팅
    public String sendMessage(String message) {
        chatService.addMessage(message);
        return message; // 수신된 메시지를 그대로 반환
    }
}
