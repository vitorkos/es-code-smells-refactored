package com.ttulka.ecommerce.common.jdbc;

/**
 * SQL query builder utility.
 */
public final class SqlQueryBuilder {
    
    private SqlQueryBuilder() {
        // Utility class
    }
    
    public static String addOrderByClause(String baseQuery) {
        return baseQuery + " ORDER BY 1";
    }
    
    public static String addOrderByWithLimitClause(String baseQuery) {
        return baseQuery + " ORDER BY 1 LIMIT ?,?";
    }
}
