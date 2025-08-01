package com.ttulka.ecommerce.portal.web;

import java.util.Map;

import com.ttulka.ecommerce.common.constants.PaginationConstants;
import com.ttulka.ecommerce.sales.catalog.FindProducts;
import com.ttulka.ecommerce.sales.catalog.FindProductsFromCategory;
import com.ttulka.ecommerce.sales.catalog.category.Uri;
import com.ttulka.ecommerce.sales.catalog.product.Product;
import com.ttulka.ecommerce.warehouse.InStock;
import com.ttulka.ecommerce.warehouse.ProductId;
import com.ttulka.ecommerce.warehouse.Warehouse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Web controller for Catalog use-cases.
 */
@Controller
@RequiredArgsConstructor
class CatalogController {

    private final @NonNull FindProducts products;
    private final @NonNull FindProductsFromCategory fromCategory;
    private final @NonNull Warehouse warehouse;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("products", products.all()
                .range(PaginationConstants.DEFAULT_MAX_RESULTS).stream()
                .map(this::toProductData)
                .toArray());
        return "catalog";
    }

    @GetMapping("/category/{categoryUri}")
    public String category(@PathVariable @NonNull String categoryUri, Model model) {
        model.addAttribute("products", fromCategory.byUri(new Uri(categoryUri))
                .range(PaginationConstants.DEFAULT_MAX_RESULTS).stream()
                .map(this::toProductData)
                .toArray());
        return "catalog";
    }

    private Map<String, Object> toProductData(Product product) {
        return Map.of(
                "id", product.id().value(),
                "title", product.title().value(),
                "description", product.description().value(),
                "price", product.price().value(),
                "inStock", getProductStock(product).amount().value()
        );
    }

    private InStock getProductStock(Product product) {
        return warehouse.leftInStock(new ProductId(product.id().value()));
    }
}