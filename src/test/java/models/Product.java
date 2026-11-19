package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.math.BigDecimal;

/** Request/response model for the product API. Extra JSON fields from the API are ignored. */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@XmlRootElement(name = "Product")
@XmlAccessorType(XmlAccessType.FIELD)
public class Product {

private Long id;

@JsonProperty("name")
@XmlElement(name = "name")
private String productName;

@JsonProperty("price")
@XmlElement(name = "price")
private BigDecimal price;

    public Product() {
    }

    public Product(String productName,BigDecimal price) {
        this.productName = productName;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", price=" + price +
                '}';
    }
}
// @XmlRootElement(name = "Product") makes the outer tag <Product>, matching your Insomnia request.
//@XmlAccessorType(FIELD) tells JAXB to read the fields directly, not the getters.
//Long id is important here too. JAXB omits a null Long, so <id> isn’t sent. A primitive
// long would write <id>0</id>.
//Keep a public no-argument constructor. Reading a response back with response.as(Product.class) needs one.
