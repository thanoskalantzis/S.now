package project.snow;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * FinalOrder Class.
 *
 * We actually use this class in order to create and control FinalOrder objects.
 * FinalOrder objects consist of the variables being shown at the Class Variables Section.
 *
 * @author thanoskalantzis
 */
public class FinalOrder implements Serializable{
    //Class Variables Section.
    private int number_table;
    private String date_time;
    private ArrayList<OrderItem> orderItemList;
    private double finalPriceOfOrder;
    private boolean isActive;
    private String orderStatus;


    //Constructors
    FinalOrder(){
        this.orderItemList=new ArrayList<OrderItem>();
    }
    FinalOrder(int number_table, String date_time, ArrayList<OrderItem> orderItemList, boolean isActive, String orderStatus){
        this.number_table=number_table;
        this.date_time=date_time;
        this.orderItemList=orderItemList;
        this.isActive=isActive;
        this.orderStatus=orderStatus;
        calculateFinalPriceOfOrder();
    }

    //Getters
    public int getNumberTable(){
        return this.number_table;
    }
    public String getDateTime(){
        return this.date_time;
    }
    public ArrayList<OrderItem> getOrderItemList(){
        return this.orderItemList;
    }
    public boolean getIsActive(){
        return this.isActive;
    }
    public double getFinalPriceOfOrder(){
        return this.finalPriceOfOrder;
    }
    public String getOrderStatus(){
        return this.orderStatus;
    }


    //Setters
    public void setNumberTable(int number_table){
        this.number_table=number_table;
    }
    public void setDateTime(String date_time){
        this.date_time=date_time;
    }
    public void setOrderItemList(ArrayList<OrderItem> orderItemList){
        this.orderItemList=orderItemList;
    }
    public void setIsActive(boolean isActive){
        this.isActive=isActive;
    }
    public void setOrderStatus(String orderStatus){
        this.orderStatus=orderStatus;
    }

    //calculateFinalPriceOfOrder method
    private void calculateFinalPriceOfOrder(){
        double finalPriceOfOrder = 0;
        for(int i=0; i<orderItemList.size(); i++){
            finalPriceOfOrder = finalPriceOfOrder +  (orderItemList.get(i).getQuantity() * orderItemList.get(i).getProduct().getPrice());
        }
    }
/*
    //toString()
    @Override
    public String toString(){
        String tempString = new String("");
        tempString=tempString+this.number_table+", "+this.date_time;

        for(int i=0; i<this.orderItemList.size(); i++){
            tempString=tempString+", "+this.orderItemList.get(i).getProduct().toString()+", "+this.orderItemList.get(i).getQuantity();
        }

        return tempString;
    }

 */
}
