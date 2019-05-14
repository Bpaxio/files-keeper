package ru.bpaxio.keeper.fileservice.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveFileRequest {
    private String originName;
    private MultipartFile file;
}
