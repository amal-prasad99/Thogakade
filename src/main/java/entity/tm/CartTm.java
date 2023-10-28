package entity.tm;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CartTm extends RecursiveTreeObject<CartTm> {
    private String code;
    private String description;
    private Double unitPrice;
    private int qtyOnHand;
    private double amount;
    private JFXButton btn;



}
