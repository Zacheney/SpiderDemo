package com.zc.simple.manager;

import com.zc.simple.iface.crawl.ICrawler;
import com.zc.simple.impl.crawl.HttpURLConnectionCrawlImpl;
import com.zc.simple.impl.crawl.SocketCrawlerImpl;
import com.zc.simple.pojos.CrawlResultPojo;
import com.zc.simple.pojos.UrlPojo;

public class CrawlerManner {

	private ICrawler crawlerImpl;

	public CrawlerManner(boolean isSocket) {
		if (isSocket) {
			crawlerImpl = new SocketCrawlerImpl();
		} else {
			crawlerImpl = new HttpURLConnectionCrawlImpl();
		}

	}

	public CrawlResultPojo crawl(UrlPojo urlPojo) {
		CrawlResultPojo resultPojo = crawlerImpl.crawl(urlPojo);
		return resultPojo;
	}

}
