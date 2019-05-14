package ru.bpaxio.keeper.fileservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.bpaxio.keeper.fileservice.rest.SaveFileRequest;
import ru.bpaxio.keeper.fileservice.rest.SavingResponse;
import ru.bpaxio.keeper.fileservice.rest.UpdateFileRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class FileService {
    @Value("${file.store.root-dir}")
    private String ROOT_DIR;

    public SavingResponse save(SaveFileRequest request) {
        SavingResponse response = new SavingResponse();
        response.setOriginName(request.getOriginName());
        response.setPath(
                save(LocalDate.now().toString(), UUID.randomUUID().toString(), request.getFile())
        );
        return response;
    }

    public SavingResponse save(UpdateFileRequest updateRequest) {

        SavingResponse response = new SavingResponse();
        response.setOriginName(updateRequest.getOriginName());
        response.setPath(
                update(updateRequest.getDirectory(), updateRequest.getFileName(), updateRequest.getFile())
        );
        return response;
    }

    private String update(String directory, String fileName, MultipartFile file) {
        delete(directory, fileName);
        return save(directory, fileName, file);
    }

    public String save(String dir, String fileName, MultipartFile file) {
        log.info("save {} - contentType: {}, name: {}", file.getOriginalFilename(),
                file.getContentType(), file.getName());
        String queryPath = dir + "/" + fileName;
        File localFile = new File(ROOT_DIR + queryPath);
        try {
            Files.createFile(localFile.toPath());
            file.transferTo(localFile);
        } catch (NoSuchFileException e) {
            try {
                Files.createDirectory(Paths.get(ROOT_DIR + LocalDate.now()));
                return save(dir, fileName, file);
            } catch (IOException e1) {
                log.info("directory can't be created. {}", e1.getMessage());
                throw new RuntimeException("service is broken");
            }
        } catch (IOException e) {
            log.info("failed. {}", e.getMessage());
            e.printStackTrace();
        }
        if (!localFile.exists()) {
            throw new RuntimeException("failed to save File. Try again");
        }
        return queryPath;
    }

    public File get(@NonNull String directory, String fileName) {
        return Optional
                .ofNullable(
                        Paths.get(ROOT_DIR + directory + "/" + fileName)
                                .toFile()
                ).filter(File::exists)
                .orElseThrow(() -> new RuntimeException("not found"));
    }

    public void delete(String directory, String fileName) {
        Path path = Paths.get(ROOT_DIR + directory + "/" + fileName);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.info("failed delete");
        }
    }
}
