DELETE FROM deal;

INSERT INTO deal(deal_type, minimum_quantity, maximum_quantity, flat_discount_amount, flat_discount_currency, product_id)
VALUES ('FLAT_AMOUNT', 1, 100, 5000, 'HKD', 1); -- top pick

INSERT INTO deal(deal_type, minimum_quantity, maximum_quantity, discount_percentage, product_id)
VALUES ('PERCENTAGE', 5, 20, 50, 1);

INSERT INTO deal(deal_type, minimum_quantity, maximum_quantity, flat_discount_amount, flat_discount_currency, product_id)
VALUES ('FLAT_AMOUNT', 1, 100, 50, 'HKD', 2);

INSERT INTO deal(deal_type, minimum_quantity, maximum_quantity, discount_percentage, product_id)
VALUES ('PERCENTAGE', 2, 20, 50, 2); -- top pick