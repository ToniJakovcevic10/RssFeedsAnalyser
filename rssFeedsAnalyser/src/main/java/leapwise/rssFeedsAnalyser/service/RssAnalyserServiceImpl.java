package leapwise.rssFeedsAnalyser.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.rometools.rome.feed.synd.SyndFeed;

import leapwise.rssFeedsAnalyser.exception.AnalysisException;
import leapwise.rssFeedsAnalyser.model.Analysis;
import leapwise.rssFeedsAnalyser.model.Feed;
import leapwise.rssFeedsAnalyser.model.Topic;
import leapwise.rssFeedsAnalyser.repository.AnalysisRepository;
import leapwise.rssFeedsAnalyser.repository.TopicRepository;
import leapwise.rssFeedsAnalyser.service.helper.HelperMethods;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RssAnalyserServiceImpl implements RssAnalyserService{

	WebClient.Builder builder = WebClient.builder();

	@Autowired
	private AnalysisRepository analysisRepository;

	@Autowired
	private TopicRepository topicRepository;
	
	private final HelperMethods helperMethods = new HelperMethods();
	
	@Override
	public int GetFeedsAnalysis(List<String> urls) {
		if (urls.size() < 2)
			throw new AnalysisException("The list of URLs is less than 2", 800);
		else {
			try {
				Analysis analysis = new Analysis();
				// create a list of all rss-s
				List<SyndFeed> rssList = helperMethods.getFeedsFromUrls(urls);

				// hashmap with possible topics and connected feeds
				HashMap<String, List<Feed>> possibleTopicsMap = helperMethods.getPossibleHotTopics(rssList);

				// list of relevant topics that will be added to the DB
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
							topic.getFeeds().add(feed);
							
							// add feeds to the topic
							feed.getTopics().add(topic);
						}
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
				throw new AnalysisException("Internal Server Error", 500);
			}
		}
	}

	@Override
	public List<Topic> fetchMostFrequentTopics(int analysisId) {

		List<Topic> hotTopics = new ArrayList<Topic>();
		Optional<Analysis> analysis = analysisRepository.findById(String.valueOf(analysisId));
		if (analysis.isPresent()) {
			// get 3 topics from the DB
			Pageable topThree = PageRequest.of(0, 3);
			try {
				hotTopics = topicRepository.get3MostFrequentTopics(topThree);				
			}catch (Exception e) {
				throw new AnalysisException(String.format("Internal Server Error"), 500);
			}
		} 
		else {
			throw new AnalysisException(String.format("Analysis not found by id: %s", analysisId), 801);
		}
		return hotTopics;
	}
}
