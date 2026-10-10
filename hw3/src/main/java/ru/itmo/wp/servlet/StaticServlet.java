package ru.itmo.wp.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class StaticServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String uri = request.getRequestURI();
        final String[] uriArray = uri.split("\\+");
        final File[] files = new File[uriArray.length];
        for (int i = 0; i < uriArray.length; i++){
            String root = "src/main/webapp/static";
            File file = new File(root, uriArray[i]);
            if (isUnLegalPath(root, file)){
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            if (!file.isFile()) {
                root = getServletContext().getRealPath("/static");
                if (root == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                file = new File(root, uriArray[i]);
                if (isUnLegalPath(root, file)) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
            }
            if (!file.isFile()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            files[i] = file;
        }
        response.setContentType(getServletContext().getMimeType(files[0].getName()));
        try (OutputStream outputStream = response.getOutputStream()) {
            for (File file : files) {
                Files.copy(file.toPath(), outputStream);
            }
        }
    }

    private boolean isUnLegalPath(final String root, File file) throws IOException {
        return !file.getCanonicalFile().toPath().startsWith(new File(root).getCanonicalFile().toPath());
    }
}
