ALTER TABLE inventory
    ADD CONSTRAINT uq_product_name
        UNIQUE (product_name);