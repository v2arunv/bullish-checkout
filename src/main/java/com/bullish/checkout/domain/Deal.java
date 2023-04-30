package com.bullish.checkout.domain;

import com.bullish.checkout.InvalidDealException;
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
            name = "amount",
            column = @Column(name = "flat_discount_amount")
    )
    @AttributeOverride(
            name = "currency",
            column =  @Column(name = "flat_discount_currency")
    )
    @CompositeType(MonetaryAmountType.class)
    private Money flatDiscount;

    @ManyToOne
    @MapsId
    @JoinColumn(name = "product_id")
    private Product product;

    public DealType getType() {
        return type;
    }

    public void setType(DealType type) {
        this.type = type;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getMinimumQuantity() {
        return minimumQuantity;
    }

    private void setMinimumQuantity(Long minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public Long getMaximumQuantity() {
        return maximumQuantity;
    }

    private void setMaximumQuantity(Long maximumQuantity) {
        this.maximumQuantity = maximumQuantity;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    private void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Money getFlatDiscount() {
        return flatDiscount;
    }

    private void setFlatDiscount(Money flatDiscount) {
        this.flatDiscount = flatDiscount;
    }

    @Override
    public String toString() {
        return "Deal{" +
                "id=" + id +
                ", type=" + type +
                ", minimumQuantity=" + minimumQuantity +
                ", maximumQuantity=" + maximumQuantity +
                ", discountPercentage=" + discountPercentage +
                ", flatDiscount=" + flatDiscount +
                ", product=" + product +
                '}';
    }

    public static class Builder {

        private Product product;
        private DealType dealType;

        private Long minimumQuantity;
        private Long maximumQuantity;

        private Money flatDiscount;

        private BigDecimal discountPercentage;

        public Builder(Product product, DealType dealType) {
            this.product = product;
            this.dealType = dealType;
        }

        public Builder minimumQuantity(Long quantity) {
            this.minimumQuantity = quantity;
            return this;
        }

        public Builder maximumQuantity(Long quantity) {
            this.maximumQuantity = quantity;
            return this;
        }

        public Builder flatDiscount(Money discount) throws InvalidDealException {
            if (this.dealType == DealType.PERCENTAGE) {
                throw new InvalidDealException("This deal is a percentage type. A flat discount amount cannot be set");
            }
            this.flatDiscount = discount;
            return this;
        }

        public Builder discountPercentage(BigDecimal percentage) throws InvalidDealException {
            if (this.dealType == DealType.FLAT_AMOUNT) {
                throw new InvalidDealException("This deal is a flat discount type. A flat discount amount cannot be set");
            }
            this.discountPercentage = percentage;
            return this;
        }

        public Deal build() {
            Deal deal =  new Deal();
            deal.setProduct(product);
            deal.setType(dealType);
            deal.setMinimumQuantity(minimumQuantity);
            deal.setMaximumQuantity(maximumQuantity);
            if (dealType == DealType.FLAT_AMOUNT) {
                deal.setFlatDiscount(flatDiscount);
            } else {
                deal.setDiscountPercentage(discountPercentage);
            }
            return deal;
        }
    }
}