package ru.bpaxio.keeper.fileservice.rest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveFileRequest {
    @NonNull
    private String noteId;
    @NonNull
    private String fileId;
}
