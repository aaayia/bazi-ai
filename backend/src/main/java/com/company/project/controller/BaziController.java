package com.company.project.controller;

import com.company.project.common.result.Result;
import com.company.project.model.dto.CalculationDTO;
import com.company.project.model.vo.CalculationVO;
import com.company.project.service.BaziService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bazi")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Allow all for development
public class BaziController {

    private final BaziService baziService;

    @PostMapping("/calculate")
    public Result<CalculationVO> calculate(@Valid @RequestBody CalculationDTO dto) {
        CalculationVO vo = baziService.calculate(dto);
        return Result.success(vo);
    }
}
