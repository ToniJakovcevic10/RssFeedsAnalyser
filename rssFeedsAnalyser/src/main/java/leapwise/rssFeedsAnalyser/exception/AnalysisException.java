package leapwise.rssFeedsAnalyser.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AnalysisException extends RuntimeException{
	
	private int code;
	
	public AnalysisException(String message, int code) {
		super(message);
		this.setCode(code);
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
