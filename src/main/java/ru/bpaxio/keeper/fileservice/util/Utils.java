package ru.bpaxio.keeper.fileservice.util;

import ru.bpaxio.keeper.fileservice.util.exception.WrongPathException;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Utils {
    public static void putFileTo(File file, HttpServletResponse response) {
        try {
            Files.copy(file.toPath(), response.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException("Fail to put file into response#outputStream", e);
        }
    }
}
