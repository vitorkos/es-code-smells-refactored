package com.ttulka.ecommerce.common.primitives;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Money domain primitive.
 */
@EqualsAndHashCode
@ToString
public final class Money {

    public static final Money ZERO = new Money(0.f);

    private static final float MAX_VALUE = 1_000_000.f;

    private final float money;

    public Money(float money) {
        if (money < 0) {
            throw new IllegalArgumentException("Money cannot be less than zero.");
        }
        if (money > MAX_VALUE) {
            throw new IllegalArgumentException("Money cannot be greater than " + MAX_VALUE + ".");
        }
        this.money = money;
    }

    public Money add(Money summand) {
        return new Money(money + summand.value());
    }

    public Money subtract(Money subtrahend) {
        return new Money(money - subtrahend.value());
    }

    public Money multi(int factor) {
        return new Money(money * factor);
    }

    public Money divide(int divisor) {
        if (divisor == 0) {
            throw new IllegalArgumentException("Cannot divide by zero.");
        }
        return new Money(money / divisor);
    }

    public boolean isGreaterThan(Money other) {
        return money > other.value();
    }

    public boolean isLessThan(Money other) {
        return money < other.value();
    }

    public boolean isZero() {
        return Float.compare(money, 0.0f) == 0;
    }

    public float value() {
        return money;
    }
}
