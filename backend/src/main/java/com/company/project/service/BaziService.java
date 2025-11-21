package com.company.project.service;

import com.company.project.model.dto.AnalysisDTO;
import com.company.project.model.dto.CalculationDTO;
import com.company.project.model.vo.CalculationVO;

public interface BaziService {
    CalculationVO calculate(CalculationDTO dto);

    CalculationVO.Analysis getAnalysis(AnalysisDTO dto);
}
