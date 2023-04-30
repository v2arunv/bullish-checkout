DROP TABLE IF EXISTS deal CASCADE;
DROP TABLE IF EXISTS product CASCADE;

CREATE TABLE product (
    id int not null primary key AUTO_INCREMENT,
    name varchar(100) not null,
    price_amount decimal not null,
    price_currency varchar(100) not null
);

CREATE TABLE deal (
    id int not null primary key  AUTO_INCREMENT,
    deal_type varchar(100) not null,
    minimum_quantity number not null,
    maximum_quantity number,
    discount_percentage decimal,
    flat_discount_amount decimal,
    flat_discount_currency varchar(100),
    product_id int not null,
    FOREIGN KEY (product_id) references product(id)
);

alter sequence product_seq restart with 5;
