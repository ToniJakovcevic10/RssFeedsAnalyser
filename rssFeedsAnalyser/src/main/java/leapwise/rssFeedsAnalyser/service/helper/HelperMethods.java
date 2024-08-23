package leapwise.rssFeedsAnalyser.service.helper;

import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import leapwise.rssFeedsAnalyser.exception.AnalysisException;
import leapwise.rssFeedsAnalyser.model.Feed;

public class HelperMethods {

	public List<SyndFeed> getFeedsFromUrls(List<String> urls) {
		// iterate through urls and store data in list of feeds
		List<SyndFeed> rssList = new ArrayList<SyndFeed>();
		try {
			for (String url : urls) {
				URL newsUrl = new URI(URLDecoder.decode(url, StandardCharsets.UTF_8.name())).toURL();
				SyndFeedInput input = new SyndFeedInput();
				rssList.add(input.build(new XmlReader(newsUrl)));
			}
		} catch (Exception e) {
			throw new AnalysisException("Error retrieving feeds from urls", 802);
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
				String[] keyWords = getSignificantWords(feedEntry.getTitle().toLowerCase().split("\\W+"));

				for (String keyWord : keyWords) {
					// if doesn't exist, add topic to the hashmap
					// if exists already, add the feed to the list<Feed>
					topicsMap.computeIfAbsent(keyWord, k -> new ArrayList<>()).add(feed);
				}
			}
		}
		return topicsMap;
	}

	public String[] getSignificantWords(String[] words) {
		// TODO: use better method to find topics, instead of this custom algorithm
		// one solution: -integrate chatgpt api and ask him to find topics

		// List of insignificant words
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
				"these", "then", "now", "where", "when", "how", // Other Function Words
				"bbc", "cnn", "nbc" // News Organizations
		};

		// Convert the array to a HashSet for quick lookups
		Set<String> insignificantWords = new HashSet<>(Arrays.asList(insignificantWordsArray));

		// Process the input array
		return Arrays.stream(words).filter(word -> !insignificantWords.contains(word.toLowerCase()))
				.filter(word -> word.length() > 2).toArray(String[]::new);

	}
}
