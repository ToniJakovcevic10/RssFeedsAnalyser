package leapwise.rssFeedsAnalyser.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import leapwise.rssFeedsAnalyser.model.Topic;

@Repository
public interface TopicRepository extends CrudRepository<Topic, String>{

	// Custom query to find the top 3 Topics ordered by counter descending
	@Query("SELECT t FROM Topic t LEFT JOIN FETCH t.feeds ORDER BY t.feedsFrequency DESC")
    List<Topic> get3MostFrequentTopics(Pageable pageable);
}
