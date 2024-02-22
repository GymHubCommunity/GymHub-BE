package com.example.temp.common.domain.period;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MonthlyDatePeriod implements DatePeriod {

    private LocalDate firstDate;
    private LocalDate lastDate;

    public static DatePeriod of(int year, int month) {
        LocalDate firstDate = LocalDate.of(year, month, 1);
        LocalDate lastDate = firstDate.withDayOfMonth(firstDate.lengthOfMonth());
        return new MonthlyDatePeriod(firstDate, lastDate);
    }

    @Override
    public LocalDate getFirstDate() {
        return firstDate;
    }

    @Override
    public LocalDate getLastDate() {
        return lastDate;
    }
}
