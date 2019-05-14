package ru.bpaxio.keeper.fileservice.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFileRequest {
    private String originName;
    private String directory;
    private String fileName;
    private MultipartFile file;
}
