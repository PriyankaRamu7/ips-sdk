package com.hpe.sis.sie.fe.ips.processing.utils;

public enum ProcessingPattern {
	
	Notify ("FireAndForget"),
	Asynchronous ("ParallelProcessing"),
	Synchronous ("RequestResponse");
	
	private final String pattern;
	
	
	private ProcessingPattern(String p) {
		pattern = p;
	}
	
	public boolean equals(String p) {
		return pattern.equals(p);
	}
	
	public String toString() {
		return this.pattern;
	}

	public String getPattern() {
		return pattern;
	}
	
}
