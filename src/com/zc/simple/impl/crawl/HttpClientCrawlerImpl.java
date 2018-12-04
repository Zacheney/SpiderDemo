package com.zc.simple.impl.crawl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.zc.simple.iface.crawl.ICrawler;
import com.zc.simple.pojos.CrawlResultPojo;
import com.zc.simple.pojos.UrlPojo;

public class HttpClientCrawlerImpl implements ICrawler {
	private final static ObjectMapper mapper = new ObjectMapper();

	@Override
	public CrawlResultPojo crawl(UrlPojo urlPojo) {
		CrawlResultPojo resultPojo = new CrawlResultPojo();
		if (urlPojo == null) {
			resultPojo.setSuccess(false);
			return resultPojo;
		}
		CloseableHttpClient httpClient = HttpClients.custom().build();
		// get方式个更简单
		HttpGet httpGet = new HttpGet(urlPojo.getUrl());
		/*
		 * RequestBuilder uri = null; try { uri =
		 * RequestBuilder.post().setUri(new URI(urlPojo.getUrl())); } catch
		 * (URISyntaxException e1) { // TODO Auto-generated catch block
		 * e1.printStackTrace(); } Map<String, Object> parasMap =
		 * urlPojo.getParasMap(); if (parasMap != null) { Set<Entry<String,
		 * Object>> entrySet = parasMap.entrySet(); for (Entry<String, Object>
		 * entry : entrySet) { uri.addParameter(entry.getKey(),
		 * entry.getValue().toString()); }
		 * 
		 * }
		 * 
		 * HttpUriRequest request = uri.build();
		 */
		BufferedReader br = null;
		try {
			CloseableHttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			br = new BufferedReader(new InputStreamReader(entity.getContent(), "utf-8"));
			String line = null;
			StringBuilder builder = new StringBuilder();
			while ((line = br.readLine()) != null) {
				builder.append(line);
			}
			resultPojo.setSuccess(true);
			resultPojo.setPageContent(builder.toString());
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

	@SuppressWarnings("resource")
	public static void main(String[] args) throws JsonProcessingException, IOException {
		// 创建io流
		FileWriter fw = new FileWriter(new File("C://Users/18368/Desktop/spider.txt"));
		BufferedWriter bw = new BufferedWriter(fw);
		// 先搜他个20页,这是整个贴吧的页数
		for (int i = 0; i < 20; i++) {
			HttpClientCrawlerImpl httpClientCrawlerImpl = new HttpClientCrawlerImpl();
			int pn = (i - 1) * 50;
			CrawlResultPojo resultPojo = httpClientCrawlerImpl
					.crawl(new UrlPojo("https://tieba.baidu.com/f?kw=%E5%89%91%E7%BD%913&ie=utf-8&pn=" + pn));
			String result = resultPojo.getPageContent();
			Document document = Jsoup.parse(result);
			Elements ties = document.select("a.j_th_tit");
			// 遍历所有帖子,找到每一条帖子
			for (Element tie : ties) {
				System.out.println("-----------start----------");
				// 打印帖子标题
				String t1 = tie.ownText();
				System.out.println(t1);
				// 找到帖子id
				String href = tie.attr("href");
				String tieId = href.split("/")[2];
				// 开始进入到贴吧内容里面去
				CrawlResultPojo tieDetai = httpClientCrawlerImpl
						.crawl(new UrlPojo("https://tieba.baidu.com/p/" + tieId + "?pn=1"));
				String content = tieDetai.getPageContent();
				Document tieDetail = Jsoup.parse(content);
				// 获取总页数
				Elements select = tieDetail.select("span.red");
				int sumFn = 0;
				if (select.size() >= 2) {
					Element sumPage = select.get(1);
					sumFn = Integer.parseInt(sumPage.text());
				}
				// 针对每一页的所有内容
				for (int k = 0; k < sumFn; k++) {
					CrawlResultPojo everytieDetail = httpClientCrawlerImpl
							.crawl(new UrlPojo("https://tieba.baidu.com/p/" + tieId + "?pn=" + (k + 1)));
					String everytieDetailContent = everytieDetail.getPageContent();
					Document documentEveryTieDetail = Jsoup.parse(everytieDetailContent);
					Elements contents = documentEveryTieDetail.select(".d_post_content");
					Elements authorNames = documentEveryTieDetail.select(".p_author_name");
					// 获取每一页所有的评论
					CrawlResultPojo conversions = httpClientCrawlerImpl.crawl(new UrlPojo(
							"https://tieba.baidu.com/p/totalComment?&tid=" + tieId + "&pn=" + (k + 1) + "&see_lz=0"));
					String conversionsContent = conversions.getPageContent();
					// 对评论进行json解析
					JsonNode jsonNode = mapper.readTree(conversionsContent);

					// 遍历所有评论,进入到具体一条评论中去
					for (int g = 0; g < contents.size(); g++) {

						// 作者:评论
						String authorName_contents = authorNames.get(g).text() + ":" + contents.get(g).text();
						System.out.println(authorName_contents);
						bw.write(authorName_contents);
						bw.newLine();
						bw.flush();
						// 获取到评论人的id
						String[] split = contents.get(g).attr("id").split("_");
						String id = null;
						if (split.length == 3) {
							id = split[2];
						}

						// 开始查是否被别人评论
						JsonNode jsonNode3 = jsonNode.get("data").get("comment_list").get(id);
						// 如果被人评论的话就..
						if (jsonNode3 != null) {
							ArrayNode arrayNode = (ArrayNode) jsonNode3.get("comment_info");
							for (JsonNode jsonNode2 : arrayNode) {
								String asText = jsonNode2.get("content").asText();
								//中文正则表达式
								asText  = asText.replaceAll("[^\u4E00-\u9FA5，]", "");
								bw.write("\t---回复---" + asText);
								bw.newLine();
								bw.flush();

								System.out.println("\t---回复---" + asText);
							}
						}

					}

				}
				// 每个帖子完成浏览
				System.out.println("-----------end----------");

			}
			// 贴吧的一页完成浏览
			System.out.println("-------完成下载第" + (i + 1) + "页数据-------");
		}
		bw.close();

	}

}
