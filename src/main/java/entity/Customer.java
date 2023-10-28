package entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class Customer{
    private String id;
    private String name;
    private String address;
    private double salary;

    public void setSalary(double salary) {
        if(salary>=0){
            this.salary = salary;
        }
    }


}
