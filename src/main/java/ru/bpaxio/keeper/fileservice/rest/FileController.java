package ru.bpaxio.keeper.fileservice.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import ru.bpaxio.keeper.fileservice.rest.model.SaveFileRequest;
import ru.bpaxio.keeper.fileservice.rest.model.UpdateFileRequest;
import ru.bpaxio.keeper.fileservice.rest.model.UpdateFileResponse;
import ru.bpaxio.keeper.fileservice.service.FileService;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Paths;

import static ru.bpaxio.keeper.fileservice.util.Utils.putFileTo;

@Slf4j
@RestController
@RequestMapping
@AllArgsConstructor
@Api(value="FilesController", description = "Files REST API")
public class FileController {
    private final FileService service;

    @PostMapping(
            value = "{noteId}/{fileId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ApiOperation("saveFile")
    @ResponseBody
    public UpdateFileResponse saveFile(@PathVariable String noteId,
                                       @PathVariable String fileId,
                                       @RequestPart("file") MultipartFile file) {
        SaveFileRequest request = new SaveFileRequest(noteId, fileId);
        log.info("request {} - file[originalName={}]", request, file.getOriginalFilename());
        return service.save(request, file);
    }

    @PutMapping(
            value = "{date}/{noteId}/{fileId}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ApiOperation("updateFile")
    @ResponseBody
    public UpdateFileResponse updateFile(@PathVariable String date,
                                         @PathVariable String noteId,
                                         @PathVariable String fileId,
                                         @RequestPart("file") MultipartFile file) {
        String path = Paths.get(date, noteId, fileId).toString();
        log.info("update {} - file[originalName={}]", path, file.getOriginalFilename());
        UpdateFileRequest request = new UpdateFileRequest(noteId, fileId, path);
        return service.update(request, file);
    }

    @GetMapping(value = "{date}/{noteId}/{fileId}", produces = MediaType.IMAGE_JPEG_VALUE)
    @ApiOperation("getFile")
    public void getFile(@PathVariable String date,
                        @PathVariable String noteId,
                        @PathVariable String fileId,
                        HttpServletResponse response) {
        String path = Paths.get(date, noteId, fileId).toString();
        log.info("get file[./{}]", path);
        File file = service.getFile(path);
        log.info("file[{}]: {} - {}", file.getName(), file.length(), file.exists());
        putFileTo(file, response);

        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=file");
        response.setHeader(HttpHeaders.CONTENT_LENGTH, Long.toString(file.length()));

    }

    @DeleteMapping("{date}/{noteId}/{fileId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("deleteFile")
    public void deleteFile(@PathVariable String date,
                           @PathVariable String noteId,
                           @PathVariable String fileId) {
        String path = Paths.get(date, noteId, fileId).toString();
        log.info("delete file[./{}]", path);
        service.delete(path);
    }
}
