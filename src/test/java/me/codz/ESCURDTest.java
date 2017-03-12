package me.codz;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.codz.domain.Blog;
import me.codz.tool.ESUtil;
import org.elasticsearch.action.admin.indices.analyze.AnalyzeResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.highlight.HighlightField;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;


/**
 * <p>Created with IDEA
 * <p>Author: laudukang
 * <p>Date: 2016/5/31
 * <p>Time: 23:00
 * <p>Version: 1.0
 */
public class ESCURDTest {
	private static final String CLUSTER_NAME = "es";
	private static final String ES_HOST = "192.168.2.128";
	private static final int ES_PORT = 9300;
	private static Client client;
	private static ObjectMapper mapper = new ObjectMapper();

	@Before
	public void initES() {
		Settings settings = Settings.builder()
				.put("cluster.name", CLUSTER_NAME).build();

		try {
			client = TransportClient.builder()
					.settings(settings)
					.build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ES_HOST), ES_PORT));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	@After
	public void closeES() {
		client.close();
	}

	@Test
	public void testIndexOne() {
		Blog blog = new Blog();
		blog.setId(1);
		blog.setTitle("博客标题");
		blog.setContent("博客正文");

		String json = null;
		try {
			json = mapper.writeValueAsString(blog);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		IndexResponse response = client.prepareIndex("blog", "blog", "1")
				.setSource(json)
				.execute()
				.actionGet();

		String _index = response.getIndex();
		String _type = response.getType();
		String _id = response.getId();
		long _version = response.getVersion();

		System.out.format("_index=%s,type=%s,id=%s,version=%s", _index, _type, _id, _version + "");
	}

	@Test
	public void testGetOne() {
		String indexName = "blog";
		String type = "blog";
		String blogId = "1";
		GetResponse response = null;
		try {
			response = client.prepareGet(indexName, type, blogId).
					setOperationThreaded(false)
					.execute()
					.get();

			System.out.println(response.getSource());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDeleteOne() {
		String indexName = "twitter";
		String type = "tweet";
		String blogId = "1";
		DeleteResponse response = client.prepareDelete(indexName, type, blogId).get();
		System.out.println(response.getVersion());
	}

	@Test
	public void testUpdate_1() {
		UpdateRequest updateRequest = new UpdateRequest();
		updateRequest.index("index");
		updateRequest.type("type");
		updateRequest.id("1");
		try {
			updateRequest.doc(
					jsonBuilder()
							.startObject()
							.field("gender", "male")
							.endObject());
			client.update(updateRequest).get();
		} catch (IOException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testUpdate_2() {
		client.prepareUpdate("ttl", "doc", "1")
				.setScript(new Script("ctx._source.gender = \"male\"", ScriptService.ScriptType.INLINE, null, null))
				.get();
	}

	@Test
	public void testUpdate_3() {
		try {
			client.prepareUpdate("ttl", "doc", "1")
					.setDoc(jsonBuilder()
							.startObject()
							.field("gender", "male")
							.endObject())
					.get();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testMultiGet() {
		MultiGetResponse multiGetItemResponses = client.prepareMultiGet()
				.add("twitter", "tweet", "1")
				.add("school", "student", "1")
				.add("school", "student", "hui")
				.get();

		for (MultiGetItemResponse itemResponse : multiGetItemResponses) {
			GetResponse response = itemResponse.getResponse();
			if (response.isExists()) {
				String json = response.getSourceAsString();
				System.out.println(json);
			}
		}
	}

	@Test
	public void testBulk() {
		BulkRequestBuilder bulkRequest = client.prepareBulk();

		try {
			bulkRequest.add(client.prepareIndex("twitter", "tweet", "1")
					.setSource(jsonBuilder()
							.startObject()
							.field("user", "kimchy")
							.field("postDate", new Date())
							.field("message", "trying out Elasticsearch")
							.endObject()
					)
			);

			bulkRequest.add(client.prepareIndex("twitter", "tweet", "2")
					.setSource(jsonBuilder()
							.startObject()
							.field("user", "kimchy")
							.field("postDate", new Date())
							.field("message", "another post")
							.endObject()
					)
			);
		} catch (IOException e) {
			e.printStackTrace();
		}

		BulkResponse bulkResponse = bulkRequest.get();
		System.out.println(bulkResponse.hasFailures());
	}

	@Test
	public void testSearchALL() {
		SearchResponse response = client.prepareSearch().execute().actionGet();
		response.getHits().forEach(t -> System.out.print(t.getSourceAsString()));
	}

	@Test
	public void testSearchByCondition() {
		SearchResponse response = client.prepareSearch("blog")
				.setTypes("blog")
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(QueryBuilders.termQuery("content", "介绍"))

				.setFrom(0).setSize(60).setExplain(true)
				.execute()
				.actionGet();
		response.getHits().forEach(t -> System.out.print(t.getId()));
	}

	@Test
	public void testIKAnalyzer() {
		SearchResponse searchResponse = client.prepareSearch("ikindex")
				.setTypes("blog")
				.setQuery(QueryBuilders.boolQuery()
						.must(QueryBuilders.matchQuery("title", "技术")
								.operator(MatchQueryBuilder.Operator.AND)))
				.addHighlightedField("title")

				.setHighlighterPreTags("<span style=\"color:red\">")
				.setHighlighterPostTags("</span>")

				.setQuery(QueryBuilders.matchQuery("content", "技术方面"))
				.addHighlightedField("content")

				.setHighlighterPreTags("<code>")
				.setHighlighterPostTags("</code>")
				.setFrom(0).setSize(30).setExplain(true)
				.execute()
				.actionGet();

		SearchHits hits = searchResponse.getHits();

		long total = hits.getTotalHits();
		System.out.println("Search total result count:" + total);
		for (SearchHit hit : hits) {
			Map<String, HighlightField> result = hit.getHighlightFields();
			System.out.println("-----------------");
			System.out.println(result.size() + " A map of highlighted fields:\n" + result);
			//HighlightField titleField = result.get("title");
			//Text[] titleTexts = titleField.fragments();
			//for (Text text : titleTexts) {
			//    System.out.println("title text:" + text);
			//}
			System.out.println("-----------------");
		}
	}

	@Test
	public void testIKAnalyzeResponse() {
		AnalyzeResponse analyzeResponse = client.admin()
				.indices()
				.prepareAnalyze("twitter", "变形金刚5新预告大黄蜂大战擎天柱，钢索登场")
				.setAnalyzer("ik")
				.execute()
				.actionGet();

		System.out.println("size:{}" + analyzeResponse.getTokens().size());
		List<AnalyzeResponse.AnalyzeToken> list = analyzeResponse.getTokens();
		for (AnalyzeResponse.AnalyzeToken token : list) {
			System.out.println("Term:{}" + token.getTerm());
		}
	}

	@Test
	public void testSearchWithHighlight() {
		try {
			SearchRequestBuilder searchRequestBuilder = client
					.prepareSearch("blog");

			// 用来设定在多个类型中搜索,可以多个,String... types
			searchRequestBuilder.setTypes("blog");

			// 设置查询类型
			// 1.SearchType.DFS_QUERY_THEN_FETCH = 精确查询
			// 2.SearchType.SCAN = 扫描查询，无序
			searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

			// 设置查询关键词
			//searchRequestBuilder
			// .setQuery(QueryBuilders.boolQuery().should(QueryBuilders.termQuery("title", key))
			// .should(QueryBuilders.termQuery("content", key)));

			QueryStringQueryBuilder queryBuilder = new QueryStringQueryBuilder("java.util.Date");
			queryBuilder.analyzer("ik_smart");
			queryBuilder.field("title").field("content");
			searchRequestBuilder.setQuery(queryBuilder);

			// 分页应用
			searchRequestBuilder.setFrom(0).setSize(20);

			// 设置是否按查询匹配度排序
			searchRequestBuilder.setExplain(true);

			// 按照字段排序
			//searchRequestBuilder.addSort("publish_time", SortOrder.DESC);

			// 设置高亮显示
			searchRequestBuilder.addHighlightedField("title");
			searchRequestBuilder.addHighlightedField("content");
			searchRequestBuilder
					.setHighlighterPreTags("<span style=\"color:red\">");
			searchRequestBuilder.setHighlighterPostTags("</span>");

			// 执行搜索,返回搜索响应信息
			SearchResponse response = searchRequestBuilder.execute().actionGet();

			// 获取搜索的文档结果
			SearchHits searchHits = response.getHits();
			SearchHit[] hits = searchHits.getHits();

			System.out.println("hits.length=" + hits.length);
			System.out.println("total=" + response.getHits().totalHits());

			for (SearchHit hit : hits) {
				Map<String, HighlightField> result = hit.highlightFields();
				HighlightField titleField = result.get("title");
				String title = "";
				if (titleField != null) {
					Text[] titleTexts = titleField.fragments();
					title = Arrays.stream(titleTexts).map(Text::toString).reduce("", String::concat);
				}

				HighlightField contentField = result.get("content");
				String content = "";
				if (contentField != null) {
					Text[] contentTexts = contentField.fragments();
					content = Arrays.stream(contentTexts).map(Text::toString).reduce("", String::concat);
				}
				System.out.format("title:%s,content:%s\n", title, content);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCreateBlogIndex() throws IOException {
		ESUtil.createBlogMapping(client, "oschina", "blog");
	}
}
