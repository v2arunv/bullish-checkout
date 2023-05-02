DELETE FROM deal;

-- -- Flat Discount Deals
INSERT INTO deal(deal_type, minimum_quantity, maximum_quantity, flat_discount_amount, flat_discount_currency, product_id)
VALUES ('FLAT_AMOUNT', 1, 100, 500, 'HKD', 1);

INSERT INTO deal(deal_type, minimum_quantity, maximum_quantity, flat_discount_amount, flat_discount_currency, product_id)
VALUES ('FLAT_AMOUNT', 5, 10, 100, 'HKD', 2);

INSERT INTO deal(deal_type, minimum_quantity, maximum_quantity, flat_discount_amount, flat_discount_currency, product_id)
VALUES ('FLAT_AMOUNT', 10, 100, 200, 'HKD', 2);

INSERT INTO deal(deal_type, minimum_quantity, maximum_quantity, flat_discount_amount, flat_discount_currency, product_id)
VALUES ('FLAT_AMOUNT', 5, 10, 100, 'HKD', 3);

INSERT INTO deal(deal_type, minimum_quantity, maximum_quantity, flat_discount_amount, flat_discount_currency, product_id)
VALUES ('FLAT_AMOUNT', 10, 100, 26500, 'HKD', 3);