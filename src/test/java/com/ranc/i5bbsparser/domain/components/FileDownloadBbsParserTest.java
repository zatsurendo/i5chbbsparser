package com.ranc.i5bbsparser.domain.components;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FileDownloadBbsParserTest {
    
    private static final Logger log = LoggerFactory.getLogger(FileDownloadBbsParserTest.class);

    @Test
    public void testCheckAndDownload() throws Exception {
        URI uri = new URI("https://d.5chan.jp/file/plane/ZWm8lTw5MB.jpg");
        String uriHost = uri.getHost();
        String threadId = "/W0ySUuqq40";
        log.info("uriHost:  {}", uriHost);

        Path fileName = Paths.get(uri.getPath()).getFileName();
        log.info("fileName: {}", fileName.toString());

        Path fullPathWithoutFileName = Paths.get(uriHost, threadId);
        log.info("parent:   {}", fullPathWithoutFileName);

        Path filePath = Paths.get("D:\\home\\s-nakahori\\myparser", fullPathWithoutFileName.toString(), fileName.toString());
        log.info("filePath: {}", filePath);
    }

    @Test
    public void testFindFiles() throws IOException {
        Path dataDir = Paths.get("D:\\home\\s-nakahori\\myparse");
        FileSystem fs = FileSystems.getDefault();
        String pattern = "**\\0BGcr9RiRO*.jpg";
        PathMatcher pathMatcher = fs.getPathMatcher("glob:" + pattern);
        FileVisitOption opts = FileVisitOption.FOLLOW_LINKS;
        log.info("::testFineFiles()");
        try (Stream<Path> stream = Files.walk(dataDir, 1, opts)) {
            stream.filter(pathMatcher::matches).forEach(System.out::println);
        }
    }

    @Test
    public void testCreateNewPath() {
        Path filePath = Paths.get("D:\\home\\s-nakahori\\myparse\\0BGc123r9RiRO.jpg");
        I5chParser parser = new I5chParser();
        Path newPath = parser.createNewPath(filePath);
        log.info("::testCreateNewPath(): {}", newPath.toString());
    }

    @Test
    public void testDownload() throws IOException {
        Path filePath = Paths.get("D:\\home\\s-nakahori\\myparse\\c.5chan.jp\\AMl1Hx5rib\\98fVriU4Ne.png");
        String sourcePath = "https://c.5chan.jp/file/plane/98fVriU4Ne.png";
        I5chParser parser = new I5chParser();
        for (int i = 0; i < 3 ; i++) {
            int errorCount = parser.download(sourcePath, filePath);
            log.info("::testDownload() - errorCount {}", errorCount);
            if (errorCount < 1) {
                break;
            }
            log.info("::checkAndDownload():Retry downloading({}) from {}", i, sourcePath);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testRangeHeader() throws Exception {
        long contentLength = 8860040;
        URL url = new URL("https://c.5chan.jp/file/1rzT0kKm1v.mp4");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Cache-Control", "no-cache");
        conn.connect();
        Map<String, List<String>> map = conn.getHeaderFields();
        log.info(map.toString());
        log.info("HTTP {} {}", conn.getResponseCode(), conn.getResponseMessage());
        BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
        ReadableByteChannel rbc = Channels.newChannel(bis);
        FileOutputStream fos = new FileOutputStream(Paths.get("D:\\home\\s-nakahori\\myparse\\c.5chan.jp\\AMl1Hx5rib\\1rzT0kKm1v.mp4").toFile());
        long nob = fos.getChannel().transferFrom(rbc, 0, contentLength);
        System.out.println(nob);
        fos.close();
        bis.close();
        conn.disconnect();
    }

    /**
     * https://stackoverflow.com/questions/3428102/how-to-resume-an-interrupted-download-part-2
     * @throws Exception
     */
    @Test
    public void testResumeDownload() throws Exception {
        String url = "https://c.5chan.jp/file/plane/98fVriU4Ne.png";
        Path filePath = Paths.get("D:\\home\\s-nakahori\\myparse\\c.5chan.jp\\AMl1Hx5rib\\98fVriU4Ne.png");
        //long result = resumeDownload(url, downloaded, contentLength, filePath, "");
        long result = normalDownload(url, filePath);
        log.info("Result: {}", result);
    }

    public long normalDownload(String urlSpec, Path filePath) {
        try {
            URL url = new URL(urlSpec);
            FileUtils.copyURLToFile(url, filePath.toFile());
        } catch (MalformedURLException e) {
            String message = MessageFormat.format(
                    "::resumeDownload() - Error while saving from = {0}, to = {1}, message = {2}", urlSpec,
                    filePath.toString(), e.getMessage());
            log.error(message, e);
            e.printStackTrace();
        } catch (IOException e) {
            String message = MessageFormat.format(
                    "::resumeDownload() - Error while saving from = {0}, to = {1}, message = {2}", urlSpec,
                    filePath.toString(), e.getMessage());
            log.error(message, e);
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            String message = MessageFormat.format(
                    "::resumeDownload() - Error while saving from = {0}, to = {1}, message = {2}", urlSpec,
                    filePath.toString(), e.getMessage());
            log.error(message, e);
        }
        return 0;
    }

    public long resumeDownload(String urlSpec, long downloaded, long contentLength, Path filePath, String lastModified) {

        boolean errorOccured = false;
        try {
            URL url = new URL(urlSpec);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            if (downloaded > 0) {
                conn.setRequestProperty("Range", "bytes=" + downloaded + "-");
                conn.setRequestProperty("If-Range", lastModified);
            }
            log.debug("::resumeDownload() - REQUEST {}", conn.getRequestProperties().toString());
            conn.connect();
            if (downloaded == 0) {
                lastModified = conn.getHeaderField("Last-Modified");
            }
            log.debug("::resumeDownload() - Header {}", conn.getHeaderFields().toString());
            try (ReadableByteChannel rbc = Channels.newChannel(conn.getInputStream());
                    FileOutputStream out = new FileOutputStream(filePath.toFile())) {
                FileChannel fileChannel = out.getChannel();
                long writeBytes = fileChannel.transferFrom(rbc, downloaded, Long.MAX_VALUE);
                downloaded += writeBytes;
                log.debug("::resumeDownload() - downloaded {}/{}", downloaded, contentLength);
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalArgumentException(e.getMessage());
            } finally {
                conn.disconnect();
            }
           

            if (downloaded < contentLength) {
                log.debug("::resumeDownload() - resume download {} to {}", urlSpec, filePath.toString());
                downloaded = resumeDownload(urlSpec, downloaded, contentLength, filePath, lastModified);
            }
        } catch (MalformedURLException e) {
            String message = MessageFormat.format(
                    "::resumeDownload() - Error while saving from = {0}, to = {1}, message = {2}", urlSpec,
                    filePath.toString(), e.getMessage());
            log.error(message, e);
            e.printStackTrace();
            errorOccured = true;
        } catch (IOException e) {
            String message = MessageFormat.format(
                    "::resumeDownload() - Error while saving from = {0}, to = {1}, message = {2}", urlSpec,
                    filePath.toString(), e.getMessage());
            log.error(message, e);
            e.printStackTrace();
            errorOccured = true;
        } catch (IllegalArgumentException e) {
            String message = MessageFormat.format(
                    "::resumeDownload() - Error while saving from = {0}, to = {1}, message = {2}", urlSpec,
                    filePath.toString(), e.getMessage());
            log.error(message, e);
            errorOccured = true;
        }
        return errorOccured ? -1 : downloaded;
    }

    @Test
    public void testGetContentLength() throws Exception {

        URL url = new URL("https://c.5chan.jp/file/plane/98fVriU4Ne.png");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        Map<String, List<String>> map = conn.getHeaderFields();
        
        log.debug(map.toString());
        log.debug(String.valueOf(conn.getContentLengthLong()));
        long contentLength = conn.getContentLengthLong();
        if (contentLength < 0) {
            try (BufferedInputStream in = new BufferedInputStream(conn.getInputStream())) {
                byte[] data = in.readAllBytes();
                log.debug("data length {}", data.length);
                contentLength = data.length;
            }
        }
        
        conn.disconnect();
    }
}
