package me.codz.tool;

import com.google.common.collect.Maps;
import me.codz.domain.Blog;
import me.codz.repository.BlogRepository;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * <p>Created with IDEA
 * <p>Author: laudukang
 * <p>Date: 2016/6/4
 * <p>Time: 16:53
 * <p>Version: 1.0
 */
public class ESUtil {

	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static Map<String, Object> importBlogToESByPage(BlogRepository blogRepository, Client client, String index, String type, int page) throws IOException {

		Map<String, Object> map = new HashMap<>();
		Pageable pageable = new PageRequest(0, 5000);
		Page<Blog> blogPage = blogRepository.findAll(pageable);

		int blogSize = blogPage.getContent().size();
		int pageIndex = 0;
		int blogTotalCount = 0;

		Instant startTime = Instant.now();
		System.out.format("开始执行导入，当前时间：%s\n", simpleDateFormat.format(new Date()));

		List<Blog> blogList = blogPage.getContent();
		//count导入页面限制
		while (nonNull(blogList) && blogList.size() > 0 && pageIndex < page) {
			//计入博客总数
			blogTotalCount += blogSize;

			ESUtil.blogToES(client, index, type, blogList, blogPage.getNumber());

			pageIndex++;

			//继续查询
			if (pageIndex < page) {
				pageable = new PageRequest(pageIndex, 5000);
				blogPage = blogRepository.findAll(pageable);
				blogList = blogPage.getContent();
				blogSize = blogList.size();
			}
		}

		Instant endTime = Instant.now();
		Duration duration = Duration.between(startTime, endTime);
		System.out.format("\n数据导入完毕：%d条，总页数：%d，总耗时：%s(s)，当前时间：%s\n", blogTotalCount, pageIndex, duration.getSeconds(), simpleDateFormat.format(new Date()));

		map.put("blogTotalCount", blogTotalCount);
		map.put("totalPageCount", pageIndex);
		return map;
	}

	public static void blogToES(Client client, String indexName, String typeName, List<Blog> blogList, int page) throws IOException {
		page++;//0→1第一页

		if (StringUtils.isBlank(typeName)) {
			return;
		}
		if (isNull(blogList) || blogList.size() == 0) {
			return;
		}

		System.out.format("页数%d开始导入到ES，当前时间:%s\n", page, simpleDateFormat.format(new Date()));

		Instant startTime = Instant.now();
		int count = 1;
		BulkRequestBuilder bulkRequest = client.prepareBulk();
		int blogId = 0;
		for (Blog blog : blogList) {
			if (isNull(blog)) {
				continue;
			}
			XContentBuilder source = convertBlogToSource(blog);

			bulkRequest.add(client.prepareIndex(indexName, typeName, String.valueOf(blog.getId())).setSource(source));

			if (count % 1000 == 0) {
				BulkResponse response = bulkRequest.execute().actionGet();
				if (response.hasFailures()) {
					System.out.format("页数%d的分组%d提交失败\n", page, count / 1000);
				}
				bulkRequest = client.prepareBulk();
			}
			count++;
			if (count > blogList.size()) {
				blogId = blog.getId();
			}
		}
		System.out.println("分组最后博客id：" + blogId);
		if (bulkRequest.numberOfActions() > 0) {
			BulkResponse response = bulkRequest.execute().actionGet();
			if (response.hasFailures()) {
				System.out.format("页数%d的最后一波提交失败\n", page);
			}
		}
		Instant endTime = Instant.now();
		Duration duration = Duration.between(startTime, endTime);
		System.out.format("页数%d导入ES完毕，总运行时间:%s秒，当前时间:%s\n", page, duration.getSeconds(), simpleDateFormat.format(new Date()));
	}

	private static XContentBuilder convertBlogToSource(Blog blog) {
		String content;
		// Markdown 博客转换
		if (blog.getContentType() == 1 || blog.getContentType() == 3) {
			content = MarkdownUtil.markdown(blog.getContent());
		} else {
			content = blog.getContent();
		}

		String plainContent = "";
		if (StringUtils.isNotBlank(content)) {
			plainContent = FormatUtil.getPlainText(content);
		}

		XContentBuilder source = null;
		try {
			source = XContentFactory.jsonBuilder()
					.startObject()
					.field("id", blog.getId())
					.field("space", blog.getSpace())
					.field("type", blog.getType())
					.field("viewCount", blog.getView_count())
					.field("replyCount", blog.getReply_count())
					.field("voteCount", blog.getVote_count())
					.field("recomm", blog.getRecomm())
					.field("title", StringUtils.defaultString(blog.getTitle(), ""))
					.field("abstracts", StringUtils.defaultString(blog.getAbstracts(), ""))
					.field("content", plainContent)
					.field("htmlContent", content)
					.field("tags", StringUtils.defaultString(blog.getTags(), ""))
					.field("createTime", simpleDateFormat.format(blog.getCreateTime()))
					.endObject();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return source;
	}

	public static void createBlogMapping(Client client, String indexName, String typeName) throws IOException {

		client.admin().indices().prepareCreate(indexName).execute().actionGet();

		Map<String, String> contentSrc = Maps.newHashMap();
		contentSrc.put("type", "string");
		contentSrc.put("store", "yes");

		Map<String, Map<String, String>> fieldsMap = new HashMap<>();
		fieldsMap.put("another", contentSrc);

		// @formatter:off
		XContentBuilder builder = XContentFactory.jsonBuilder()
				.startObject()
				.startObject(typeName)
				//.startObject("")
				//      .field("enabled", false)
				// .endObject()
				.startObject("properties")
					.startObject("id")
						.field("type", "integer")//.field("store", "yes")
					.endObject()
					.startObject("space")
						.field("type", "integer")//.field("store", "yes")
					.endObject()
					.startObject("type")
						.field("type", "integer")//.field("store", "no")
					.endObject()
					.startObject("viewCount")
						.field("type", "integer")
					.endObject()
					.startObject("replyCount")
						.field("type", "integer")
					.endObject()
					.startObject("voteCount")
						.field("type", "integer")
					.endObject()
					.startObject("recomm")
						.field("type", "integer")
					.endObject()
					.startObject("title")
						.field("type", "string").field("store", "yes").field("analyzer", "ik").field("searchAnalyzer", "ik_max_word")
					.endObject()
					.startObject("abstracts")
						.field("type", "string").field("store", "yes").field("analyzer", "ik").field("searchAnalyzer", "ik")
					.endObject()
					.startObject("content")
						.field("type", "string").field("store", "yes").field("analyzer", "ik").field("searchAnalyzer", "ik").field("fields",fieldsMap)
					.endObject()
					.startObject("htmlContent")
						.field("type", "string").field("store", "yes")
					.endObject()
					.startObject("tags")
						.field("type", "string").field("store", "yes").field("analyzer", "ik").field("searchAnalyzer", "ik")
					.endObject()
					.startObject("createTime")
						.field("type", "date").field("format", "yyy-MM-dd HH:mm:ss")//.field("store", "yes")
					.endObject()
				.endObject()
				.endObject()
				.endObject();
		// @formatter:on

		PutMappingRequest mapping = Requests.putMappingRequest(indexName).type(typeName).source(builder);
		client.admin().indices().putMapping(mapping).actionGet();
	}
}
