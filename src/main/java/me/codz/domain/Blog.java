package me.codz.domain;

import org.hibernate.sql.Template;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;


/**
 * The persistent class for the osc_blogs database table.
 */
@Entity
@Table(name = "osc_blogs")
@NamedQuery(name = "Blog.findAll", query = "SELECT b FROM Blog b")
public class Blog implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String abstracts;

	@Lob
	private String content;

	@Column(name = "content_type")
	private byte contentType;

	@Column(name = "create_time")
	private Timestamp createTime;

	private byte options;

	private int space;

	private String tags;

	private String title;

	private byte type;

	private int view_count;
	private int reply_count;
	private int vote_count;
	private int recomm;

	public Blog() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAbstracts() {
		return this.abstracts;
	}

	public void setAbstracts(String abstracts) {
		this.abstracts = abstracts;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public byte getContentType() {
		return this.contentType;
	}

	public void setContentType(byte contentType) {
		this.contentType = contentType;
	}

	public Timestamp getCreateTime() {
		return this.createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public byte getOptions() {
		return this.options;
	}

	public void setOptions(byte options) {
		this.options = options;
	}

	public int getSpace() {
		return this.space;
	}

	public void setSpace(int space) {
		this.space = space;
	}

	public String getTags() {
		return this.tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public byte getType() {
		return this.type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public int getReply_count() {
		return reply_count;
	}

	public void setReply_count(int reply_count) {
		this.reply_count = reply_count;
	}

	public int getView_count() {
		return view_count;
	}

	public void setView_count(int view_count) {
		this.view_count = view_count;
	}

	public int getVote_count() {
		return vote_count;
	}

	public void setVote_count(int vote_count) {
		this.vote_count = vote_count;
	}

	public int getRecomm() {
		return recomm;
	}

	public void setRecomm(int recomm) {
		this.recomm = recomm;
	}
}