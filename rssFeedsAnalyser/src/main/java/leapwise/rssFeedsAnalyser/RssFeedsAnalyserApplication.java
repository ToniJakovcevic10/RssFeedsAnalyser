package leapwise.rssFeedsAnalyser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "leapwise.rssFeedsAnalyser")
public class RssFeedsAnalyserApplication {

	public static void main(String[] args) {
		SpringApplication.run(RssFeedsAnalyserApplication.class, args);
	}

}
