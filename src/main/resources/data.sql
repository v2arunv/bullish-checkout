INSERT INTO product(id, name, price_amount,price_currency)
VALUES (1, 'Macbook Pro', 1299, 'HKD');

INSERT INTO product(id, name, price_amount,price_currency)
VALUES (2, 'iPhone', 999, 'HKD');

INSERT INTO product(id, name, price_amount,price_currency)
VALUES (3, 'AirPods Pro', 499, 'HKD');


-- Percentage Discount Deals
INSERT INTO deal(id, deal_type, minimum_quantity, maximum_quantity, discount_percentage, product_id)
VALUES (1, 'PERCENTAGE', 1, 100, 10, 2);

-- Flat Discount Deals
INSERT INTO deal(id, deal_type, minimum_quantity, maximum_quantity, flat_discount_amount, flat_discount_currency, product_id)
VALUES (2, 'FLAT_AMOUNT', 1, 100, 500, 'HKD', 1);