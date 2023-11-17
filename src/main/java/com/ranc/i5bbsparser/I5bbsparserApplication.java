package com.ranc.i5bbsparser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.ranc.i5bbsparser.domain.components.OptionProcessor;
import com.ranc.i5bbsparser.domain.components.config.PersistentDataProperties;
import com.ranc.i5bbsparser.domain.components.initialdata.DataInitializer;
import com.ranc.i5bbsparser.domain.services.BbsThreadService;


@SpringBootApplication
@EnableJpaAuditing
public class I5bbsparserApplication implements CommandLineRunner{

	private static final Logger LOG = LoggerFactory.getLogger(I5bbsparserApplication.class);

	@Autowired
	OptionProcessor optionProcessor;
	@Autowired
	PersistentDataProperties persistentDataProperties;
	@Autowired
	BbsThreadService bbsThreadService;
	@Autowired
	@Qualifier("actualDataInitializer")
	DataInitializer dataInitializer;

	public static void main(String[] args) {
		SpringApplication.run(I5bbsparserApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		LOG.info("::run():Application started.");

		for (int i = 0; i < args.length; i++) {
			LOG.info("args[{}]: {}", i, args[i]);
		}
		LOG.info("args length:{}", args.length);
		if (persistentDataProperties.isEnabled()) {
			initInitialData();
		}
		optionProcessor.process(args);
	}

	private void initInitialData() {
		dataInitializer.dataInit();
	}
}
