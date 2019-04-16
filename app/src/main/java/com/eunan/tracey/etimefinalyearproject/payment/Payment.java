package com.eunan.tracey.etimefinalyearproject.payment;

public class Payment {
    String date;
    String amount;

    public Payment(String date, String amount) {
        this.date = date;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "date='" + date + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }
}
