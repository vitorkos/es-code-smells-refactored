package com.ttulka.ecommerce.portal.web;

import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.ttulka.ecommerce.portal.PlaceOrderFromCart;
import com.ttulka.ecommerce.portal.PrepareOrderDelivery;
import com.ttulka.ecommerce.sales.cart.Cart;
import com.ttulka.ecommerce.sales.cart.RetrieveCart;
import com.ttulka.ecommerce.shipping.delivery.Address;
import com.ttulka.ecommerce.shipping.delivery.Person;
import com.ttulka.ecommerce.shipping.delivery.Place;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Web controller for Order use-cases.
 */
@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
class OrderController {

    private final @NonNull RetrieveCart retrieveCart;
    private final @NonNull PlaceOrderFromCart placeOrderFromCart;
    private final @NonNull PrepareOrderDelivery prepareOrderDelivery;

    @GetMapping
    public String index() {
        return "order";
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String place(@NonNull String name, @NonNull String address,
                        HttpServletRequest request, HttpServletResponse response) {
        Cart cart = retrieveCart.byId(new CartIdFromCookies(request, response).cartId());
        UUID orderId = UUID.randomUUID();

        placeOrderFromCart.placeOrder(orderId, cart);

        prepareOrderDelivery.prepareDelivery(
                orderId,
                new Address(new Person(name), new Place(address)));

        cart.empty();

        return "redirect:/order/success";
    }

    @GetMapping("/success")
    public String success(HttpServletRequest request, HttpServletResponse response) {
        return "order-success";
    }

    @GetMapping("/error")
    public String error(String message, Model model) {
        model.addAttribute("messageCode", message);
        return "order-error";
    }

    @ExceptionHandler({PlaceOrderFromCart.NoItemsToOrderException.class, IllegalArgumentException.class})
    String exception(Exception ex) {
        return "redirect:/order/error?message=" + getErrorCode(ex);
    }

    private String getErrorCode(Exception e) {
        if (e instanceof PlaceOrderFromCart.NoItemsToOrderException) {
            return "noitems";
        }
        if (e instanceof IllegalArgumentException) {
            return "requires";
        }
        return "default";
    }
}
