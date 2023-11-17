package com.ranc.i5bbsparser.domain.components;

import java.net.URI;

import com.ranc.i5bbsparser.domain.model.Bbs;
import com.ranc.i5bbsparser.domain.model.BbsThread;

public interface BbsParserFactory {
    
    /**
     * {@code typeSpec} から {@code BbsParser} を取得<br>
     * {@code typeSpec} は URI の ホスト部、www.google.co.jp 等を指定する。
     * @param typeSpec 
     * @return {@code BbsParser}
     * @exception IllegalArgumentException {@code BbsParser} を返せない場合
     */
    BbsParser getParser(String typeSpec);

    /**
     * {@code BbsThread} から {@code BbsParser} を取得<br>
     * {@code BbsThread} の {@code url} プロパティが設定されている必要がある。
     * @param bbsThread
     * @return {@code BbsParser}
     * @exception IllegalArgumentException {@code BbsParser} を返せない場合
     */
    BbsParser getParser(BbsThread bbsThread);

    /**
     * {@code Bbs} から {@code BbsParser} を取得<br>
     * {@code Bbs} の {@code typeSpec} が設定されている必要がある。
     * @param bbs
     * @return {@code BbsParser}
     * @exception IllegalArgumentException {@code BbsParser} を返せない場合
     */
    BbsParser getParser(Bbs bbs);

    /**
     * {@code URI} から {@code BbsParser} を取得<br>
     * {@code URI} のホスト部を {@code typeSpec} として{@code BbsParser} を取得。
     * @param uri
     * @return {@code BbsParser}
     * @exception IllegalArgumentException {@code BbsParser} を返せない場合
     */
    BbsParser getParser(URI uri);
}
