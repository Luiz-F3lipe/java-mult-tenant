package com.plasoft.trez_api.controller;

import com.plasoft.trez_api.config.TenantContext;
import com.plasoft.trez_api.entity.Coupon;
import com.plasoft.trez_api.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/coupons")
public class CouponController {
    private final CouponRepository repository;

    @GetMapping("/{bd}")
    public List<Coupon> getCoupons(@PathVariable String bd) {
        TenantContext.getInstance().setCurrentTenant(bd);

        return repository.findAll();
    }
}
