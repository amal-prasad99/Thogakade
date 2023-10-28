package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    private String code;
    private String description;
    private double unitPrice;
    private int qtyOnHand;

    public void setQtyOnHand(int qty){
        qtyOnHand = qty>=0? qty : 0;
    }
    public void setUnitePrice(double price){
        unitPrice = price>=0? price : 0;
    }

}
