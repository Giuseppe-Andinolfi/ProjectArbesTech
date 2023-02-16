package com.phonecompany.billing;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TelephoneBillCalculatorImpl implements TelephoneBillCalculator {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private static final BigDecimal RATE_DAY = BigDecimal.valueOf(1);
    private static final BigDecimal RATE_NIGHT = BigDecimal.valueOf(0.5);
    private static final BigDecimal RATE_LONG_CALL = BigDecimal.valueOf(0.2);
    private static final int MINUTES_LONG_CALL = 5;

    @Override
    public BigDecimal calculate(String phoneLog) {
        Map<String, Long> phoneCallCountMap = Arrays.stream(phoneLog.split("\n"))
                .map(this::parsePhoneLog)
                .collect(Collectors.groupingBy(PhoneCall::getPhoneNumber, Collectors.counting()));

        String mostFrequentPhoneNumber = phoneCallCountMap.entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(null);

        return Arrays.stream(phoneLog.split("\n"))
                .map(this::parsePhoneLog)
                .filter(phoneCall -> !phoneCall.getPhoneNumber().equals(mostFrequentPhoneNumber))
                .map(this::calculatePhoneCallCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private PhoneCall parsePhoneLog(String phoneLog) {
        String[] parts = phoneLog.split(",");
        String phoneNumber = parts[0];
        LocalDateTime startDateTime = LocalDateTime.parse(parts[1], DATE_TIME_FORMATTER);
        LocalDateTime endDateTime = LocalDateTime.parse(parts[2], DATE_TIME_FORMATTER);
        return new PhoneCall(phoneNumber, startDateTime, endDateTime);
    }

    private BigDecimal calculatePhoneCallCost(PhoneCall phoneCall) {
        BigDecimal rate = isDaytime(phoneCall.getStartDateTime()) ? RATE_DAY : RATE_NIGHT;
        int durationMinutes = getDurationInMinutes(phoneCall.getStartDateTime(), phoneCall.getEndDateTime());

        if (durationMinutes > MINUTES_LONG_CALL) {
            int extraMinutes = durationMinutes - MINUTES_LONG_CALL;
            return rate.multiply(BigDecimal.valueOf(MINUTES_LONG_CALL))
                    .add(RATE_LONG_CALL.multiply(BigDecimal.valueOf(extraMinutes)));
        } else {
            return rate.multiply(BigDecimal.valueOf(durationMinutes));
        }
    }

    private boolean isDaytime(LocalDateTime dateTime) {
        int hour = dateTime.getHour();
        return (hour >= 8 && hour < 20);
    }

    private int getDurationInMinutes(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        long durationInSeconds = startDateTime.until(endDateTime, java.time.temporal.ChronoUnit.SECONDS);
        return (int) (durationInSeconds / 60);
    }

    private static class PhoneCall {

        private String phoneNumber;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;

        public PhoneCall(String phoneNumber, LocalDateTime startDateTime, LocalDateTime endDateTime) {
            this.phoneNumber = phoneNumber;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public LocalDateTime getStartDateTime() {
            return startDateTime;
        }

        public LocalDateTime getEndDateTime() {
            return endDateTime;
        }
    }

}
