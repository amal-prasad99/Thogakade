package entity.tm;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailsTm extends RecursiveTreeObject<OrderDetailsTm> {
    private String itemCode;
    private String desc;
    private int qty;
    private double Amount;
}

