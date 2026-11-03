package api;

import utils.config.ConfigFileReader;

public final class Endpoints {

    public static final String PRODUCTS = ConfigFileReader.get("products.endpoint");
    public static final String PRODUCT_BY_ID = PRODUCTS + "/{id}";
}
