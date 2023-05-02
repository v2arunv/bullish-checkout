
INSERT INTO product(name, price_amount,price_currency)
VALUES ('Macbook Pro', 1299, 'HKD');

INSERT INTO product(name, price_amount,price_currency)
VALUES ('iPhone', 999, 'HKD');

INSERT INTO product(name, price_amount,price_currency)
VALUES ('AirPods Pro', 499, 'HKD');


-- Percentage Discount Deals
INSERT INTO deal(deal_type, minimum_quantity, maximum_quantity, discount_percentage, product_id)
VALUES ('PERCENTAGE', 1, 100, 10, 2);


INSERT INTO deal(deal_type, minimum_quantity, maximum_quantity, discount_percentage, product_id)
VALUES ('PERCENTAGE', 2, 20, 50, 1);


-- -- Flat Discount Deals
INSERT INTO deal(deal_type, minimum_quantity, maximum_quantity, flat_discount_amount, flat_discount_currency, product_id)
VALUES ('FLAT_AMOUNT', 1, 100, 500, 'HKD', 1);

