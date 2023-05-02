// There are better ways to handle this and given more time, we need to find a way to
// gracefully remove products even if there's a reference to it in other tables
// However, that's another rabbit hole to jump into and I think I'll save that for later

DELETE FROM deal;
DELETE FROM product;
DELETE FROM basket;
DELETE FROM basket_line_item;

INSERT INTO product(id, name, price_amount,price_currency)
VALUES (1, 'Macbook Pro', 1299, 'HKD');
