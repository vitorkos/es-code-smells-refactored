package com.ttulka.ecommerce.sales.catalog.jdbc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.ttulka.ecommerce.common.constants.PaginationConstants;
import com.ttulka.ecommerce.common.jdbc.SqlQueryBuilder;
import com.ttulka.ecommerce.common.primitives.Money;
import com.ttulka.ecommerce.sales.catalog.product.Description;
import com.ttulka.ecommerce.sales.catalog.product.Product;
import com.ttulka.ecommerce.sales.catalog.product.ProductId;
import com.ttulka.ecommerce.sales.catalog.product.Products;
import com.ttulka.ecommerce.sales.catalog.product.Title;

import org.springframework.jdbc.core.JdbcTemplate;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * JDBC implementation of Products collection.
 */
@RequiredArgsConstructor
final class ProductsJdbc implements Products {

    private final @NonNull String query;
    private final @NonNull List<Object> queryParams;

    private final @NonNull JdbcTemplate jdbcTemplate;

    private int start = 0;
    private int limit = PaginationConstants.UNLIMITED_RESULTS;

    public ProductsJdbc(@NonNull String query, @NonNull Object queryParam, @NonNull JdbcTemplate jdbcTemplate) {
        this(query, List.of(queryParam), jdbcTemplate);
    }

    public ProductsJdbc(@NonNull String query, @NonNull JdbcTemplate jdbcTemplate) {
        this(query, List.of(), jdbcTemplate);
    }

    @Override
    public Products range(int start, int limit) {
        if (start < 0 || limit <= 0 || limit - start > PaginationConstants.UNLIMITED_RESULTS) {
            throw new IllegalArgumentException("Start must be greater than zero, " +
                                               "items count must be greater than zero and less or equal than " + PaginationConstants.UNLIMITED_RESULTS);
        }
        this.start = start;
        this.limit = limit;
        return this;
    }

    @Override
    public Products range(int limit) {
        return range(0, limit);
    }

    @Override
    public Stream<Product> stream() {
        var params = new ArrayList<>(queryParams);
        params.add(start);
        params.add(limit);
        return jdbcTemplate.queryForList(SqlQueryBuilder.addOrderByWithLimitClause(query), params.toArray())
                .stream()
                .map(this::toProduct);
    }

    private Product toProduct(Map<String, Object> entry) {
        return new ProductJdbc(
                new ProductId(entry.get("id")),
                new Title((String) entry.get("title")),
                new Description((String) entry.get("description")),
                new Money(((BigDecimal) entry.get("price")).floatValue()),
                jdbcTemplate);
    }
}
