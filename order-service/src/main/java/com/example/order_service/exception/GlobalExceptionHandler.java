package com.example.order_service.exception;

import com.example.order_service.dto.FailedSvcLogEvent;
import com.example.order_service.kafka.KafkaFailedServiceLogSender;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String SERVICE_NAME = "order-service";

    private final KafkaFailedServiceLogSender kafkaFailedServiceLogSender;

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        LOG.warn("Bad order-service request. path: {}, message: {}", request.getRequestURI(), ex.getMessage());
        publishFailedServiceLog(ex, request);
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiError> handleNoSuchElementException(NoSuchElementException ex, HttpServletRequest request) {
        LOG.warn("Order resource not found. path: {}, message: {}", request.getRequestURI(), ex.getMessage());
        publishFailedServiceLog(ex, request);
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        LOG.warn("Order database constraint failed. path: {}", request.getRequestURI(), ex);
        publishFailedServiceLog(ex, request);
        return buildResponse(HttpStatus.CONFLICT, "Request violates database constraints", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception ex, HttpServletRequest request) {
        LOG.error("Unhandled order-service exception. path: {}", request.getRequestURI(), ex);
        publishFailedServiceLog(ex, request);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error", request);
    }

    private void publishFailedServiceLog(Exception ex, HttpServletRequest request) {
        FailedSvcLogEvent event = FailedSvcLogEvent.builder()
                .failedReason(ex.getMessage())
                .svcName(SERVICE_NAME)
                .failedTime(LocalDateTime.now())
                .build();
        LOG.info("Publishing failed service log event. path: {}, svcName: {}, failedTime: {}",
                request.getRequestURI(), event.getSvcName(), event.getFailedTime());
        kafkaFailedServiceLogSender.publishFailedServiceLogEvent(event);
    }

    private ResponseEntity<ApiError> buildResponse(HttpStatus status, String message, HttpServletRequest request) {
        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(apiError);
    }
}
