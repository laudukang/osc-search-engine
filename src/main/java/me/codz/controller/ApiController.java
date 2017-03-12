package me.codz.controller;

import com.google.common.collect.Lists;
import me.codz.domain.BlogDomain;
import me.codz.repository.BlogRepository;
import me.codz.tool.ESUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;

/**
 * <p>Created with IDEA
 * <p>Author: laudukang
 * <p>Date: 2016/5/30
 * <p>Time: 23:08
 * <p>Version: 1.0
 */
@Controller
public class ApiController {

	private static final String CLUSTER_NAME = "es";
	private static final int CLUSTER_PORT = 9300;
	private static final String OSC_INDEX = "oschina";
	private static final String BLOG_TYPE = "blog";
	private static final String ES_HOST = "192.168.2.128";

	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	private BlogRepository blogRepository;
	private Client client;

	//init es client
	public ApiController() {
		Settings settings = Settings.builder().put("cluster.name", CLUSTER_NAME).build();
		try {
			client = TransportClient.builder()
					.settings(settings).build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ES_HOST), CLUSTER_PORT));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		client.close();
	}

	@RequestMapping(value = "hi")
	@ResponseBody
	public Map<String, Object> testHi() {
		Map<String, Object> map = new HashMap<>();
		map.put("msg", "hi lau");
		map.put("success", true);
		return map;
	}

	@RequestMapping("/")
	public String home() {
		return "index";
	}

	@RequestMapping("/blog/{blogId}")
	public String blogDetail(@PathVariable("blogId") Integer id, Model model) {
		if (nonNull(id)) {
			try {
				GetResponse response = client
						.prepareGet(OSC_INDEX, BLOG_TYPE, String.valueOf(id))
						.setOperationThreaded(false)
						.execute()
						.get();
				Map<String, Object> map = response.getSource();
				model.addAttribute("data", map);
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		return "blog_detail";
	}

	/**
	 * 搜索api
	 *
	 * @param key
	 * @param pageIndex
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/search")
	public String search(String key, Integer pageIndex, HttpServletRequest request, Model model) {
		if (StringUtils.isNotBlank(key)) {
			if (isNull(pageIndex) || pageIndex < 1) {
				pageIndex = 1;
			}

			long userId = 0;
			String userIdStr = request.getParameter("user");
			if (NumberUtils.isDigits(userIdStr)) {
				userId = Long.valueOf(userIdStr);
			}

			Map<String, Object> map = this.searchEngine(key, userId, pageIndex);

			long total = (long) map.get("total");
			model.addAttribute("data", map.get("data"));
			model.addAttribute("total", total);
			model.addAttribute("totalPage", (int) Math.ceil(total / 20.0));
		} else {
			model.addAttribute("data", null);
			model.addAttribute("total", 0);
			model.addAttribute("totalPage", 0);
		}
		model.addAttribute("page", pageIndex);
		model.addAttribute("key", StringUtils.isNotBlank(key) ? key : "");
		return "index";
	}

	/**
	 * 搜索
	 *
	 * @param key
	 * @param userId
	 * @param page
	 * @return
	 */
	private Map<String, Object> searchEngine(String key, long userId, int page) {
		Map<String, Object> map = new HashMap<>();
		long totalCount = 0;

		if (StringUtils.isBlank(key)) {
			map.put("data", null);
			map.put("total", totalCount);
			return map;
		}

		SearchResponse response = searchResponseBuilder(key, userId, page);
		// 搜索结果总数
		totalCount = response.getHits().totalHits();

		// 转换搜索的文档结果
		List<BlogDomain> blogDomainList = convertSearchHitToBlogDomain(response.getHits());

		map.put("data", blogDomainList);
		map.put("total", totalCount);
		return map;
	}

	private SearchResponse searchResponseBuilder(String key, long userId, int page) {
		SearchRequestBuilder searchRequestBuilder = client
				.prepareSearch(OSC_INDEX);

		// 用来设定在多个类型中搜索，可以多个，String... types
		searchRequestBuilder.setTypes(BLOG_TYPE);

		// 设置查询类型
		// 1.SearchType.DFS_QUERY_THEN_FETCH = 精确查询
		// 2.SearchType.SCAN = 扫描查询,无序
		searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

		// 设置查询关键词
		//  searchRequestBuilder
		// .setQuery(QueryBuilders.boolQuery()
		// .should(QueryBuilders.termQuery("title", key))
		// .should(QueryBuilders.termQuery("content", key)));

		QueryStringQueryBuilder queryBuilder = new QueryStringQueryBuilder(key);
		queryBuilder.analyzer("ik_smart");
		queryBuilder.field("title").field("content").field("abstracts");

		//是否需要过滤用户
		if (userId != 0) {
			QueryBuilder userQueryBuilder = boolQuery()
					.must(termQuery("space", userId))
					.must(queryBuilder);
			searchRequestBuilder.setQuery(userQueryBuilder);
		} else {
			searchRequestBuilder.setQuery(queryBuilder);
		}

		// 分页设置，低效
		searchRequestBuilder.setFrom((page - 1) * 20).setSize(20);
		// 设置是否按查询匹配度排序
		searchRequestBuilder.setExplain(true);
		// 按照字段排序
		// searchRequestBuilder.addSort("createTime", SortOrder.DESC);

		// 设置高亮显示
		searchRequestBuilder.addHighlightedField("title");
		searchRequestBuilder.addHighlightedField("content");
		searchRequestBuilder.setHighlighterPreTags("<span style=\"color:red\">");
		searchRequestBuilder.setHighlighterPostTags("</span>");

		// 或者em标签
		//searchRequestBuilder.setHighlighterPreTags("<em>");
		//searchRequestBuilder.setHighlighterPostTags("<em>");

		// 执行搜索,返回搜索响应信息
		return searchRequestBuilder.execute().actionGet();
	}

	/**
	 * 暂仅支持blog内容提取转换
	 *
	 * @param searchHits
	 * @return
	 */
	private List<BlogDomain> convertSearchHitToBlogDomain(SearchHits searchHits) {
		List<BlogDomain> blogDomainList = Lists.newArrayList();

		Arrays.stream(searchHits.getHits()).forEach(hit -> {
			BlogDomain blogDomain = new BlogDomain();

			Map<String, Object> source = hit.getSource();

			blogDomain.setId(NumberUtils.toInt(hit.getId()));

			// 获取对应的高亮域
			Map<String, HighlightField> result = hit.highlightFields();

			String title;
			// 从设定的高亮域中取得指定域
			HighlightField titleField = result.get("title");
			if (nonNull(titleField)) {
				// 取得定义的高亮标签
				Text[] titleTexts = titleField.fragments();
				title = Arrays.stream(titleTexts).map(Text::toString).reduce("", String::concat);
			} else {
				// 使用原生title
				title = String.valueOf(source.get("title"));
			}
			blogDomain.setTitle(title);

			// 从设定的高亮域中取得指定域
			HighlightField contentField = result.get("content");
			String content;

			if (contentField != null) {
				// 取得定义的高亮标签
				Text[] contentTexts = contentField.fragments();
				content = Arrays.stream(contentTexts).map(Text::toString).reduce("", String::concat);
			} else {
				// 使用原生博客正文
				content = String.valueOf(source.get("content"));
			}
			content = content.length() > 300 ? content.substring(0, 300) : content;
			blogDomain.setContent(content);

			blogDomainList.add(blogDomain);

		});

		return blogDomainList;
	}

	@RequestMapping(value = "createMapping", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> createMapping(String index, String type) {
		Map<String, Object> map = new HashMap<>();
		String responseMsg;
		boolean responseStatus = false;
		if (StringUtils.isBlank(index) || StringUtils.isBlank(type)) {
			responseMsg = "索引或类型不能为空";
		} else {
			try {
				ESUtil.createBlogMapping(client, index, type);

				responseStatus = true;
				responseMsg = "创建成功";
			} catch (IOException e) {
				e.printStackTrace();
				responseStatus = false;
				responseMsg = "创建失败：" + e.getMessage();
			}
		}

		map.put("success", responseStatus);
		map.put("msg", responseMsg);
		return map;
	}

	@RequestMapping(value = "import", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> indexBlog(String index, String type, String page) {
		Map<String, Object> result = new HashMap<>();

		if (StringUtils.isBlank(index) || StringUtils.isBlank(type)) {
			result.put("success", false);
			result.put("msg", "非法参数");
		} else {
			try {
				int p = NumberUtils.toInt(page, Integer.MAX_VALUE);
				Instant startTime = Instant.now();

				Map<String, Object> map = ESUtil.importBlogToESByPage(blogRepository, client, index, type, p);

				Instant endTime = Instant.now();
				Duration duration = Duration.between(startTime, endTime);
				result.put("success", true);
				result.put("totalTime(s)", duration.getSeconds());
				result.put("msg", "导入成功");
				result.put("blogTotalCount", map.get("blogTotalCount"));
				result.put("totalPageCount", map.get("totalPageCount"));
			} catch (IOException e) {
				e.printStackTrace();
				result.put("success", false);
				result.put("msg", e.getMessage());
			}
		}
		return result;
	}


}
