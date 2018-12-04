package com.zc.simple.iface.crawl;

import com.zc.simple.pojos.CrawlResultPojo;
import com.zc.simple.pojos.UrlPojo;

public interface ICrawler {
	public CrawlResultPojo crawl(UrlPojo urlPojo);

}
