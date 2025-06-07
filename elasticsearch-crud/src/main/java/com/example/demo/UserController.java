package com.example.demo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserController {
    private UserDocumentRepository userDocumentRepository;

    public UserController(UserDocumentRepository userDocumentRepository) {
        this.userDocumentRepository = userDocumentRepository;
    }

    // 사용자 저장
    @PostMapping()
    public UserDocument createUser(@RequestBody UserCreateRequestDto requestDto) {
        UserDocument user = new UserDocument(
                requestDto.getId(),
                requestDto.getName(),
                requestDto.getAge(),
                requestDto.getIsActive()
        );

        return userDocumentRepository.save(user);
    }

    // 전체 사용자 조회
    @GetMapping()
    public Page<UserDocument> findUsers() {
        return userDocumentRepository.findAll(PageRequest.of(0, 10));// of(페이지 번호, 페이지 크기) : 0번 페이지에서 10개의 데이터를 조회

    }

    // 특정 사용자 조회
    @GetMapping("/{id}")
    public UserDocument findUserById(@PathVariable String id) {
        return userDocumentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
    }

    // 특정 사용자 수정
    @PutMapping("/{id}")
    public UserDocument updateUser(@PathVariable String id, @RequestBody UserUpdateRequestDto requestDto) {
        UserDocument existingUser = userDocumentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        existingUser.setAge(requestDto.getAge());
        existingUser.setName(requestDto.getName());
        existingUser.setIsActive(requestDto.getIsActive());

        return userDocumentRepository.save(existingUser);
    }

    // 특정 사용자 삭제
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        UserDocument user = userDocumentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
        userDocumentRepository.delete(user);
    }

}
