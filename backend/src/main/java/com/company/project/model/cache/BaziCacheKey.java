package com.company.project.model.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaziCacheKey implements Serializable {
    private String solarDate;
    private String year;
    private String month;
    private String day;
    private String time;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BaziCacheKey that = (BaziCacheKey) o;
        return Objects.equals(solarDate, that.solarDate) &&
                Objects.equals(year, that.year) &&
                Objects.equals(month, that.month) &&
                Objects.equals(day, that.day) &&
                Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(solarDate, year, month, day, time);
    }
}
