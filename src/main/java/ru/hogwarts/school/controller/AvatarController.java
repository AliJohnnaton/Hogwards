package ru.hogwarts.school.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/avatars")
public class AvatarController {
    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping("/{studentId}")
    public ResponseEntity<Avatar> uploadAvatar(@PathVariable Long studentId,
                                               @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED).body(avatarService.uploadAvatar(studentId, file));
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<Avatar> getAvatarByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(avatarService.getAvatarByStudentId(studentId));
    }

    @GetMapping("/id/{avatarId}")
    public ResponseEntity<Avatar> getAvatarById(@PathVariable Long avatarId) {
        return ResponseEntity.ok(avatarService.getAvatarById(avatarId));
    }

    @GetMapping
    public ResponseEntity<Page<Avatar>> getAllAvatars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(avatarService.getAllAvatars(PageRequest.of(page, size)));
    }

    @GetMapping("/min-size")
    public ResponseEntity<List<Avatar>> getAvatarsByMinSize(@RequestParam long minSize) {
        return ResponseEntity.ok(avatarService.getAvatarsByFileSizeGreaterThan(minSize));
    }

    @PutMapping("/{studentId}")
    public ResponseEntity<Avatar> updateAvatar(@PathVariable Long studentId,
                                               @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(avatarService.updateAvatar(studentId, file));
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteAvatarByStudent(@PathVariable Long studentId) {
        avatarService.deleteAvatarByStudentId(studentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/id/{avatarId}")
    public ResponseEntity<Void> deleteAvatarById(@PathVariable Long avatarId) {
        avatarService.deleteAvatarById(avatarId);
        return ResponseEntity.noContent().build();
    }
}
