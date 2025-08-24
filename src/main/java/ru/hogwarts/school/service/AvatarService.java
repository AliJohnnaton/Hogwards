package ru.hogwarts.school.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AvatarService {
    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    @Value("${avatars.dir.path}")
    private String avatarsDir;

    public AvatarService(AvatarRepository avatarRepository,
                         StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }

    public Avatar uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        // Проверка формата - только PNG
        if (!"image/png".equals(file.getContentType())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Разрешены только PNG файлы. Получен: " + file.getContentType()
            );
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Студент с ID " + studentId + " не найден"
                ));

        Optional<Avatar> existingAvatar = avatarRepository.findByStudentId(studentId);
        if (existingAvatar.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Аватар для студента " + studentId + " уже существует"
            );
        }

        Path uploadDir = Paths.get(avatarsDir);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path filePath = uploadDir.resolve(studentId + ".png");
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Avatar avatar = new Avatar();
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType("image/png");
        avatar.setData(file.getBytes());
        avatar.setStudent(student);

        return avatarRepository.save(avatar);
    }

    @Transactional(readOnly = true)
    public Avatar getAvatarByStudentId(Long studentId) {
        return avatarRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Аватар для студента " + studentId + " не найден"
                ));
    }

    @Transactional(readOnly = true)
    public Avatar getAvatarById(Long avatarId) {
        return avatarRepository.findById(avatarId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Аватар с ID " + avatarId + " не найден"
                ));
    }

    @Transactional(readOnly = true)
    public Page<Avatar> getAllAvatars(Pageable pageable) {
        return avatarRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Avatar> getAvatarsByFileSizeGreaterThan(long minSize) {
        return avatarRepository.findAll().stream()
                .filter(avatar -> avatar.getFileSize() > minSize)
                .toList();
    }

    public Avatar updateAvatar(Long studentId, MultipartFile file) throws IOException {
        if (!"image/png".equals(file.getContentType())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Разрешены только PNG файлы"
            );
        }

        Avatar existingAvatar = avatarRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Аватар для обновления не найден"
                ));

        Path filePath = Paths.get(existingAvatar.getFilePath());
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        existingAvatar.setFileSize(file.getSize());
        existingAvatar.setData(file.getBytes());

        return avatarRepository.save(existingAvatar);
    }

    public void deleteAvatarByStudentId(Long studentId) {
        Avatar avatar = avatarRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Аватар для удаления не найден"
                ));

        try {
            Files.deleteIfExists(Paths.get(avatar.getFilePath()));
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ошибка при удалении файла аватара"
            );
        }

        avatarRepository.delete(avatar);
    }

    public void deleteAvatarById(Long avatarId) {
        Avatar avatar = avatarRepository.findById(avatarId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Аватар с ID " + avatarId + " не найден"
                ));

        try {
            Files.deleteIfExists(Paths.get(avatar.getFilePath()));
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Ошибка при удалении файла аватара"
            );
        }

        avatarRepository.delete(avatar);
    }

    @Transactional(readOnly = true)
    public boolean avatarExistsForStudent(Long studentId) {
        return avatarRepository.existsByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public long getAvatarCount() {
        return avatarRepository.count();
    }

    public byte[] getAvatarData(Long studentId) {
        return getAvatarByStudentId(studentId).getData();
    }

    public Path getAvatarPath(Long studentId) {
        return Paths.get(getAvatarByStudentId(studentId).getFilePath());
    }
}