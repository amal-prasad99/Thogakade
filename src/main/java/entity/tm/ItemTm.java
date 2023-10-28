package entity.tm;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemTm extends RecursiveTreeObject<ItemTm> {

    private String code;
    private String description;
    private double unitPrice;
    private int qtyOnHand;
    private JFXButton btn;

    public void setQtyOnHand(int qty){
        qtyOnHand = qty>=0? qty : 0;
    }
    public void setUnitePrice(double price){
        unitPrice = price>=0? price : 0;
    }

}
