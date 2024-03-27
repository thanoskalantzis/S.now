package project.snow;

import java.io.Serializable;

/**
 * Business Class.
 *
 * We actually use this class in order to create and control Business objects.
 * Those Business objects are the ones containing each business information.
 *
 * @author thanoskalantzis
 */
public class Business implements Serializable {
    //Class variables.
    private String email;
    private String password;
    private String business_id_name;
    private int afm_business;
    private long phone;
    private String address;
    private int postal_code;
    private int number_tables;

    //Constructors.
    Business(){}
    Business(String email, String password, String business_id_name, int afm_business, long phone, String address, int postal_code, int number_tables){
        this.email=email;
        this.password=password;
        this.business_id_name=business_id_name;
        this.afm_business=afm_business;
        this.phone=phone;
        this.address=address;
        this.postal_code=postal_code;
        this.number_tables=number_tables;
    }

    //Setters
    public void setEmail(String email){
        this.email=email;
    }
    public void setPassword(String password){
        this.password=password;
    }
    public void setBusinessIdName(String business_id_name){
        this.business_id_name=business_id_name;
    }
    public void setAfmBusiness(int afm_business){
        this.afm_business=afm_business;
    }
    public void setPhone(long phone){
        this.phone=phone;
    }
    public void setAddress(String address){
        this.address=address;
    }
    public void setPostalCode(int postal_code){
        this.postal_code=postal_code;
    }
    public void setNumberTables(int number_tables){
        this.number_tables=number_tables;
    }

    //Getters
    public String getEmail(){
        return this.email;
    }
    public String getPassword(){
        return this.password;
    }
    public String getBusinessIdName(){
        return this.business_id_name;
    }
    public int getAfmBusiness(){
        return this.afm_business;
    }
    public long getPhone(){
        return this.phone;
    }
    public String getAddress(){
        return this.address;
    }
    public int getPostalCode(){
        return this.postal_code;
    }
    public int getNumberTables(){
        return this.number_tables;
    }

    //toString() function
    @Override
    public String toString(){
        return String.valueOf(this.business_id_name+"\n\n"+this.address+", "+this.postal_code+", "+this.phone);
    }
}
