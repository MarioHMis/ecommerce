package com.marware.ecommerce.controller;

import com.marware.ecommerce.security.RoleConstants;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/admin")
    @PreAuthorize(RoleConstants.HAS_ADMIN)
    public String adminAccess() {
        return "Solo los ADMIN pueden ver esto";
    }

    @GetMapping("/seller")
    @PreAuthorize(RoleConstants.HAS_SELLER)
    public String sellerAccess() {
        return "Solo los SELLER pueden ver esto";
    }

    @GetMapping("/buyer")
    @PreAuthorize(RoleConstants.HAS_BUYER)
    public String buyerAccess() {
        return "Solo los CUSTOMER pueden ver esto";
    }

    @GetMapping("/any")
    @PreAuthorize(RoleConstants.HAS_ADMIN_OR_SELLER)
    public String adminOrSellerAccess() {
        return "ADMIN o SELLER pueden ver esto";
    }
}
