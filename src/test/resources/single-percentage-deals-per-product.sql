-- Percentage Discount Deals
DELETE FROM deal;

INSERT INTO deal(deal_type, minimum_quantity, maximum_quantity, discount_percentage, product_id)
VALUES ('PERCENTAGE', 5, 20, 50, 1);

INSERT INTO deal(deal_type, minimum_quantity, maximum_quantity, discount_percentage, product_id)
VALUES ('PERCENTAGE', 3, 10, 10, 2);
