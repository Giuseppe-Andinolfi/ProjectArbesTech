package com.phonecompany.billing;
import java.math.BigDecimal;

public class Main {

    public static void main(String[] args) {
        String phoneLog = "420774577453,13-01-2020 18:10:15,13-01-2020 18:12:57\n" +
                "420776562353,18-01-2020 08:59:20,18-01-2020 09:10:00\n" +
                "420774577453,20-01-2020 20:35:10,20-01-2020 20:45:50\n" +
                "420776562353,22-01-2020 05:30:00,22-01-2020 06:05:20\n" +
                "420777777777,25-01-2020 15:25:00,25-01-2020 16:30:45\n" +
                "420777777777,28-01-2020 20:45:00,28-01-2020 21:15:00";
        TelephoneBillCalculator calculator = new TelephoneBillCalculatorImpl();
        BigDecimal totalAmount = calculator.calculate(phoneLog);
        System.out.println("Total amount to pay: " + totalAmount + " Kč");
    }

}