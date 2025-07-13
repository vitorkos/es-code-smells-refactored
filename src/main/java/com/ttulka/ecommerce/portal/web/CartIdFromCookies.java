package com.ttulka.ecommerce.portal.web;

import java.util.Arrays;
import java.util.UUID;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.ttulka.ecommerce.sales.cart.CartId;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Retrieve and save Cart ID from/to HTTP cookies.
 */
@RequiredArgsConstructor
final class CartIdFromCookies {

    private final static String COOKIE_NAME = "CART_ID";

    private final @NonNull HttpServletRequest request;
    private final @NonNull HttpServletResponse response;

    private CartId cartId;

    public CartId cartId() {
        if (cartId == null) {
            String cartIdValue = extractCartIdFromCookies();
            cartId = new CartId(cartIdValue);
            saveCookie(cartId.value());
        }
        return cartId;
    }

    private String extractCartIdFromCookies() {
        if (request.getCookies() == null) {
            return generateNewCartId();
        }
        
        return Arrays.stream(request.getCookies())
                .filter(cookie -> COOKIE_NAME.equalsIgnoreCase(cookie.getName()))
                .map(Cookie::getValue)
                .findAny()
                .orElseGet(this::generateNewCartId);
    }

    private String generateNewCartId() {
        return UUID.randomUUID().toString();
    }

    private void saveCookie(String value) {
        response.addCookie(asCookie(COOKIE_NAME, value));
    }

    private Cookie asCookie(String name, String value) {
        var cookie = new Cookie(name, value);
        cookie.setPath("/");
        return cookie;
    }
}
