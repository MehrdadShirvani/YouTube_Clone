package Server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;

public class MediaServer {

    private static final int PORT = 2131;
    public static void run()
    {
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        server.createContext("/video", new VideoHandler());
        server.createContext("/image", new PhotoHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + PORT);
    }

    static class PhotoHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();
            String[] parts = requestPath.split("/");
            if(parts.length != 3)
            {
                exchange.sendResponseHeaders(400, 0);
                exchange.close();
                return;
            }

            long photoId;
            try {
                photoId = Long.parseLong(parts[2]);
            }
            catch(Exception ignore) {
                exchange.sendResponseHeaders(400, 0);
                exchange.close();
                return;
            }

            if ("HEAD".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleHeadRequest(exchange, photoId);
            } else if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleGetRequest(exchange, photoId);
            } else {
                exchange.sendResponseHeaders(405, 0);
                exchange.close();
            }
        }

        public byte[] getPhotoBytes(long photoId) {
            File file = new File("src/main/resources/Server/Images/" + photoId + ".png");
            try {
                return Files.readAllBytes(file.toPath());
            } catch (IOException e) {
                return null;
            }
        }

        private void handleHeadRequest(HttpExchange exchange, long photoId) throws IOException {
            byte[] photoBytes = getPhotoBytes(photoId);
            if (photoBytes == null) {
                exchange.sendResponseHeaders(404, 0);
                exchange.close();
                return;
            }

            exchange.getResponseHeaders().set("Content-Type", "image/png");
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        }


        private void handleGetRequest(HttpExchange exchange, long photoId) throws IOException {
            new Thread(() -> {
                try {
                    byte[] photoBytes = getPhotoBytes(photoId);
                    if (photoBytes == null) {
                        exchange.sendResponseHeaders(404, 0);
                        exchange.close();
                        return;
                    }

                    sendFullResponse(exchange, photoBytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        exchange.sendResponseHeaders(500, 0);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    exchange.close();
                }
            }).start();
        }

        private void sendFullResponse(HttpExchange exchange, byte[] photoBytes) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "image/png");
            exchange.getResponseHeaders().set("Content-Length", String.valueOf(photoBytes.length));
            exchange.sendResponseHeaders(200, photoBytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(photoBytes);
            }

            exchange.close();
        }

    }
    static class VideoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();
            String[] parts = requestPath.split("/");
            if(parts.length != 3)
            {
                exchange.sendResponseHeaders(400, 0);
                exchange.close();
                return;
            }
            long videoId;
            try {
                videoId = Long.parseLong(parts[2]);
            }
            catch(Exception ignore) {
                exchange.sendResponseHeaders(400, 0);
                exchange.close();
                return;
            }


            if ("HEAD".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleHeadRequest(exchange, videoId);
            } else if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                handleGetRequest(exchange, videoId);
            } else {
                exchange.sendResponseHeaders(405, 0);
                exchange.close();
            }
        }

        private void handleHeadRequest(HttpExchange exchange, long videoId) throws IOException {

            File videoFile = getVideoFilePath(videoId);

            if (!videoFile.exists() || !videoFile.canRead()) {
                exchange.sendResponseHeaders(404, 0);
                exchange.close();
                return;
            }

            long fileSize = videoFile.length();
            exchange.getResponseHeaders().set("Content-Type", "video/mp4");
            exchange.getResponseHeaders().set("Content-Length", String.valueOf(fileSize));
            exchange.sendResponseHeaders(200, -1);
            exchange.close();
        }

        private File getVideoFilePath(long videoId)
        {
            return new File("src/main/resources/Server/Videos/" + videoId + ".mp4");
        }

        private void handleGetRequest(HttpExchange exchange, long videoId) throws IOException {
            new Thread(() -> {
                try {
                    File videoFile = getVideoFilePath(videoId);
                    if (!videoFile.exists() || !videoFile.canRead()) {
                        exchange.sendResponseHeaders(404, 0);
                        exchange.close();
                        return;
                    }

                    String rangeHeader = exchange.getRequestHeaders().getFirst("Range");
                    if (rangeHeader == null) {
                        sendFullResponse(exchange, videoFile);
                    } else {
                        sendPartialResponse(exchange, videoFile, rangeHeader);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    try {
                        exchange.sendResponseHeaders(500, 0);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    exchange.close();
                }
            }).start();
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
            }

            exchange.close();
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
            exchange.sendResponseHeaders(206, contentLength); // 206 for Partial Content

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
            }

            exchange.close();
        }
    }
}
