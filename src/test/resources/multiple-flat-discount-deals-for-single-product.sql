DELETE FROM deal;

INSERT INTO deal(deal_type, minimum_quantity, maximum_quantity, flat_discount_amount, flat_discount_currency, product_id)
VALUES ('FLAT_AMOUNT', 5, 10, 100, 'HKD', 2);

INSERT INTO deal(deal_type, minimum_quantity, maximum_quantity, flat_discount_amount, flat_discount_currency, product_id)
VALUES ('FLAT_AMOUNT', 3, 100, 200, 'HKD', 2);