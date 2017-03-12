package me.codz.crawler;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

/**
 * <p>Created with IDEA
 * <p>Author: laudukang
 * <p>Date: 2016/8/13
 * <p>Time: 23:53
 * <p>Version: 1.0
 */
public class CSDNCrawler extends BreadthCrawler {

	public CSDNCrawler() {
		super("crawl", true);
		//start page
		this.addSeed("http://blog.csdn.net/.*");//添加种子地址
		//fetch url like http://news.hfut.edu.cn/show-xxxxxxhtml
		this.addRegex("http://blog.csdn.net/.*/article/details/.*");
		//do not fetch jpg|png|gif
		this.addRegex("-.*\\.(jpg|png|gif).*");

	}

	public static void main(String[] args) throws Exception {
		CSDNCrawler crawler = new CSDNCrawler();
		crawler.setThreads(50);
		crawler.setTopN(100);
		//crawler.setResumable(true);

		//start crawl with depth of 4
		crawler.start(4);
	}

	@Override
	public void visit(Page page, CrawlDatums next) {
		String url = page.getUrl();
		String content = "";
		try {
			content = ContentExtractor.getContentByUrl(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (page.matchUrl("http://blog.csdn.net/.*/article/details/.*")) {
			String title = page.select("div[class=article_title]").first().text();
			String author = page.select("div[id=blog_userface]").first().text();//获取作者名

			System.out.println("title:" + title + "\tauthor:" + author);
			//System.out.println("content:" + content);
		}
	}
}