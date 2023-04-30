package com.bullish.checkout.domain;

import io.hypersistence.utils.hibernate.type.money.MonetaryAmountType;
import jakarta.persistence.*;
import org.hibernate.annotations.CompositeType;
import org.javamoney.moneta.Money;

import java.math.BigDecimal;

@Entity
@Table(name = "deal")
public class Deal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "deal_type")
    private DealType type;

    @Column(name = "minimum_quantity")
    private Long minimumQuantity;

    @Column(name = "maximum_quantity")
    private Long maximumQuantity;

    @Column(name = "discount_percentage")
    private BigDecimal discountPercentage;

    @AttributeOverride(
            name = "flat_discount_amount",
            column = @Column(name = "flat_discount_amount")
    )
    @AttributeOverride(
            name = "flat_discount_currency",
            column =  @Column(name = "flat_discount_currency")
    )
    @CompositeType(MonetaryAmountType.class)
    private Money flatDiscount;

    @ManyToOne
    @MapsId
    @JoinColumn(name = "product_id")
    private Product product;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(Long minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public Long getMaximumQuantity() {
        return maximumQuantity;
    }

    public void setMaximumQuantity(Long maximumQuantity) {
        this.maximumQuantity = maximumQuantity;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Money getFlatDiscount() {
        return flatDiscount;
    }

    public void setFlatDiscount(Money flatDiscount) {
        this.flatDiscount = flatDiscount;
    }
}
