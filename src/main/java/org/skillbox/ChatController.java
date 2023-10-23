package org.skillbox;

import org.skillbox.dto.MessageDto;
import org.skillbox.dto.MessageMapper;
import org.skillbox.model.Message;
import org.skillbox.model.MessageRepository;
import org.skillbox.model.User;
import org.skillbox.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class ChatController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/api/init")
    public HashMap<String, Boolean> init()
    {
        HashMap<String, Boolean> response = new HashMap<>();
        String sessionId = RequestContextHolder
            .currentRequestAttributes().getSessionId();
        Optional<User> userOpt =
            userRepository.findBySessionId(sessionId);
        response.put("result", userOpt.isPresent());
        return response;
    }

    @PostMapping("/api/auth")
    public HashMap<String, Boolean> auth(@RequestParam String name)
    {
        HashMap<String, Boolean> response = new HashMap<>();
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        User user = new User();
        user.setName(name);
        user.setSessionId(sessionId);
        userRepository.save(user);
        response.put("result", true);
        return response;
    }

    @PostMapping("/api/message")
    public Map<String, Boolean> sendMessage(@RequestParam String message)
    {
        if (message.isEmpty()) {
            return Map.of("result", false);
        }
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        User user = userRepository.findBySessionId(sessionId).get();

        Message msg = new Message();
        msg.setDateTime(LocalDateTime.now());
        msg.setMessage(message);
        msg.setUser(user);
        messageRepository.saveAndFlush(msg);
        return Map.of("result", true);
    }

    @GetMapping("/api/message")
    public List<MessageDto> getMessagesList()
    {
        return messageRepository
                .findAll(Sort.by(Sort.Direction.ASC, "dateTime"))
                .stream()
                .map(message -> MessageMapper.map(message))
                .collect(Collectors.toList());
    }

    @GetMapping("/api/users")
    public List<String> getUsersList() {
        return userRepository
                .findAll()
                .stream()
                .map(user -> user.getName())
                .collect(Collectors.toList());
    }
}
