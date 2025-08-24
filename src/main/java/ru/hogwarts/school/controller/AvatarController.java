package ru.hogwarts.school.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;

import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/avatars")
public class AvatarController {
    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @PostMapping(value = "/students/{studentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Avatar> uploadAvatar(
            @PathVariable Long studentId,
            @RequestParam("file") MultipartFile file) {

        try {
            Avatar avatar = avatarService.uploadAvatar(studentId, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(avatar);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ошибка загрузки файла: " + e.getMessage()
            );
        }
    }

    @GetMapping(value = "/students/{studentId}/from-db", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getAvatarDataFromDb(@PathVariable Long studentId) {
        try {
            Avatar avatar = avatarService.getAvatarByStudentId(studentId);
            byte[] data = avatar.getData();

            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(avatar.getMediaType()))
                    .header("Content-Disposition", "inline; filename=\"avatar.png\"")
                    .body(data);

        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(
                    e.getStatusCode(),
                    e.getReason(),
                    e
            );
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Внутренняя ошибка сервера"
            );
        }
    }

    @GetMapping(value = "/students/{studentId}/from-file", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getAvatarDataFromFile(@PathVariable Long studentId) {
        try {
            Avatar avatar = avatarService.getAvatarByStudentId(studentId);
            byte[] data = Files.readAllBytes(avatarService.getAvatarPath(studentId));

            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(avatar.getMediaType()))
                    .header("Content-Disposition", "inline; filename=\"avatar.png\"")
                    .body(data);

        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(
                    e.getStatusCode(),
                    e.getReason(),
                    e
            );
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Файл аватарки не найден: " + e.getMessage()
            );
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ошибка чтения файла"
            );
        }
    }

    @GetMapping("/students/{studentId}")
    public ResponseEntity<Avatar> getAvatarByStudentId(@PathVariable Long studentId) {
        try {
            Avatar avatar = avatarService.getAvatarByStudentId(studentId);
            return ResponseEntity.ok(avatar);
        } catch (ResponseStatusException e) {
            throw e;
        }
    }

    @GetMapping("/{avatarId}")
    public ResponseEntity<Avatar> getAvatarById(@PathVariable Long avatarId) {
        try {
            Avatar avatar = avatarService.getAvatarById(avatarId);
            return ResponseEntity.ok(avatar);
        } catch (ResponseStatusException e) {
            throw e;
        }
    }

    @GetMapping
    public ResponseEntity<Page<Avatar>> getAllAvatars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Avatar> avatars = avatarService.getAllAvatars(PageRequest.of(page, size));
            return ResponseEntity.ok(avatars);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ошибка получения списка аватаров"
            );
        }
    }

    @PutMapping(value = "/students/{studentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Avatar> updateAvatar(
            @PathVariable Long studentId,
            @RequestParam("file") MultipartFile file) {

        try {
            Avatar updatedAvatar = avatarService.updateAvatar(studentId, file);
            return ResponseEntity.ok(updatedAvatar);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ошибка обновления файла"
            );
        }
    }

    @DeleteMapping("/students/{studentId}")
    public ResponseEntity<Void> deleteAvatarByStudentId(@PathVariable Long studentId) {
        try {
            avatarService.deleteAvatarByStudentId(studentId);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            throw e;
        }
    }

    @GetMapping("/students/{studentId}/exists")
    public ResponseEntity<Boolean> checkAvatarExists(@PathVariable Long studentId) {
        try {
            boolean exists = avatarService.avatarExistsForStudent(studentId);
            return ResponseEntity.ok(exists);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ошибка проверки существования аватара"
            );
        }
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"error\": \"" + ex.getReason() + "\"}");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"error\": \"Внутренняя ошибка сервера\"}");
    }
}