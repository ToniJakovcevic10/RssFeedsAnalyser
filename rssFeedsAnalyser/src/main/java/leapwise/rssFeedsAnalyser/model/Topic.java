package leapwise.rssFeedsAnalyser.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="topic")
public class Topic {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	@Column(name="topic")
	private String topic;
	
	@Column(name="feedsFrequency")
	private int feedsFrequency;
	
	@ManyToOne
	@JoinColumn(name="analysis_id")
	@JsonIgnore
	private Analysis analysis;
	
	 @ManyToMany(fetch=FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	    @JoinTable(
	        name = "topic_feed",
	        joinColumns = @JoinColumn(name = "topic_id"),
	        inverseJoinColumns = @JoinColumn(name = "feed_id")
	    )
	private List<Feed> feeds = new ArrayList<Feed>();
	
	public Topic() {}
	
	public Topic(String topic) {
		this.topic = topic;
	}

	public int getId() {
		return id;
	}
	
	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public int getFeedsFrequency() {
		return feedsFrequency;
	}

	public void setFeedsFrequency(int feedsFrequency) {
		this.feedsFrequency = feedsFrequency;
	}

	public List<Feed> getFeeds() {
		return feeds;
	}

	public void setFeeds(List<Feed> feeds) {
		this.feeds = feeds;
	}

	public void setAnalysis(Analysis analysis) {
		this.analysis = analysis;
	}
}
