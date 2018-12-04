package com.zc.simple.impl.crawl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.zc.simple.iface.crawl.ICrawler;
import com.zc.simple.pojos.CrawlResultPojo;
import com.zc.simple.pojos.UrlPojo;

public class SocketCrawlerImpl implements ICrawler {

	@SuppressWarnings("resource")
	@Override
	public CrawlResultPojo crawl(UrlPojo urlPojo) {
		CrawlResultPojo resultPojo = new CrawlResultPojo();
		if (urlPojo == null && urlPojo.getUrl() == null) {
			resultPojo.setSuccess(false);
			resultPojo.setPageContent(null);
			return resultPojo;
		}
		String host = urlPojo.getHost();
		if (host == null) {
			resultPojo.setSuccess(false);
			resultPojo.setPageContent(null);
			return resultPojo;
		}
		BufferedWriter bw = null;
		BufferedReader br = null;
		String result = "";
		try {
			Socket socket = new Socket(host, 80);
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			bw.write("GET " + urlPojo.getUrl() + " HTTP/1.0\r\n");//get后面和http前面都要加上一个空格
			bw.write("HOST:" + host + "\r\n");
			bw.write("\r\n");
			bw.flush();
			br = new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				String read = br.readLine();
				result += read + "\r\n";
			}
			resultPojo.setSuccess(true);
			resultPojo.setPageContent(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
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
