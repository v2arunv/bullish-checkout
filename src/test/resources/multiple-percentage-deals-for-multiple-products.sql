DELETE FROM deal;

INSERT INTO deal(deal_type, minimum_quantity, maximum_quantity, discount_percentage, product_id)
VALUES ('PERCENTAGE', 5, 20, 25, 1); -- top pick

INSERT INTO deal(deal_type, minimum_quantity, maximum_quantity, discount_percentage, product_id)
VALUES ('PERCENTAGE', 3, 10, 10, 1);

INSERT INTO deal(deal_type, minimum_quantity, maximum_quantity, discount_percentage, product_id)
VALUES ('PERCENTAGE', 5, 15, 30, 2); -- top pick

INSERT INTO deal(deal_type, minimum_quantity, maximum_quantity, discount_percentage, product_id)
VALUES ('PERCENTAGE', 3, 10, 15, 2);
