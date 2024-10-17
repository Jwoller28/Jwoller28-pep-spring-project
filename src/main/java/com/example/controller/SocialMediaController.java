package com.example.controller;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class SocialMediaController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageService messageService;

    @PostMapping("/register")
    public ResponseEntity<Account> registerAccount(@RequestBody Account account) {
        Optional<Account> existingAccount = accountService.findByUsername(account.getUsername());
        if (existingAccount.isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        if (account.getPassword().length() < 4 || account.getUsername().isBlank()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Account registeredAccount = accountService.registerAccount(account);
        return new ResponseEntity<>(registeredAccount, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account account) {
        Optional<Account> foundAccount = accountService.findByUsername(account.getUsername());
        if (foundAccount.isPresent() && foundAccount.get().getPassword().equals(account.getPassword())) {
            return new ResponseEntity<>(foundAccount.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @PostMapping("/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        if (message.getMessageText().isBlank() || message.getMessageText().length() > 255) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (accountService.findById(message.getPostedBy()).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Message createdMessage = messageService.createMessage(message);
        return new ResponseEntity<>(createdMessage, HttpStatus.OK);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageService.getAllMessages();
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable Integer messageId) {
        Optional<Message> message = messageService.getMessageById(messageId);
        return message.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                      .orElseGet(() -> new ResponseEntity<>(HttpStatus.OK));
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Integer> deleteMessageById(@PathVariable Integer messageId) {
        if (messageService.getMessageById(messageId).isPresent()) {
            messageService.deleteMessageById(messageId);
            return new ResponseEntity<>(1, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<Integer> updateMessage(@PathVariable Integer messageId, @RequestBody Message updatedMessage) {
        if (updatedMessage.getMessageText().isBlank() || updatedMessage.getMessageText().length() > 255) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<Message> existingMessage = messageService.getMessageById(messageId);
        if (existingMessage.isPresent()) {
            Message messageToUpdate = existingMessage.get();
            messageToUpdate.setMessageText(updatedMessage.getMessageText());
            messageService.createMessage(messageToUpdate);
            return new ResponseEntity<>(1, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getMessagesByUserId(@PathVariable Integer accountId) {
        List<Message> messages = messageService.getMessagesByUserId(accountId);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }
}
