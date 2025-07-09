package com.marware.ecommerce.security;

public class RoleConstants {
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_SELLER = "ROLE_SELLER";
    public static final String ROLE_BUYER = "ROLE_CUSTOMER"; // si antes usabas BUYER, usa CUSTOMER si ese es el nombre real

    public static final String HAS_ADMIN = "hasRole('ROLE_ADMIN')";
    public static final String HAS_SELLER = "hasRole('ROLE_SELLER')";
    public static final String HAS_BUYER = "hasRole('ROLE_CUSTOMER')";

    public static final String HAS_ADMIN_OR_SELLER = "hasAnyRole('ROLE_ADMIN', 'ROLE_SELLER')";
    public static final String HAS_ADMIN_OR_BUYER = "hasAnyRole('ROLE_ADMIN', 'ROLE_CUSTOMER')";
}
