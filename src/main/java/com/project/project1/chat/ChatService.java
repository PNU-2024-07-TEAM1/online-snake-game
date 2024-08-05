package com.project.project1.chat;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChatService {
    public static List<String> messages= new ArrayList<>();

    public void addMessage(String message){
        messages.add(message);
    }

    public List<String> getMessages(){
        return messages;
    }
}
