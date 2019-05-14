package ru.bpaxio.keeper.fileservice.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFileResponse {
    @NonNull
    private String path;
    @NonNull
    private String noteId;
    @NonNull
    private String fileId;
    @NonNull
    private String originName;
}
