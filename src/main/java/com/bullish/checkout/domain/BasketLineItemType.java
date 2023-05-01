package com.bullish.checkout.domain;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.spi.ValueAccess;
import org.hibernate.usertype.CompositeUserType;

import java.io.Serializable;
import java.util.Objects;

public class BasketLineItemType implements CompositeUserType<BasketLineItem> {

    public static class BasketLineItemMapper {
        Product product;
        int quantity;
    }

    public BasketLineItemType() {}

    @Override
    public Object getPropertyValue(BasketLineItem component, int property) throws HibernateException {
        switch (property) {
            case 0:
                return component.getProduct().getId().longValue();
            case 1:
                return component.getQuantity();
        }
        return null;
    }

    @Override
    public BasketLineItem instantiate(ValueAccess values, SessionFactoryImplementor sessionFactory) {

        Product product = values.getValue(0, Product.class);
        int quantity = values.getValue(1, Integer.class);
        return new BasketLineItem(product, quantity);
    }

    @Override
    public Class<?> embeddable() {
        return BasketLineItemMapper.class;
    }

    @Override
    public Class<BasketLineItem> returnedClass() {
        return BasketLineItem.class;
    }

    @Override
    public boolean equals(BasketLineItem x, BasketLineItem y) {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(BasketLineItem x) {
        return x.hashCode();
    }

    @Override
    public BasketLineItem deepCopy(BasketLineItem value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(BasketLineItem value) {
        return (Serializable) value;
    }

    @Override
    public BasketLineItem assemble(Serializable cached, Object owner) {
        return (BasketLineItem) cached;
    }

    @Override
    public BasketLineItem replace(BasketLineItem detached, BasketLineItem managed, Object owner) {
        return detached;
    }
}
