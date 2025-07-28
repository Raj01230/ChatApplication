
package com.dollop.app.handler;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
//import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.dollop.app.exception.BadRequestException;
import com.dollop.app.exception.DateFormateException;
import com.dollop.app.exception.ExpiredResourceException;
import com.dollop.app.exception.InvalidResourceException;
import com.dollop.app.exception.OtpNotVerifiedException;
import com.dollop.app.exception.ResourceAlreadyExistsException;
import com.dollop.app.exception.ResourceNotFoundException;
import com.dollop.app.response.ErrorResponse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice

public class CustomHandler {
	private static final Logger log = LoggerFactory.getLogger(CustomHandler.class);

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> name(HttpMessageNotReadableException ex) {
		InvalidFormatException invalidEx = null;

		String fieldName = null;
		Map<String, String> response = new HashMap<>();
		if (ex.getCause() instanceof JsonParseException || ex.getCause() instanceof MismatchedInputException) {
			response.put("error", "Invalid request body ");
			return new ResponseEntity<ErrorResponse>(
					ErrorResponse.builder().message(response).response(HttpStatus.BAD_REQUEST).build(),
					HttpStatus.BAD_REQUEST);
		}
		invalidEx = (InvalidFormatException) ex.getCause();

		if (invalidEx != null && !invalidEx.getPath().isEmpty()) {
			fieldName = invalidEx.getPath().get(0).getFieldName();
			log.error("InvalidFormatException: {}", invalidEx.getMessage());
		} else {
			response.put("error", "Invalid request,request body is required");
			return new ResponseEntity<ErrorResponse>(
					ErrorResponse.builder().message(response).response(HttpStatus.BAD_REQUEST).build(),
					HttpStatus.BAD_REQUEST);
		}

		if (ex.getCause() instanceof HttpMessageNotReadableException
				|| fieldName.substring(fieldName.length() - 4).equals("Date")) {

			response.put("error", "Invalid value for field '" + fieldName + "'. Expected an yyyy-MM-dd HH:mm:ss.");
		} else if (ex.getCause() instanceof InvalidFormatException) {

			response.put("error", "Invalid value for field '" + fieldName + "'. Expected an integer.");
		} else {
			response.put("error", "Invalid request body format.");
		}
		return new ResponseEntity<ErrorResponse>(
				ErrorResponse.builder().message(response).response(HttpStatus.BAD_REQUEST).build(),
				HttpStatus.BAD_REQUEST);

	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<String> handleNotFound(Exception ex) {
		return ResponseEntity.status(404).body("Endpoint not found: " + ex.getMessage());
	}

	@ExceptionHandler(InsufficientAuthenticationException.class)
	public ResponseEntity<String> handleNotFound(InsufficientAuthenticationException ex) {
		return ResponseEntity.status(401).body(ex.getMessage());
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorResponse> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {

		String details = "Please use the correct HTTP method. Expected: " + ex.getSupportedHttpMethods();
		Map<String, String> m = new HashMap<>();
		m.put("details", details);
		m.put("message", ex.getMessage());
		ErrorResponse errorResponse = ErrorResponse.builder().message(m).response(HttpStatus.METHOD_NOT_ALLOWED.value())
				.build();

		return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
	}

	@ExceptionHandler(ResourceAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> name(ResourceAlreadyExistsException ex) {
		return new ResponseEntity<ErrorResponse>(
				ErrorResponse.builder().message(ex.getMessage()).response(HttpStatus.CONFLICT).build(),
				HttpStatus.CONFLICT);

	}

	@ExceptionHandler(InvalidResourceException.class)
	public ResponseEntity<ErrorResponse> name(InvalidResourceException ex) {
		return new ResponseEntity<ErrorResponse>(
				ErrorResponse.builder().message(ex.getMessage()).response(HttpStatus.BAD_REQUEST).build(),
				HttpStatus.BAD_REQUEST);

	}

	@ExceptionHandler(DateFormateException.class)
	public ResponseEntity<ErrorResponse> name(DateFormateException ex) {
		return new ResponseEntity<ErrorResponse>(
				ErrorResponse.builder().message(ex.getMessage()).response(HttpStatus.BAD_REQUEST).build(),
				HttpStatus.BAD_REQUEST);

	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> name(ConstraintViolationException ex) {
		return new ResponseEntity<ErrorResponse>(
				ErrorResponse.builder().message(ex.getMessage()).response(HttpStatus.BAD_REQUEST).build(),
				HttpStatus.BAD_REQUEST);

	}

	@ExceptionHandler(OtpNotVerifiedException.class)
	public ResponseEntity<ErrorResponse> name(OtpNotVerifiedException ex) {
		return new ResponseEntity<ErrorResponse>(
				ErrorResponse.builder().message(ex.getMessage()).response(HttpStatus.UNAUTHORIZED).build(),
				HttpStatus.UNAUTHORIZED);

	}

	@ExceptionHandler(ExpiredResourceException.class)
	public ResponseEntity<ErrorResponse> name(ExpiredResourceException ex) {
		System.err.println("EX :: " + ex.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ErrorResponse.builder().message(ex.getMessage()).response(HttpStatus.UNAUTHORIZED).build());
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> name(ResourceNotFoundException ex) {
		return new ResponseEntity<ErrorResponse>(
				ErrorResponse.builder().message(ex.getMessage()).response(HttpStatus.NOT_FOUND).build(),
				HttpStatus.NOT_FOUND);

	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleMethodNotArgsNotValidException(
			MethodArgumentNotValidException ex) {
		Map<String, String> respo = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = "message";
			String message = error.getDefaultMessage();
			respo.put(fieldName, message);
		});

		return new ResponseEntity<>(respo, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorResponse> name(BadRequestException ex) {
		return new ResponseEntity<ErrorResponse>(
				ErrorResponse.builder().message(ex.getMessage()).response(HttpStatus.NOT_FOUND).build(),
				HttpStatus.NOT_FOUND);

	}
}
