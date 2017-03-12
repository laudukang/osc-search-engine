package me.codz.service;

import me.codz.config.PersistenceJPAConfig;
import me.codz.domain.Blog;
import me.codz.domain.BlogDomain;
import me.codz.repository.BlogRepository;
import me.codz.tool.ESUtil;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.IOException;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

/**
 * <p>Created with IDEA
 * <p>Author: laudukang
 * <p>Date: 2016/6/4
 * <p>Time: 15:37
 * <p>Version: 1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceJPAConfig.class}, loader = AnnotationConfigContextLoader.class)
@Rollback(false)
public class BlogRepositoryTest {
	@Autowired
	private BlogRepository blogRepository;

	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static String esClusterName = "es";
	private static String indexName = "oschina";
	private static String type = "blog";
	private static String esIPAddress = "192.168.2.128";
	private static int esPort = 9300;

	@Test
	public void testFindOne() {
		Blog blog = blogRepository.findOne(84244);
		if (nonNull(blog)) {
			System.out.println(blog.getTitle());
		}
	}

	@Test
	public void testFindPart() {
		List<BlogDomain> blogList = blogRepository.findPart(14876);
		if (nonNull(blogList) && blogList.size() > 0) {
			System.out.println(blogList.size());
		}
	}

	@Test
	public void testCreateBlogMapping() {
		Settings settings = Settings.builder().put("cluster.name", esClusterName).build();
		try {
			Client client = TransportClient.builder()
					.settings(settings)
					.build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(esIPAddress), esPort));

			ESUtil.createBlogMapping(client, indexName, type);
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testImportBlogToESByPage() {
		Instant startTime = Instant.now();

		int importPage = Integer.MAX_VALUE;

		Settings settings = Settings.builder().put("cluster.name", esClusterName).build();

		Client client = null;
		Map<String, Object> result = null;
		try {
			client = TransportClient.builder()
					.settings(settings)
					.build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(esIPAddress), esPort));

			result = ESUtil.importBlogToESByPage(blogRepository, client, indexName, type, importPage);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (nonNull(client)) {
			client.close();
		}

		Instant endTime = Instant.now();
		Duration duration = Duration.between(startTime, endTime);

		System.out.println(result);
		System.out.println("total cost time:" + duration.getSeconds());
	}

}
