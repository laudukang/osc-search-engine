package me.codz.domain;

import java.util.Date;

/**
 * <p>Created with IDEA
 * <p>Author: laudukang
 * <p>Date: 2016/6/5
 * <p>Time: 22:28
 * <p>Version: 1.0
 */
public class BlogDomain {
	private int id;
	private int space;
	private byte type;
	private String title;
	private String content;
	private String tags;
	private Date createTime;
	private byte contentType;
	private String abstracts;
	private int viewCount;
	private int replyCount;
	private int voteCount;
	private int recomm;

	public BlogDomain() {
	}

	public BlogDomain(int id, int space, byte type, String title, String content, String tags, Date createTime, byte contentType, String abstracts, int viewCount, int replyCount, int voteCount) {
		this.id = id;
		this.space = space;
		this.type = type;
		this.title = title;
		this.content = content;
		this.tags = tags;
		this.createTime = createTime;
		this.contentType = contentType;
		this.abstracts = abstracts;
		this.viewCount = viewCount;
		this.replyCount = replyCount;
		this.voteCount = voteCount;
	}

	public BlogDomain(int id, int space, byte type, String title, String content, String tags, Date createTime, byte contentType, String abstracts, int viewCount, int replyCount, int voteCount, int recomm) {
		this.id = id;
		this.space = space;
		this.type = type;
		this.title = title;
		this.content = content;
		this.tags = tags;
		this.createTime = createTime;
		this.contentType = contentType;
		this.abstracts = abstracts;
		this.viewCount = viewCount;
		this.replyCount = replyCount;
		this.voteCount = voteCount;
		this.recomm = recomm;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSpace() {
		return space;
	}

	public void setSpace(int space) {
		this.space = space;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public byte getContentType() {
		return contentType;
	}

	public void setContentType(byte contentType) {
		this.contentType = contentType;
	}

	public String getAbstracts() {
		return abstracts;
	}

	public void setAbstracts(String abstracts) {
		this.abstracts = abstracts;
	}

	public int getViewCount() {
		return viewCount;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}

	public int getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}

	public int getVoteCount() {
		return voteCount;
	}

	public void setVoteCount(int voteCount) {
		this.voteCount = voteCount;
	}

	public int getRecomm() {
		return recomm;
	}

	public void setRecomm(int recomm) {
		this.recomm = recomm;
	}
}
