package com.ranc.i5bbsparser.domain.components;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.ranc.i5bbsparser.domain.model.BbsThread;
import com.ranc.i5bbsparser.domain.model.Post;
import com.ranc.i5bbsparser.domain.model.PostImage;
import com.ranc.i5bbsparser.domain.services.BbsThreadService;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@ConfigurationProperties("i5chbbsparser.file-download-bbs-parser")
public abstract class FileDownloadBbsParser implements BbsParser {

    private static final Logger log = LoggerFactory.getLogger(FileDownloadBbsParser.class);
    
    @Autowired
    private BbsThreadService bbsThreadService;

    public static final String DEFAULT_SAVE_PATH = "D:\\myparse";
    /** BBSスレッド */
    private BbsThread bbsThread;
    /** 保存先パス */
    private String savePath = DEFAULT_SAVE_PATH;
    /** スレッドパス */
    private String threadPath = "unknown";
    /** スリープ時間 */
    private long sleepLength = 2000;
    /** ファイル上書きポリシー */
    private FileOverwritePolicy fileOverwritePolicy = FileOverwritePolicy.RENAME;

    public static enum FileOverwritePolicy {
        OVERWRITE,
        RENAME,
        NOOP
    }

    public FileDownloadBbsParser(BbsThread bbsThread) {
        this.bbsThread = bbsThread;
    }

    public void sleep(long millis) {

        try {
            Thread.sleep(millis == 0 ? sleepLength : millis);
        } catch (InterruptedException e) {
            throw new IllegalStateException();
        }
    }

    /**
     * ページをパースする
     * <p>インスタンスに予め{@code BbsThread}が設定されていることが前提となる。
     * 設定されていなかったり、設定されていても url が無効な場合は -1 を返す。
     * <p>正常に終了した場合は、整数を返す。
     * @return int
     */
    @Override
    public int parse() {

        if (this.bbsThread == null || this.bbsThread.getUrl() == null || this.bbsThread.getUrl().isBlank()) {
            log.warn("::parse(): No valid url found in {}", this.bbsThread == null ? "null" : this.bbsThread.toString());
            return -1;
        }
        int resultCount = parse(this.bbsThread.getUrl());
        return resultCount;
    }

    /**
     * {@code url} で指定されたページをパースする
     * <p>ページをパースして、メディアをダウンロードして指定場所に保存し、
     * 保存した件数を返します。
     * @param url
     * @return
     */
    @Override
    public int parse(String url) {
        int resultCount = 0;
        try {
            // URLチェックと、スレッドパスの設定
            URI threadUrl = new URI(url);
            String threadPath = threadUrl.getPath();
            this.threadPath = threadPath.length() > 1 ? threadPath : "unknown";
        } catch (URISyntaxException e) {
            // threadUrl が不正な場合は何もしない
            log.warn("::parse(): caught an URI Syntax exception. (url={})", getBbsThread().getUrl());
            return -1;
        }
        List<Post> result = parseUrl(url);

        if (result != null && !result.isEmpty()) {
            resultCount = save(result);
            log.info("::parse():{} media files saved.", resultCount);
        } else {
            log.info("::parse(): No media files saved from {}.", url);
        }

        if (this.bbsThread != null && url.equalsIgnoreCase(this.bbsThread.getUrl())) {
            bbsThreadService.saveOrUpdate(this.bbsThread);
            log.info("::parse(): bbsThread updated - {}", bbsThread.toString());
        }

        log.info("::parse():Parse for {} finished.", url);
        return resultCount;
    }

    /**
     * URLページをパースする
     * <p>実装クラスにて実装する。
     * 
     * @param url
     * @return
     */
    public abstract List<Post> parseUrl(String url);

    private int save(List<Post> posts) {

        int mediaCount = countMedias(posts);
        int saveCount = 0;
        int skipCount = 0;
        
        if (posts == null || posts.isEmpty()) {
            return 0;
        }

        for (Post post : posts) {
            for (PostImage postImage : post.getPostImages()) {
                sleep(3000);
                try {
                    checkAndDownload(postImage.getUrl());
                    saveCount++;
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("::save():Error while saving: message = {}, url = {}", e.getMessage(), postImage.getUrl());
                    skipCount++;
                }
                log.info("::save() - saving progress .. {}/{} skipcount {}", saveCount, mediaCount, skipCount);
            }
        }
        return saveCount;
    }

    private int countMedias(List<Post> posts) {
        int count = 0;
        for(Post post : posts) {
            count += post.getPostImages().size();
        }
        return count;
    }

    @Override
    public List<Post> parse(BbsThread thread) {
        return parseUrl(thread.getUrl());
    }

    @Override
    public void checkAndDownload(String sourceUrl) throws Exception {
        URI uri = new URI(sourceUrl);
        boolean succeeded = false;
        String uriHost = uri.getHost();
        String threadId = this.threadPath.length() > 1 ? this.threadPath : "unknown";
        Path fileName = Paths.get(uri.getPath()).getFileName();
        Path filePath = Paths.get(this.savePath, uriHost, threadId, fileName.toString());

        log.trace("::checkAndDownload():File Path = {}", filePath);
        // ディレクトリが存在しない場合、作成
        if (!Files.exists(filePath.getParent())) {
            try {
                Files.createDirectories(filePath.getParent());
                log.trace("::checkAndDownload():Directories created: {}", filePath.getParent().toString());
            } catch (IOException e) {
                log.error("::checkAndDownload():ERROR: {} while creating directories: {}", e.getMessage(), filePath.getParent().toString());
                throw new IllegalArgumentException("::checkAndDownload():ERROR: while creating directories.", e);
            }
        } else if (Files.exists(filePath.getParent()) && Files.isRegularFile(filePath.getParent())) {
            log.error("::checkAndDownload(): specified filapath is not a directory. {}", filePath.getParent().toString());
            throw new IllegalArgumentException(MessageFormatter.format("::checkAndDownload(): specified filapath is not a directory. {}", filePath.getParent().toString()).getMessage());
        }
        // 既存ファイルチェック
        if (Files.exists(filePath)) {
            log.debug("::checkAndDownload(): file already exists - {}", filePath.toString());
            switch(this.fileOverwritePolicy) {
                case OVERWRITE:
                    Files.delete(filePath);
                    log.debug("::checkAndDownload(): delete {}", filePath.toString());
                    break;
                case RENAME:
                    filePath = createNewPath(filePath);
                    break;
                case NOOP:
                    log.debug("::checkAndDownload(): skip download media - {}", sourceUrl);
                    return;
            }
        }

        for (int i = 0; i < 3 ; i++) {
            int errorCount = download(sourceUrl, filePath);
            if (errorCount < 1) {
                succeeded = true;
                break;
            }
            log.info("::checkAndDownload():Retry downloading({}) from {}", i, sourceUrl);
            sleep(2000);
            Files.delete(filePath);
        }

        if (!succeeded) {
            log.info("::checkAndDownload():File saving failed. FilePath = {}", filePath.toString());
        }
    }

    public Path createNewPath(Path filePath) {

        String fileName = filePath.getFileName().toString();
        String bodyPart = getBodyPart(fileName);
        String extPart = getExtPart(fileName);
        int count = filesCount(filePath);

        if (count == 0) {
            String message = MessageFormatter.format("::createNewPath(): specified file count = 0 {}", filePath.toString()).getMessage();
            throw new IllegalArgumentException(message);
        }
    
        count++;

        StringBuffer sb = new StringBuffer();
        sb.append(bodyPart).append("(").append(count).append(")");
        if (!extPart.isBlank()) {
            sb.append(".").append(extPart);
        }
        String newFileName = sb.toString();
        Path newPath = Paths.get(filePath.getParent().toString(), newFileName);

        log.debug("::createNewPath(): from = {}", filePath.toString());
        log.debug("::createNewPath(): to = {}", newPath.toString());
        return newPath;
    }

    public int filesCount(Path filePath) {

        // ファイル名からパターンを作成
        String fileName = filePath.getFileName().toString();
        String bodyPart = getBodyPart(fileName);
        String extPart = getExtPart(fileName);
        String pattern = "glob:**\\" + bodyPart + (extPart.isBlank() ? "(*)" : "(*)." + extPart);

        // 指定ディレクトリ直下の該当ファイルを検索
        Path parentPath = filePath.getParent();
        FileSystem fs = FileSystems.getDefault();
        PathMatcher pathMatcher = fs.getPathMatcher(pattern);
        List<Path> pathList = null;
        try (Stream<Path> stream = Files.walk(parentPath, 1, FileVisitOption.FOLLOW_LINKS)) {
            pathList = stream.filter(Files::isRegularFile).filter(pathMatcher::matches).collect(Collectors.toList());
        } catch (IOException e) {
            log.error("::checkAndCreateNew() - catch Exception while find files. path = {}", filePath.toString());
            throw new IllegalArgumentException(e);
        }
        return pathList == null ? 0 : pathList.size();
    }

    /**
     * ファイルネームの拡張子を取得
     * <p>ただし、{@code .} は含まない。
     * 
     * @param fileName
     * @return
     */
    private String getExtPart(String fileName) {
        Optional<String> ext = Optional.of(fileName).filter(value -> value.contains("."))
                .map(value -> value.substring(value.lastIndexOf('.')).replaceFirst(".", ""));
        return ext.isEmpty() ? "" : ext.get();
    }

    /**
     * ファイル名の拡張子意外の本体部分を取得
     * @param fileName
     * @return
     */
    private String getBodyPart(String fileName) {
        Optional<String> body = Optional.of(fileName).filter(value -> value.contains("."))
                .map(value -> value.substring(0, value.lastIndexOf('.')));
        if (body.isEmpty()) {
            return fileName;
        }
        return body.isEmpty() ? "" : body.get();
    }

    /**
     * 
     * @param sourcePath
     * @param filePath
     * @return
     */
    public synchronized int download(String sourcePath, Path filePath) {

        int errorCount = 0;
        try {
            URL u = new URL(sourcePath);
            HttpURLConnection ucon = (HttpURLConnection) u.openConnection();
            ucon.setRequestMethod("GET");
            ucon.setUseCaches(false);
            ucon.connect();
            long contentLength = ucon.getContentLengthLong();

            try (BufferedInputStream in = new BufferedInputStream(ucon.getInputStream());
                    BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(filePath.toFile()))) {
                byte[] buf = new byte[8192];
                long numberOfBytes = 0;
                int count = 0;

                while ((count = in.read(buf)) > 0) {
                    fos.write(buf, 0, count);
                    numberOfBytes += count;
                }

                if (contentLength != -1 && numberOfBytes != contentLength) {
                    String message = MessageFormatter.format(
                            "::download() - download incomplete - only {} could be downloaded against a content length {}",
                            numberOfBytes, contentLength).getMessage();
                    throw new IllegalArgumentException(message);
                }

                log.debug("::download() - save file = {}, number of bytes = {}", filePath.toString(), numberOfBytes);
                fos.flush();
            } catch (IOException e) {
                log.error("Error File saving: from = {} to={}, Message: {}", sourcePath, filePath.toString(), e.getMessage());
                errorCount++;
            } catch (IllegalArgumentException e) {
                log.error("Error File saving: from={} to={}, Message: {}", sourcePath, filePath.toString(), e.getMessage());
                log.error("Download incomplete. retry");
                errorCount++;
            } finally {
                if (errorCount > 0) {
                    if (Files.exists(filePath)) {
                        log.debug("::download() - Delete {}", filePath.toString());
                        Files.delete(filePath);
                    }
                }
            }
            ucon.disconnect();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("::download() - Error while download", e);
        } catch (IOException e) {
            throw new IllegalArgumentException("::download() - Error while download", e);
        }
        return errorCount;
    }

    /**
     * 
     * @param sourcePath
     * @param filePath
     * @return
     */
    public synchronized int downloadOld(String sourcePath, Path filePath) {

        int errorCount = 0;
        try {
            URL u = new URL(sourcePath);
            URLConnection ucon = u.openConnection();
            ucon.setConnectTimeout(60 * 1000);
            long contentLength = ucon.getContentLengthLong();
            log.debug("::download() - Content-Length = {}", contentLength);
            try (InputStream in = new BufferedInputStream(ucon.getInputStream());
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filePath.toFile()))) {
                long numberOfBytes = in.transferTo(out);
                log.debug("::download() - number-of-bytes = {}", numberOfBytes);
                if (numberOfBytes != contentLength) {
                    String message = MessageFormatter.format(
                            "::download() - download incomplete - only {} could be downloaded against a content length {}",
                            numberOfBytes, contentLength).getMessage();
                    throw new IllegalArgumentException(message);
                }
                log.info("File saved: from = {} to={}", sourcePath, filePath.toString());
            } catch (IOException e) {
                log.error("Error File saving: from = {} to={}, Message: {}", sourcePath, filePath.toString(), e.getMessage());
                errorCount++;
            } catch (IllegalArgumentException e) {
                log.error("Error File saving: from={} to={}, Message: {}", sourcePath, filePath.toString(), e.getMessage());
                log.error("Download incomplete. retry");
                errorCount++;
            } catch (UncheckedIOException e) {
                log.error("Error File saving: from={} to={}, Message: {}", sourcePath, filePath.toString(), e.getMessage());
                log.error(e.getMessage());
                errorCount++;
            }
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException();
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }

        return errorCount;
    }



}
