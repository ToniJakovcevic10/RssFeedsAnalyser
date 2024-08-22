package leapwise.rssFeedsAnalyser.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import leapwise.rssFeedsAnalyser.model.Feed;

@Repository
public interface FeedRepository extends CrudRepository<Feed, String>{

}
