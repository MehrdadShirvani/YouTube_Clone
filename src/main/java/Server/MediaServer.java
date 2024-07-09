package Server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.concurrent.Executors;

public class MediaServer {

    private static final int PORT = 2131;

    public static void run() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/video", new VideoHandler());
            server.createContext("/image", new PhotoHandler());
            server.createContext("/download", new DownloadHandler());
            server.createContext("/upload", new UploadHandler());
            server.createContext("/subtitle", new SubtitleHandler());
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            System.out.println("Server started on port " + PORT);
        } catch (IOException e) {
            throw new RuntimeException("Server could not start", e);
        }
    }

    private static File getVideoFilePath(long videoId) {
        return new File("src/main/resources/Server/Videos/" + videoId + ".mp4");
    }

    static class PhotoHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();
            String[] parts = requestPath.split("/");

            if (parts.length != 3) {
                exchange.sendResponseHeaders(400, -1);
                exchange.close();
                return;
            }

            String photoId = parts[2];

            if ("HEAD".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleHeadRequest(exchange, photoId);
            } else if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleGetRequest(exchange, photoId);
            } else {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
            }
        }

        private byte[] getPhotoBytes(String photoId) {
            File file = new File("src/main/resources/Server/Images/" + photoId + ".jpg");
            try {
                return Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                return null;
            }
        }

        private void handleHeadRequest(HttpExchange exchange, String photoId) throws IOException {
            byte[] photoBytes = getPhotoBytes(photoId);
            if (photoBytes == null) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }

            exchange.getResponseHeaders().set("Content-Type", "image/jpeg");
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        }

        private void handleGetRequest(HttpExchange exchange, String photoId) throws IOException {
            byte[] photoBytes = getPhotoBytes(photoId);
            if (photoBytes == null) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }

            sendFullResponse(exchange, photoBytes);
        }

        private void sendFullResponse(HttpExchange exchange, byte[] photoBytes) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "image/jpeg");
            exchange.getResponseHeaders().set("Content-Length", String.valueOf(photoBytes.length));
            exchange.sendResponseHeaders(200, photoBytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(photoBytes);
            } finally {
                exchange.close();
            }
        }
    }
    static class DownloadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                String requestPath = exchange.getRequestURI().getPath();
                String[] parts = requestPath.split("/");

                if (parts.length != 3) {
                    exchange.sendResponseHeaders(400, -1);
                    exchange.close();
                    return;
                }

                Long videoId = Long.parseLong(parts[2]);
                File videoFile = getVideoFilePath(videoId);

                if (videoFile.exists() && videoFile.isFile()) {
                    try (InputStream is = new FileInputStream(videoFile)) {
                        exchange.sendResponseHeaders(200, videoFile.length());
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = is.read(buffer)) != -1) {
                            exchange.getResponseBody().write(buffer, 0, bytesRead);
                        }
                    } catch (IOException e) {
                        exchange.sendResponseHeaders(500, -1); // Internal server error
                    }
                }
                exchange.close();
            }
        }
    }

    static class UploadHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String contentType = exchange.getRequestHeaders().getFirst("Content-Type");
                if(contentType == null ||contentType.split("[|]").length != 2 )
                {
                    exchange.sendResponseHeaders(400, 0);
                    exchange.close();
                    return;
                }


                File file;
                if(contentType.split("[|]")[0].equalsIgnoreCase("image/jpg"))
                {
                    String photoId = (contentType.split("[|]")[1]);
                    String uploadPath = "src/main/resources/Server/Images/" + photoId + ".jpg";
                    file = new File(uploadPath);
                }
                else if(contentType.split("[|]")[0].equalsIgnoreCase("text/vtt"))
                {
                    String vttId = (contentType.split("[|]")[1]);
                    String uploadPath = "src/main/resources/Server/Images/" + vttId + ".vtt";
                    file = new File(uploadPath);
                }
                else {
                    long videoId = Long.parseLong(contentType.split("[|]")[1]);
                    String uploadPath = "src/main/resources/Server/Videos/" + videoId + ".mp4";
                    file = new File(uploadPath);
                }
                try(InputStream is =  exchange.getRequestBody();
                    FileOutputStream fos = new FileOutputStream(file))
                {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }
                    exchange.sendResponseHeaders(200, 0);
                    exchange.close();

            }
        }
    }
    static class SubtitleHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();
            String[] parts = requestPath.split("/");

            if (parts.length != 3) {
                exchange.sendResponseHeaders(400, -1);
                exchange.close();
                return;
            }

            String fileId = parts[2];

            if ("HEAD".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleHeadRequest(exchange, fileId);
            } else if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleGetRequest(exchange, fileId);
            } else {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
            }
        }

        private byte[] getFileBytes(String fileId) {
            File file = new File("src/main/resources/Server/Videos/" + fileId + ".vtt");
            try {
                return Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                return null;
            }
        }

        private void handleHeadRequest(HttpExchange exchange, String fileId) throws IOException {
            byte[] fileBytes = getFileBytes(fileId);
            if (fileBytes == null) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }

            exchange.getResponseHeaders().set("Content-Type", "text/vtt");
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        }

        private void handleGetRequest(HttpExchange exchange, String fileId) throws IOException {
            byte[] fileBytes = getFileBytes(fileId);
            if (fileBytes == null) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }

            sendFullResponse(exchange, fileBytes);
        }

        private void sendFullResponse(HttpExchange exchange, byte[] fileBytes) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "text/vtt");
            exchange.getResponseHeaders().set("Content-Length", String.valueOf(fileBytes.length));
            exchange.sendResponseHeaders(200, fileBytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(fileBytes);
            } finally {
                exchange.close();
            }
        }
    }
    static class VideoHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();
            String[] parts = requestPath.split("/");

            if (parts.length != 3) {
                exchange.sendResponseHeaders(400, -1);
                exchange.close();
                return;
            }

            long videoId;
            try {
                videoId = Long.parseLong(parts[2]);
            } catch (NumberFormatException e) {
                exchange.sendResponseHeaders(400, -1);
                exchange.close();
                return;
            }

            if ("HEAD".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleHeadRequest(exchange, videoId);
            } else if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleGetRequest(exchange, videoId);
            } else {
                exchange.sendResponseHeaders(405, -1);
                exchange.close();
            }
        }

        private void handleHeadRequest(HttpExchange exchange, long videoId) throws IOException {
            File videoFile = getVideoFilePath(videoId);
            if (!videoFile.exists() || !videoFile.canRead()) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }

            long fileSize = videoFile.length();
            exchange.getResponseHeaders().set("Content-Type", "video/mp4");
            exchange.getResponseHeaders().set("Content-Length", String.valueOf(fileSize));
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        }



        private void handleGetRequest(HttpExchange exchange, long videoId) throws IOException {
            File videoFile = getVideoFilePath(videoId);
            if (!videoFile.exists() || !videoFile.canRead()) {
                exchange.sendResponseHeaders(404, -1);
                exchange.close();
                return;
            }

            String rangeHeader = exchange.getRequestHeaders().getFirst("Range");
            if (rangeHeader == null) {
                sendFullResponse(exchange, videoFile);
            } else {
                sendPartialResponse(exchange, videoFile, rangeHeader);
            }
        }

        private void sendFullResponse(HttpExchange exchange, File videoFile) throws IOException {
            long fileSize = videoFile.length();
            exchange.getResponseHeaders().set("Content-Type", "video/mp4");
            exchange.getResponseHeaders().set("Content-Length", String.valueOf(fileSize));
            exchange.sendResponseHeaders(200, fileSize);

            try (OutputStream responseBody = exchange.getResponseBody();
                 FileInputStream fileInputStream = new FileInputStream(videoFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    responseBody.write(buffer, 0, bytesRead);
                }
            } finally {
                exchange.close();
            }
        }

        private void sendPartialResponse(HttpExchange exchange, File videoFile, String rangeHeader) throws IOException {
            long fileSize = videoFile.length();
            String[] ranges = rangeHeader.replace("bytes=", "").split("-");
            long start = Long.parseLong(ranges[0]);
            long end = ranges.length > 1 ? Long.parseLong(ranges[1]) : fileSize - 1;
            if (end >= fileSize) {
                end = fileSize - 1;
            }

            long contentLength = end - start + 1;

            exchange.getResponseHeaders().set("Content-Type", "video/mp4");
            exchange.getResponseHeaders().set("Content-Range", "bytes " + start + "-" + end + "/" + fileSize);
            exchange.getResponseHeaders().set("Content-Length", String.valueOf(contentLength));
            exchange.sendResponseHeaders(206, contentLength);

            try (OutputStream responseBody = exchange.getResponseBody();
                 FileInputStream fileInputStream = new FileInputStream(videoFile)) {

                fileInputStream.skip(start);
                byte[] buffer = new byte[4096];
                long bytesToSend = contentLength;
                int bytesRead;
                while (bytesToSend > 0 && (bytesRead = fileInputStream.read(buffer, 0, (int) Math.min(buffer.length, bytesToSend))) != -1) {
                    responseBody.write(buffer, 0, bytesRead);
                    bytesToSend -= bytesRead;
                }
            } finally {
                exchange.close();
            }
        }
    }
}
