package com.zc.simple.impl.crawl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import com.zc.simple.iface.crawl.ICrawler;
import com.zc.simple.pojos.CrawlResultPojo;
import com.zc.simple.pojos.UrlPojo;

public class HttpURLConnectionCrawlImpl implements ICrawler {

	@Override
	public CrawlResultPojo crawl(UrlPojo urlPojo) {
		CrawlResultPojo resultPojo = new CrawlResultPojo();
		if (urlPojo == null || urlPojo == null) {
			resultPojo.setSuccess(false);
			resultPojo.setPageContent(null);
			return resultPojo;
		}
		// 创建链接
		HttpURLConnection httpUrlConnection = urlPojo.getHttpUrlConnection();
		BufferedReader br = null;
		StringBuilder result = new StringBuilder();
		try {
			// 基本思路都是从链接中获得io流
			br = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				result.append(line + "\r\n");
			}
			resultPojo.setSuccess(true);
			resultPojo.setPageContent(result.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}

		return resultPojo;
	}

}
