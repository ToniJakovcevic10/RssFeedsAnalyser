package leapwise.rssFeedsAnalyser.service;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import leapwise.rssFeedsAnalyser.exception.analysisException;
import leapwise.rssFeedsAnalyser.model.Analysis;
import leapwise.rssFeedsAnalyser.model.Feed;
import leapwise.rssFeedsAnalyser.model.Topic;
import leapwise.rssFeedsAnalyser.repository.AnalysisRepository;
import leapwise.rssFeedsAnalyser.repository.TopicRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TopicsAnalyserService {

	WebClient.Builder builder = WebClient.builder();

	@Autowired
	private AnalysisRepository analysisRepository;

	@Autowired
	private TopicRepository topicRepository;
	
	public int GetFeedsAnalysis(List<String> urls) {
		if (urls.size() < 2)
			throw new analysisException("The list of URLs less than 2", 800);
		else {
			try {
				Analysis analysis = new Analysis();
				// create a list of all rss-s
				List<SyndFeed> rssList = getFeedsFromUrls(urls);

				// hashmap with possible topics and connected feeds
				HashMap<String, List<Feed>> possibleTopicsMap = getPossibleHotTopics(rssList);

				// list of relevant feeds that will be added to the DB
				List<Topic> hotTopicsList = new ArrayList<Topic>();

				// iterate through hashmap and save hot topics
				for (Map.Entry<String, List<Feed>> possibleTopicMap : possibleTopicsMap.entrySet()) {
					// save only hot topics
					if (possibleTopicMap.getValue().size() > 1) {

						// create new topic with keyword from the hashmap
						Topic topic = new Topic(possibleTopicMap.getKey());
						// add analysis to the topic
						topic.setAnalysis(analysis);
						// set frequency of the topic
						topic.setFeedsFrequency(possibleTopicMap.getValue().size());
						
						// insert in the table feed and topic
						for (Feed feed : possibleTopicMap.getValue()) {
							feed.setTopic(topic);
						}
						// add feeds to the topic
						topic.setFeeds(possibleTopicMap.getValue());
						// add topic to the hotTopicsList
						hotTopicsList.add(topic);
					}
				}
				// add topic to the analysis
				analysis.setTopics(hotTopicsList);

				// save analysis to the DB
				// since CascadeType=ALL, automatically, all topics and feeds will be added to
				// the
				// DB as well
				System.out.print(analysisRepository.save(analysis));

				return analysis.getId();

			} catch (Exception e) {
				throw new analysisException("Internal server error", 500);
			}
		}
	}

	public List<Topic> fetchMostFrequentTopics(int analysisId) {

		List<Topic> hotTopics = new ArrayList<Topic>();
		Optional<Analysis> analysis = analysisRepository.findById(String.valueOf(analysisId));
		if (analysis.isPresent()) {
			// get 3 topics from the DB
			Pageable topThree = PageRequest.of(0, 3);
			hotTopics = topicRepository.get3MostFrequentTopics(topThree);
		} else {
			throw new analysisException(String.format("Analysis not found by id: %s", analysisId), 801);
		}

		return hotTopics;
	}
	
	public List<SyndFeed> getFeedsFromUrls(List<String> urls) {
		// iterate through urls and store data in list of feeds
		List<SyndFeed> rssList = new ArrayList<SyndFeed>();
		try {
			for (String url : urls) {
				@SuppressWarnings("deprecation")
				URL newsUrl = new URL(URLDecoder.decode(url, StandardCharsets.UTF_8.name()));
				SyndFeedInput input = new SyndFeedInput();
				rssList.add(input.build(new XmlReader(newsUrl)));
			}
		} catch (Exception e) {
			throw new analysisException("Error retrieving feeds from urls", 802);
		}
		return rssList;
	}

	public HashMap<String, List<Feed>> getPossibleHotTopics(List<SyndFeed> rssList) {
		HashMap<String, List<Feed>> topicsMap = new HashMap<String, List<Feed>>();

		for (SyndFeed feeds : rssList) {

			for (int i = 0; i < feeds.getEntries().size(); i++) {
				// get keywords from title
				// split title into String array with regex
				SyndEntry feedEntry = feeds.getEntries().get(i);
				Feed feed = new Feed(feedEntry.getTitle(), feedEntry.getLink());
				String[] keyWords = getPossibleTopics(feedEntry.getTitle().toLowerCase().split("\\W+"));

				for (String keyWord : keyWords) {
					// if doesn't exist, add topic to the hashmap
					// if exists already, add the feed to the list<Feed>
					topicsMap.computeIfAbsent(keyWord, k -> new ArrayList<>()).add(feed);
				}
			}
		}
		return topicsMap;
	}

	public String[] getPossibleTopics(String[] words) {
		String[] insignificantWordsArray = { "here", "what", "s", "you", "need", "know", "a", "an", "the", // Articles
				"and", "but", "or", "if", "while", "because", "so", // Conjunctions
				"in", "on", "at", "by", "for", "with", "of", "to", "from", "about", "under", "over", "between", // Prepositions
				"he", "she", "it", "they", "we", "I", "you", "me", "him", "her", "us", "them", "whose", "which", "that", // Pronouns
				"is", "are", "was", "were", "have", "has", "had", "will", "would", "can", "could", "should", "may",
				"might", "must", // Auxiliary Verbs
				"very", "really", "quite", "just", "too", "almost", "always", "never", "often", "rarely", // Common
																											// Adverbs
				"good", "bad", "new", "old", "large", "small", "important", "significant", "recent", "major", // Common
																												// Adjectives
				"not", "only", "some", "any", "all", "every", "each", "one", "two", "three", "this", "that", "those",
				"these", "then", "now", "where", "when", "how" // Other Function Words
		};

		// Convert the array to a HashSet for quick lookups
		Set<String> insignificantWords = new HashSet<>(Arrays.asList(insignificantWordsArray));

		// Process the input array
		return Arrays.stream(words).filter(word -> !insignificantWords.contains(word.toLowerCase()))
				.filter(word -> word.length() > 2).toArray(String[]::new);

	}

	
}
