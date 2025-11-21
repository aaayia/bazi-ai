package com.company.project.service.impl;

import cn.hutool.core.date.DateUtil;
import com.company.project.manager.DeepSeekManager;
import com.company.project.model.dto.AnalysisDTO;
import com.company.project.model.dto.CalculationDTO;
import com.company.project.model.vo.CalculationVO;
import com.company.project.service.BaziService;
import com.nlf.calendar.EightChar;
import com.nlf.calendar.Lunar;
import com.nlf.calendar.Solar;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BaziServiceImpl implements BaziService {

    private final DeepSeekManager deepSeekManager;

    @Override
    public CalculationVO calculate(CalculationDTO dto) {
        // 1. Parse Date
        String[] dateParts = dto.getBirthDate().split("-");
        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]);
        int day = Integer.parseInt(dateParts[2]);

        // Parse Time (Simple mapping for MVP)
        int hour = parseTime(dto.getBirthTime());

        // 2. Calculate Bazi using Lunar library
        Solar solar = Solar.fromYmdHms(year, month, day, hour, 0, 0);
        Lunar lunar = solar.getLunar();
        EightChar eightChar = lunar.getEightChar();

        // 3. Build VO
        CalculationVO vo = new CalculationVO();
        vo.setSolarDate(dto.getBirthDate());
        vo.setLunarDate(lunar.toString());

        CalculationVO.Bazi bazi = new CalculationVO.Bazi();
        bazi.setYear(eightChar.getYear());
        bazi.setMonth(eightChar.getMonth());
        bazi.setDay(eightChar.getDay());
        bazi.setTime(eightChar.getTime());
        vo.setBazi(bazi);

        // 4. Calculate Wuxing
        vo.setWuxing(calculateWuxing(eightChar));
        vo.setZodiac(lunar.getYearShengXiao());

        return vo;
    }

    @Override
    @Cacheable(value = "baziAnalysis", key = "#dto.solarDate + '-' + #dto.year + '-' + #dto.month + '-' + #dto.day + '-' + #dto.time")
    public CalculationVO.Analysis getAnalysis(AnalysisDTO dto) {
        log.info("Cache miss - calling DeepSeek API for: {}", dto);
        String prompt = buildPromptFromDTO(dto);
        return deepSeekManager.getAnalysis(prompt);
    }

    private String buildPromptFromDTO(AnalysisDTO dto) {
        return String.format(
                "请根据以下八字进行性格分析和五行建议：\\n" +
                        "阳历：%s\\n" +
                        "八字：%s %s %s %s\\n" +
                        "请返回 JSON 格式，包含 character (性格), suggestion (建议), poetry (诗词)。",
                dto.getSolarDate(),
                dto.getYear(), dto.getMonth(), dto.getDay(), dto.getTime());
    }

    private int parseTime(String timeStr) {
        if (timeStr == null || timeStr.contains("不详"))
            return 12; // Default to noon
        if (timeStr.contains("子"))
            return 23;
        if (timeStr.contains("丑"))
            return 1;
        if (timeStr.contains("寅"))
            return 3;
        if (timeStr.contains("卯"))
            return 5;
        if (timeStr.contains("辰"))
            return 7;
        if (timeStr.contains("巳"))
            return 9;
        if (timeStr.contains("午"))
            return 11;
        if (timeStr.contains("未"))
            return 13;
        if (timeStr.contains("申"))
            return 15;
        if (timeStr.contains("酉"))
            return 17;
        if (timeStr.contains("戌"))
            return 19;
        if (timeStr.contains("亥"))
            return 21;
        try {
            return Integer.parseInt(timeStr.split(":")[0]);
        } catch (Exception e) {
            return 12;
        }
    }

    private CalculationVO.Wuxing calculateWuxing(EightChar eightChar) {
        CalculationVO.Wuxing wuxing = new CalculationVO.Wuxing();
        int wood = 0;
        int fire = 0;
        int earth = 0;
        int gold = 0;
        int water = 0;

        // Iterate through all 4 pillars (Year, Month, Day, Time)
        // Each pillar has a Stem (Gan) and a Branch (Zhi)
        // We count the WuXing of each.

        List<String> chars = new ArrayList<>();
        chars.add(eightChar.getYearGan());
        chars.add(eightChar.getYearZhi());
        chars.add(eightChar.getMonthGan());
        chars.add(eightChar.getMonthZhi());
        chars.add(eightChar.getDayGan());
        chars.add(eightChar.getDayZhi());
        chars.add(eightChar.getTimeGan());
        chars.add(eightChar.getTimeZhi());

        for (String c : chars) {
            String element = getWuXingByChar(c);
            if ("木".equals(element))
                wood++;
            else if ("火".equals(element))
                fire++;
            else if ("土".equals(element))
                earth++;
            else if ("金".equals(element))
                gold++;
            else if ("水".equals(element))
                water++;
        }

        wuxing.setWood(wood);
        wuxing.setFire(fire);
        wuxing.setEarth(earth);
        wuxing.setGold(gold);
        wuxing.setWater(water);

        List<String> missing = new ArrayList<>();
        if (wood == 0)
            missing.add("木");
        if (fire == 0)
            missing.add("火");
        if (earth == 0)
            missing.add("土");
        if (gold == 0)
            missing.add("金");
        if (water == 0)
            missing.add("水");
        wuxing.setMissing(missing);

        return wuxing;
    }

    private String getWuXingByChar(String c) {
        // Stems
        if ("甲".equals(c) || "乙".equals(c))
            return "木";
        if ("丙".equals(c) || "丁".equals(c))
            return "火";
        if ("戊".equals(c) || "己".equals(c))
            return "土";
        if ("庚".equals(c) || "辛".equals(c))
            return "金";
        if ("壬".equals(c) || "癸".equals(c))
            return "水";

        // Branches
        if ("寅".equals(c) || "卯".equals(c))
            return "木";
        if ("巳".equals(c) || "午".equals(c))
            return "火";
        if ("辰".equals(c) || "戌".equals(c) || "丑".equals(c) || "未".equals(c))
            return "土";
        if ("申".equals(c) || "酉".equals(c))
            return "金";
        if ("亥".equals(c) || "子".equals(c))
            return "水";

        return "";
    }

    private String buildPrompt(CalculationVO vo) {
        return String.format(
                "请根据以下八字进行性格分析和五行建议：\n" +
                        "阳历：%s\n" +
                        "八字：%s %s %s %s\n" +
                        "请返回 JSON 格式，包含 character (性格), suggestion (建议), poetry (诗词)。",
                vo.getSolarDate(),
                vo.getBazi().getYear(), vo.getBazi().getMonth(), vo.getBazi().getDay(), vo.getBazi().getTime());
    }
}
