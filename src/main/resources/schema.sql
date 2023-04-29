DROP TABLE IF EXISTS product;
CREATE TABLE product (
    id int not null AUTO_INCREMENT,
    name varchar(100) not null,
    price_amount decimal not null,
    price_currency varchar(100) not null
);