package com.ranc.i5bbsparser.domain.components;

import java.time.LocalDateTime;
import java.util.List;

import com.ranc.i5bbsparser.domain.model.BbsThread;
import com.ranc.i5bbsparser.domain.model.Post;

public interface BbsParser {

    /**
     * ページをパースする
     * <p>インスタンスに予め{@code BbsThread}が設定されていることが前提となる。
     * 設定されていなかったり、設定されていても url が無効な場合は -1 を返す。
     * <p>正常に終了した場合は、整数を返す。
     * @return
     */
    int parse();

    /**
     * {@code url} で指定されたページをパースする
     * <p>ページをパースして、メディアをダウンロードして指定場所に保存し、
     * 保存した件数を返します。
     * @param url
     * @return
     */
    int parse(String url);
    List<Post> parse(BbsThread thread);
    LocalDateTime convertDatetimeString(String dateTimeString);
    void checkAndDownload(String sourceUrl) throws Exception;
}
