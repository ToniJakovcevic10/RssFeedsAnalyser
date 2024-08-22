package leapwise.rssFeedsAnalyser.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
	@JoinColumn(name="analysisId")
	@JsonIgnore
	private Analysis analysis;
	
	@OneToMany(mappedBy="topic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonManagedReference
	private List<Feed> feeds;
	
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
