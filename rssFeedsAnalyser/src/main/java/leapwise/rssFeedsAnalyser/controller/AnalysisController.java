package leapwise.rssFeedsAnalyser.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import leapwise.rssFeedsAnalyser.exception.AnalysisException;
import leapwise.rssFeedsAnalyser.model.Topic;
import leapwise.rssFeedsAnalyser.service.RssAnalyserServiceImpl;

@RestController
@RequestMapping
public class AnalysisController {
	
	@Autowired
	private RssAnalyserServiceImpl topicsAnalyserService;
	
	// it is necessary to insert encoded string value for url
	@GetMapping("/analyse/new")
	@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Analysis of the URL has been started"),
		@ApiResponse(responseCode="500", description = "Internal Server Error"),
        @ApiResponse(responseCode = "800", description = "The list of URLs is less than 2"),
        @ApiResponse(responseCode = "802", description = "Error retrieving feeds from urls")
    })
	public ResponseEntity<String> fetchAnalysis(@RequestParam List<String> url){
        return ResponseEntity.ok("Analysis of the URLs is finished and saved in the DB with id: " + topicsAnalyserService.GetFeedsAnalysis(url));
	}
	
	
	@GetMapping("/frequency/{id}")
	@ApiResponses({
		@ApiResponse(responseCode="200", description = "get three hottest topics"),
		@ApiResponse(responseCode="500", description = "Internal Server Error"),
		@ApiResponse(responseCode="801", description = "analysis not found")
	})
	public ResponseEntity<List<Topic>> fetchMostFrequentTopics(@PathVariable("id") String analysisId){
		
		List<Topic> topics = topicsAnalyserService.fetchMostFrequentTopics(Integer.parseInt(analysisId));
		return ResponseEntity.ok(topics);
	}
	
	@ExceptionHandler(AnalysisException.class)
    public ResponseEntity<String> handleAnalysisException(AnalysisException ex) {
        return ResponseEntity.status(ex.getCode()).body(ex.getMessage());
    }
}
