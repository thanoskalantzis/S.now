package project.snow;

import java.io.Serializable;

/**
 * OrderItem class.
 *
 * @author thanoskalantzis
 */
public class OrderItem implements Serializable {
    //Class variables.
    private Product product;
    private int quantity;

    OrderItem(){}
    OrderItem(Product product, int quantity){
        this.product=product;
        this.quantity=quantity;
    }

    //Getters
    public Product getProduct(){
        return this.product;
    }
    public int getQuantity(){
        return this.quantity;
    }

    //Setters
    public void setProduct(Product product){
        this.product=product;
    }
    public void setQuantity(int quantity){
        this.quantity=quantity;
    }

    @Override
    public String toString(){
        return this.product.toString()+", "+this.quantity;
    }
}
