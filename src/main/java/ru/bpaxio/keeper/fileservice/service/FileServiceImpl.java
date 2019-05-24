package ru.bpaxio.keeper.fileservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.bpaxio.keeper.fileservice.rest.model.SaveFileRequest;
import ru.bpaxio.keeper.fileservice.rest.model.UpdateFileRequest;
import ru.bpaxio.keeper.fileservice.rest.model.UpdateFileResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
public class FileServiceImpl implements FileService {
    private final Path ROOT_DIR;

    public FileServiceImpl(@Value("${file.store.root-path}")String rootPath) {
        ROOT_DIR = Paths.get(System.getProperty("user.dir"), rootPath);
        log.info("root dir: {}", ROOT_DIR);
    }

    @Override
    public UpdateFileResponse save(SaveFileRequest request, MultipartFile file) {
        UpdateFileResponse response = new UpdateFileResponse();
        response.setNoteId(request.getNoteId());
        response.setFileId(request.getFileId());
        response.setOriginName(file.getOriginalFilename());
        response.setPath(
                save(request.getNoteId(), request.getFileId(), file)
        );
        return response;
    }

    @Override
    public UpdateFileResponse update(UpdateFileRequest request, MultipartFile file) {
        delete(request.getPath());
        return save(request, file);
    }

    @Override
    public File getFile(@NonNull String path) {
        return Optional
                .ofNullable(
                        Paths.get(ROOT_DIR.toString(), path)
                                .toFile()
                ).filter(File::exists)
                .orElseThrow(() -> new RuntimeException("not found"));
    }

    @Override
    public void delete(@NonNull String relativePath) {
        Path path = Paths.get(ROOT_DIR.toString(), relativePath);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.info("failed delete");
        }
    }

    private String save(String noteId, String fileId, MultipartFile file) {
        log.info("update {} - contentType: {}, name: {}", file.getOriginalFilename(),
                file.getContentType(), file.getName());
        String relativePath = LocalDate.now().toString() + "/" + noteId + "/" + fileId;
        File localFile = prepareFile(noteId, fileId);
        try {
            file.transferTo(localFile);
        } catch (NoSuchFileException e) {
            log.warn("File can't be transferred. {}", e.getMessage());
            throw new RuntimeException("File can't be transferred.", e);
        } catch (IOException e) {
            log.info("Failed. {}", e.getMessage());
            throw new RuntimeException("Failed.", e);
        }

        if (!localFile.exists()) {
            throw new RuntimeException("Failed to update File. Try again");
        }
        return relativePath;
    }

    private File prepareFile(final String noteId, final String fileId) {
        Path path = Paths.get(ROOT_DIR.toString(), LocalDate.now().toString(), noteId);
        try {
            Files.createDirectories(path);
            File localFile = Paths
                    .get(ROOT_DIR.toString(), LocalDate.now().toString(), noteId, fileId)
                    .toFile();
            return Files.createFile(localFile.toPath()).toFile();
        } catch (IOException e) {
            log.error("failed create directory {}", path);
            throw new RuntimeException("failed create directory " + path, e);
        }
    }

}
