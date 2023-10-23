package org.skillbox.dto;

import org.skillbox.model.Message;

public class MessageMapper {
    public static MessageDto map (Message message){
        MessageDto messageDto = new MessageDto();
        messageDto.setDatetime(message.getDateTime().toString());
        messageDto.setUsername(message.getUser().getName());
        messageDto.setText(message.getMessage());
        return messageDto;
    }
}
