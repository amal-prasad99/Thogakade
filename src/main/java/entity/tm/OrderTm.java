package entity.tm;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderTm extends RecursiveTreeObject<OrderTm> {
    private String id;
    private String date;
    private String custName;
    private JFXButton btn;

}
