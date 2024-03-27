package project.snow;

import java.io.Serializable;

/**
 * Customer Class.
 *
 * We actually use this class in order to create and control Customer objects.
 * Those Customer objects are the ones containing each customer information.
 *
 * @author thanoskalantzis
 */
public class Customer implements Serializable {
    private String first_name;
    private String last_name;
    private String email;
    private String password;
    private long phone;

    //Constructors
    Customer(){}
    Customer(String first_name, String last_name, String email, String password, long phone){
        this.first_name=first_name;
        this.last_name=last_name;
        this.email=email;
        this.password=password;
        this.phone=phone;
    }

    //Setters
    public void setFirstName(String first_name){
        this.first_name=first_name;
    }
    public void setLastName(String last_name){
        this.last_name=last_name;
    }
    public void setEmail(String email){
        this.email=email;
    }
    public void setPassword(String password){
        this.password=password;
    }
    public void setPhone(long phone){
        this.phone=phone;
    }

    //Getters
    public String getFirstName(){
        return this.first_name;
    }
    public String getLastName(){
        return this.last_name;
    }
    public String getEmail(){
        return this.email;
    }
    public String getPassword(){
        return this.password;
    }
    public long getPhone(){
        return this.phone;
    }

    //toString()
    @Override
    public String toString(){
        return String.valueOf(this.first_name+" "+this.last_name+" "+this.email+" "+this.phone);
    }
}
