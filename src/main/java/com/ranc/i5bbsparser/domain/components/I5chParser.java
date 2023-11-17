package com.ranc.i5bbsparser.domain.components;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.stereotype.Component;

import com.ranc.i5bbsparser.domain.model.BbsThread;
import com.ranc.i5bbsparser.domain.model.Post;
import com.ranc.i5bbsparser.domain.model.PostImage;

import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class I5chParser extends FileDownloadBbsParser {

    private static final Logger log = LoggerFactory.getLogger(I5chParser.class);

    private List<Post> postList = null;

    public I5chParser(BbsThread bbsThread) {
        super(bbsThread);
    }

    /**
     * URLページをパースする
     * <p>実際にページを順繰りにパースしていき、{@code Post}のコレクションを返す。
     * この{@code Post}は、{@code PostImage}のコレクションを含んでいる。
     * <p>インスタンス変数{@code bbsThread}
     * が設定されている場合は、Post番号によって不要な投稿データは除外され、
     * 最新の投稿Noを{@code bbsThread} にセットする。
     * <p>最終的にこのPostコレクションは、Post番号で昇順にソートされた状態で返される。
     * @param url
     * @return
     */
    @Override
    public List<Post> parseUrl(String url) {

        log.info("Start parsing: {}", url);

        // 有効なスレッドかどうか
        if (getBbsThread() != null && !getBbsThread().getEnabled()) {
            log.info("::parse(): This url is set disable: [{}]", url);
            return null;
        }

        this.postList = new ArrayList<>();
        
        parseParentEntry(url);
        recursiveParse(url);

        if (!postList.isEmpty()) {
            
            // メディアを含んでいる Post のリスト
            List<Post> resultList = null;
            // ダウンロード済み以外の Post をフィルタリングするかどうか
            if (getBbsThread() != null && getBbsThread().getLastParsingPostNo() != null) {
                // インスタンスに bbsThread が設定されていて、ダウンロード済みポストNoが存在する場合
                // フィルタリングしたうえで、昇順にソート
                resultList = postList.stream().filter(post -> {
                            if (Long.valueOf(post.getNo()) - super.getBbsThread().getLastParsingPostNo() < 1) {
                                return false;
                            }
                            return true;
                        })
                        .filter(post -> {
                            if (post.getPostImages() == null || post.getPostImages().isEmpty()) {
                                return false;
                            }
                            return true;
                        })
                        .sorted(Comparator.comparing(Post::getNo))
                        .collect(Collectors.toList());
            } else {
                // bbsThread が存在しないか、存在しても最終取得ポストNoが Null 場合
                // 昇順にソート
                resultList = postList.stream()
                        .filter(post -> {
                            if (post.getPostImages() == null || post.getPostImages().isEmpty()) {
                                return false;
                            }
                            return true;
                        })
                        .sorted(Comparator.comparing(Post::getNo))
                        .collect(Collectors.toList());
            }
            
            // BbsThread が設定されている場合、最終取得 Post No を更新
            if (getBbsThread() != null && url.equalsIgnoreCase(getBbsThread().getUrl())) {
                // Post番号の最大値を得る
                Long max = postList.stream().mapToLong(post -> post.getNo()).max().orElse(-1);
                if (max != -1) {
                    // BbsThread の lastParsingPostNo をセット
                    getBbsThread().setLastParsingPostNo(max);
                    log.debug("::parseUrl() - set max post no {}. {}", max, getBbsThread().toString());
                }
            }
            return resultList;
        }
        return null;
    }

    private void parseParentEntry(String url) {

        try {
            String baseUrl = baseUrl(url);
            Document doc = Jsoup.connect(url).get();
            Elements entries = doc.select("#bbs_parent_entry .resentry");
            log.info("::parseParentEntry(): Parsing {} ... has {} entries.", url, entries.size());
            List<Post> posts = new ArrayList<>();
            parsePost(entries, url, baseUrl, posts);
            for (Post post : posts) {
                if (!isDownloaded(post)) {
                    if (hasMedia(post)) {
                        log.debug("::parseParentEntry():post has media(s). postUrl: {}, postNo: {}, postDate: {}", post.getUrl(), post.getNo(), post.getDateTime());
                        postList.add(post);
                    } else {
                        postList.add(post);
                        log.debug("::parseParentEntry():post has no media. postUrl: {}, postNo: {}, postDate: {}", post.getUrl(), post.getNo(), post.getDateTime());
                    }
                }
            }
            // スレッドタイトルの取得
            Element bbsTitle = doc.selectFirst("#bbstitle");
            if (bbsTitle != null && getBbsThread() != null) {
                getBbsThread().setTitle(bbsTitle.text());
            } else {
                log.debug("::parseParentEntry() - bbsTitle not found. {}", url);
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("::parseParentEntry() - {}", e.getMessage());
        }
    }

    private void recursiveParse(String url) {

        boolean reachEnd = false;
        List<Post> posts = parsePage(url);
        for (Post post : posts) {
            postList.add(post);
            if (isDownloaded(post)) {
                // すでにダウンロード済み
                reachEnd = true;
            }
        }

        if (reachEnd) {
            log.debug("::recursiveParse() - post has reached to downloaded post.");
            return;
        }

        String nextPageUrl = nextPage(url);
        if (nextPageUrl == null) {
            return;
        }

        sleep(0);
        recursiveParse(nextPageUrl);
    }

    private boolean hasMedia(Post post) {

        if (post.getPostImages() == null || post.getPostImages().isEmpty()) {
            return false;
        }
        return true;
    }

    private List<Post> parsePage(String url) {

        List<Post> posts = new ArrayList<>();

        log.debug("parsePage target = {}", url);
        
        String baseUrl = baseUrl(url);
        log.debug("Base Url: {}", baseUrl);

        try {
            Document document = Jsoup.connect(url).get();
            Elements entries = document.select("#bbs_entries .resentry");
            log.info("::parsePage(): Parsing {} ... has {} entries.", url, entries.size());
            parsePost(entries, url, baseUrl, posts);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return posts;
    }

    private void parsePost(Elements entries, String url, String baseUrl, List<Post> posts) {

        for (Element entry : entries) {
            // Parse Entry
            // post header information
            Element ehead = entry.selectFirst(".ehead");
            if (ehead == null) {
                continue;
            }
            Element eno = ehead.selectFirst(".eno");
            Element name = ehead.selectFirst(".name");
            Element trip = ehead.selectFirst(".trip");
            String enoString = eno != null ? eno.text() : "";
            String nameString = name != null ? name.text() : "";
            String tripString = trip != null ? trip.text() : "";
            // log.debug("Post Header: No.{}, name:{}, trip:{}", enoString, nameString, tripString);

            // post comment
            Element comment = entry.selectFirst(".comment");
            String commentString = comment != null ? comment.html() : "";
            // log.debug("Post comment: {}", commentString);

            // Post date
            Element date = entry.selectFirst(".date");
            if (date == null) {
                log.warn("::parsePage(): No date elements is found in post: No.{}, name:{}, trip:{}", enoString, nameString, tripString);
                continue;
            }
            LocalDateTime postDateTIme = parsePostDateTime(date);
            if (postDateTIme == null) {
                log.warn("::parsePage(): Date elements is found in post, but failed convert date: No.{}, name:{}, trip:{}", enoString, nameString, tripString);
                continue;
            }
            String postIdString = parsePostId(date);

            // Post thumbs
            Elements thumbList = entry.select(".filethumblist li");
            List<PostImage> postThumbList = parseThumbs(thumbList, baseUrl);

            Post post = new Post();
            post.setUrl(url);
            post.setNo(Long.valueOf(enoString));
            post.setPostId(postIdString);
            post.setComment(commentString);
            post.setDateTime(postDateTIme);
            post.setName(nameString);
            post.setNo(Long.valueOf(enoString));
            post.setPostImages(postThumbList);
            post.setTrip(tripString);
            log.info("::parsePage(): Parsed. url={}, no={}, date={}", url, enoString, postDateTIme);
            posts.add(post);
        }
    }

    private List<PostImage> parseThumbs(Elements thumbList, String baseUrl) {

        List<PostImage> mediaUrlList = new ArrayList<>();

        if (thumbList != null && !thumbList.isEmpty()) {
            for (Element li : thumbList) {
                sleep(0);
                Element link = li.selectFirst("a");
                if (link == null) continue;
                String href = link.attr("href");
                // log.debug("Image Page Url: {}{}", baseUrl, href);
                String postedMediaUrl = baseUrl + href;

                try {
                    Document d = Jsoup.connect(postedMediaUrl).get();
                    String target = "src";
                    Element mainImg = d.selectFirst("#bbs_threadlist_entries .dispimage .MainImg");
                    if (mainImg == null) {
                        mainImg = d.selectFirst("#bbs_threadlist_entries #mjmjplayer a");
                        if (mainImg == null) {
                            log.warn("::parseThumbs():Failed to search element in [{}].", postedMediaUrl);
                            continue;
                        }
                        
                        target = "href";
                    }
                    log.info("::parseThumbs(): Found media ... {}", mainImg.attr(target));
                    String fileUrl = baseUrl + mainImg.attr(target);
                    // log.debug("Media File Url: {}", fileUrl);
                    mediaUrlList.add(new PostImage(fileUrl));
                } catch (IOException e) {
                    log.error("::parseThumbs():failed to get document of [{}]", postedMediaUrl);
                }
            }
        }
        return mediaUrlList;
    }

    
    private String nextPage(String url) {

        try {
            Document doc = Jsoup.connect(url).get();
            Elements pageLinks = doc.select(".page span");
            if (pageLinks == null || pageLinks.isEmpty() || pageLinks.size() < 8) {
                return null;
            }
            Element link = pageLinks.get(1).selectFirst("a");
            if (link == null) {
                return null;
            }
            String href = link.attr("href");
            href = href.replaceFirst("^\\.", "");
            if (href.isEmpty()) {
                return null;
            }
            String baseUrl = baseUrl(url);
            return baseUrl + href;
        } catch (IOException e) {
            throw new IllegalArgumentException("page links parser failed. " + url);
        }
    }

    private String baseUrl(String url) {
        
        String[] urlParts = url.split("/"); 
        if (urlParts.length < 4) {
            throw new IllegalArgumentException(MessageFormatter.format("Bad url", url).getMessage());
        }
        return urlParts[0] + "/" + urlParts[1] + "/" + urlParts[2];
    }

    
    private String parsePostId(Element dateElement) {
        String[] dateAndId = dateElement.text().split("ID:");
        if (dateAndId.length != 2) {
            return null;
        }
        // log.info("Post ID: {},", dateAndId[1].trim());
        return dateAndId[1].trim();
    }

    private LocalDateTime parsePostDateTime(Element dateElement) {

        String[] dateAndId = dateElement.text().split("ID:");
        if (dateAndId.length != 2) {
            return null;
        }
        // log.info("Post Date: {}, ID: {},", dateAndId[0].trim(), dateAndId[1].trim());
        LocalDateTime localDateTime = convertDatetimeString(dateAndId[0].trim());
        return localDateTime;
    }

    private boolean isDownloaded(Post post) {

        if (getBbsThread() == null || getBbsThread().getLastParsingPostNo() == null) {
            return false;
        }
        if (post.getNo() <= getBbsThread().getLastParsingPostNo()) {
            log.debug("::isDownloaded():This post is already parsed: No={}, Date={}, Url={}", post.getNo(), post.getDateTime(), post.getUrl());
            return true;
        }
        return false;
    }

    /**
     * 例："2020/02/20(土) 10:10:45" を LocalDateTime に
     */
    @Override
    public LocalDateTime convertDatetimeString(String dateTimeString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu/MM/dd(EE) HH:mm:ss").localizedBy(Locale.JAPANESE);
        return LocalDateTime.parse(dateTimeString, formatter);
    }
}
