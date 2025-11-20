package com.company.project.model.vo;

import lombok.Data;
import java.util.List;

@Data
public class CalculationVO {
    private String solarDate;
    private String lunarDate;
    private Bazi bazi;
    private Wuxing wuxing;
    private String zodiac;
    private Analysis analysis;

    @Data
    public static class Bazi {
        private String year;
        private String month;
        private String day;
        private String time;
    }

    @Data
    public static class Wuxing {
        private Integer gold;
        private Integer wood;
        private Integer water;
        private Integer fire;
        private Integer earth;
        private List<String> missing;
    }

    @Data
    public static class Analysis {
        private String character;
        private String suggestion;
        private String poetry;
    }
}
