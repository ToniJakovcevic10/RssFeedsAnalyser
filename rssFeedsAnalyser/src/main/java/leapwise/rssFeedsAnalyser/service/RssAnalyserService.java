package leapwise.rssFeedsAnalyser.service;

import java.util.List;

import leapwise.rssFeedsAnalyser.exception.AnalysisException;
import leapwise.rssFeedsAnalyser.model.Topic;

public interface RssAnalyserService {

	public int GetFeedsAnalysis(List<String> urls) throws AnalysisException;
	
	public List<Topic> fetchMostFrequentTopics(int analysisId) throws AnalysisException;
}
