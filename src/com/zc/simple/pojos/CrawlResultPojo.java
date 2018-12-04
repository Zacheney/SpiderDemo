package com.zc.simple.pojos;

//抓取结果的封住
public class CrawlResultPojo {
	//是否抓取成功
	private boolean isSuccess;
	//返回的抓取内容
	private String pageContent;
	//返回的状态码
	private int httpStatuCode;
	public CrawlResultPojo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CrawlResultPojo(boolean isSuccess, String pageContent, int httpStatuCode) {
		super();
		this.isSuccess = isSuccess;
		this.pageContent = pageContent;
		this.httpStatuCode = httpStatuCode;
	}
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public String getPageContent() {
		return pageContent;
	}
	public void setPageContent(String pageContent) {
		this.pageContent = pageContent;
	}
	public int getHttpStatuCode() {
		return httpStatuCode;
	}
	public void setHttpStatuCode(int httpStatuCode) {
		this.httpStatuCode = httpStatuCode;
	}
	@Override
	public String toString() {
		return "CrawlResultPojo [isSuccess=" + isSuccess + ", pageContent=" + pageContent + ", httpStatuCode="
				+ httpStatuCode + "]";
	}
	
	
	
	
	
	
}
