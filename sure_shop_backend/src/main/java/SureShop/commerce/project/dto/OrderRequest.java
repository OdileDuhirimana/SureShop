package SureShop.commerce.project.dto;

import lombok.Data;

@Data
public class OrderRequest {
    private String shippingAddress;
    private String notes;
}