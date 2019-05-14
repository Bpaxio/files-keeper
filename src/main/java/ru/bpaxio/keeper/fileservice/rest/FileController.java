package ru.bpaxio.keeper.fileservice.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.bpaxio.keeper.fileservice.service.FileService;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
@RestController
@RequestMapping("files")
@AllArgsConstructor
@Api(value="FilesController", description = "Files REST API")
public class FileController {
    private final FileService service;


    @PostMapping(
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ApiOperation("saveFile")
    @ResponseBody
    public SavingResponse saveFile(@RequestPart("file") MultipartFile file) {
        log.info("received file[originalName={}]",
                file.getOriginalFilename());
        return service.save(new SaveFileRequest(file.getOriginalFilename(), file));
    }

    @PutMapping(
            value = "{dir}/{fileName}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ApiOperation("saveFile")
    @ResponseBody
    public SavingResponse updateFile(@PathVariable("dir") String directory,
                                     @PathVariable("fileName") String fileName,
                                     @RequestPart("file") MultipartFile file) {
        log.info("received file[originalName={}]",
                file.getOriginalFilename());
        return service.save(new UpdateFileRequest(file.getOriginalFilename(), directory, fileName, file));
    }

    @GetMapping(value = "{dir}/{fileName}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ApiOperation("getFile")
    public void getFile(@PathVariable("dir") String directory,
                        @PathVariable("fileName") String fileName,
                        HttpServletResponse response) {
        log.info("get file[/{}/{}]", directory, fileName);
        File file = service.get(directory, fileName);
        log.info("file[{}]: {} - {}", file.getName(), file.length(), file.exists());
        try {
            Files.copy(file.toPath(), response.getOutputStream());
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=file");
            response.setHeader(HttpHeaders.CONTENT_LENGTH, Long.toString(file.length()));
        } catch (IOException e) {
            throw new RuntimeException("fail to stream it");
        }
    }

    @DeleteMapping(value = "{dir}/{fileName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("deleteFile")
    public void deleteFile(@PathVariable("dir") String directory,
                           @PathVariable("fileName") String fileName) {
        log.info("delete file[/{}/{}]", directory, fileName);
        service.delete(directory, fileName);
    }
}
