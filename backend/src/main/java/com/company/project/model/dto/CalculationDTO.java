package com.company.project.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CalculationDTO {
    @NotNull(message = "出生日期不能为空")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "日期格式需为 yyyy-MM-dd")
    private String birthDate; // 阳历: 2025-11-20

    @NotNull(message = "出生时辰不能为空")
    private String birthTime; // 例如: "子时" 或 "23:00"
}
