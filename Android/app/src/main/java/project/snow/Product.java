package project.snow;

import java.io.Serializable;

/**
 * Product Class.
 *
 * This class creates object containing all information about a specific product.
 *
 * @author thanoskalantzis
 */
public class Product implements Serializable {
    //Class Variables.
    private int product_id;
    private int category_id;
    private String product_name;
    private String description;
    private double price;


    //Constructors
    Product(){}
    Product(int product_id, int category_id, String product_name, String description, double price){
        this.product_id=product_id;
        this.category_id=category_id;
        this.product_name=product_name;
        this.description=description;
        this.price=price;
    }
    Product(Product product){
        this.product_id = (int) new Integer(product.getProductId());
        this.category_id = (int) new Integer(product.getCategoryId());
        this.product_name = new String(product.getProductName());
        this.description = new String(product.getDescription());
        this.price = (double) new Double(product.getPrice());
    }

    //Setters
    public void setProductId(int product_id){
        this.product_id=product_id;
    }
    public void setCategoryId(int category_id){
        this.category_id=category_id;
    }
    public void setProductName(String product_name){
        this.product_name=product_name;
    }
    public void setDescription(String description){
        this.description=description;
    }
    public void setPrice(double price){
        this.price=price;
    }

    //Getters
    public int getProductId(){
        return this.product_id;
    }
    public int getCategoryId(){
        return this.category_id;
    }
    public String getProductName(){
        return this.product_name;
    }
    public String getDescription(){
        return this.description;
    }
    public double getPrice(){
        return this.price;
    }

    //toString()
    @Override
    public String toString(){
        return String.valueOf(this.product_name+"\n\n"+this.description+" "+this.price+"€");
    }
}
