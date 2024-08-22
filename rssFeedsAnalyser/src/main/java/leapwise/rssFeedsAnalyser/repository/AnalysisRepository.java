package leapwise.rssFeedsAnalyser.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import leapwise.rssFeedsAnalyser.model.Analysis;

@Repository
public interface AnalysisRepository extends CrudRepository<Analysis, String>{

}
