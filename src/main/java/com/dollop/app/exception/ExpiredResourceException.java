package com.dollop.app.exception;

public class ExpiredResourceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExpiredResourceException() {
		super();
	}

	public ExpiredResourceException(String message) {
		super(message);
	}
	

}
