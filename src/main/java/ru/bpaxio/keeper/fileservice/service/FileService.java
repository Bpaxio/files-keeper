package ru.bpaxio.keeper.fileservice.service;

import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;
import ru.bpaxio.keeper.fileservice.rest.model.SaveFileRequest;
import ru.bpaxio.keeper.fileservice.rest.model.UpdateFileRequest;
import ru.bpaxio.keeper.fileservice.rest.model.UpdateFileResponse;

import java.io.File;

public interface FileService {

    UpdateFileResponse save(SaveFileRequest request, MultipartFile file);

    UpdateFileResponse update(UpdateFileRequest path, MultipartFile file);

    File getFile(@NonNull String path);

    void delete(@NonNull String relativePath);
}
