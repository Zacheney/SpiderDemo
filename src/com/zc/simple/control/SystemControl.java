package com.zc.simple.control;

import com.zc.simple.manager.CrawlerManner;
import com.zc.simple.pojos.CrawlResultPojo;
import com.zc.simple.pojos.UrlPojo;

public class SystemControl {
	public static void main(String[] args) {
		// 获得网址的封装
		UrlPojo urlPojo = new UrlPojo("http://www.qq.com");
		// 选择哪一种方式
		CrawlerManner crawlerManner = new CrawlerManner(false);
		// 执行爬虫,并获取封装好的结果
		CrawlResultPojo resultPojo = crawlerManner.crawl(urlPojo);
		String pageContent = resultPojo.getPageContent();
		System.out.println(pageContent);
	}
}
