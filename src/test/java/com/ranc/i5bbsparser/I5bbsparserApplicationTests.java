package com.ranc.i5bbsparser;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import com.google.common.net.InternetDomainName;
import com.ranc.i5bbsparser.domain.components.BbsParser;
import com.ranc.i5bbsparser.domain.components.BbsParserFactory;
import com.ranc.i5bbsparser.domain.model.BbsThread;
import com.ranc.i5bbsparser.domain.services.BbsThreadService;

@Configuration
@SpringBootTest
//@SpringBootTest(args = {"-a", "https://www.syakouba.com/album", "c", "d"})
class I5bbsparserApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	ApplicationContext ctx;
	@Autowired
	BbsThreadService bbsThreadService;
	@Autowired
	BbsParserFactory parserFactory;

	@Transactional
	@Test
	void tetRunner() throws Exception {
		String[] args = {"-i", "https://c.5chan.jp/0W6WAfs0hs"};
		CommandLineRunner runnner = ctx.getBean(I5bbsparserApplication.class);
		runnner.run(args);
	}

	@Test
	public void testExecute() {
		List<BbsThread> threads = new ArrayList<>();
		bbsThreadService.findAll().forEach(threads::add);
		threads.forEach(thread -> {
			BbsParser parser = parserFactory.getParser(thread);
			parser.parse();
		});
	}


	@Test
	void urlParsingTest() throws Exception {
		//String spec = "https://www.syakouba.com/album/album.php?u=0/3b410c3cc9";
		String spec = "https://www.syakouba.com/album";
		URI url = new URI(spec);
		System.out.println("getAuthority(): " + url.getAuthority()); // 192.168.10.120
		System.out.println("getScheme(): " + url.getScheme()); // http
		System.out.println("getSchemeSpecificPart()" + url.getSchemeSpecificPart());
		System.out.println("getAuthority(): " + url.getAuthority());
		System.out.println("getHost(): " + url.getHost()); // 192.168.10.120
		System.out.println("getPath(): " + url.getPath()); // /doc/resource1.html
		System.out.println("getQuery(): " + url.getQuery());
		System.out.println("baseUrl: " + baseUrl(url.toString()));
		Path path = Paths.get(url.getPath());
		System.out.println("fileName: " + path.getFileName().toString());

		URI uri = new URI(spec);
		String host = uri.getHost();

		InternetDomainName internetDomainName = InternetDomainName.from(host).topPrivateDomain();
		String domainName = internetDomainName.toString();
		System.out.println(domainName);

	}
	
    private String baseUrl(String url) {
        
        String[] urlParts = url.split("/"); 
        if (urlParts.length < 4) {
            throw new IllegalArgumentException(MessageFormatter.format("Bad url", url).getMessage());
        }
        return urlParts[0] + "/" + urlParts[1] + "/" + urlParts[2];
    }
}
