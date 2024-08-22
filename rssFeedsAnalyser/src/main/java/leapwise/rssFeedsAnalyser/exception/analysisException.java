package leapwise.rssFeedsAnalyser.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class analysisException extends RuntimeException{
	
	private int code;
	
	public analysisException(String message, int code) {
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
