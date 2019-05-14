package ru.bpaxio.keeper.fileservice.rest.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class UpdateFileRequest extends SaveFileRequest {
    @NonNull
    private String path;

    public UpdateFileRequest(@NonNull String noteId, @NonNull String fileId, @NonNull String path) {
        super(noteId, fileId);
        this.path = path;
    }
}
