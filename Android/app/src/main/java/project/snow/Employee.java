package project.snow;

import java.io.Serializable;

/**
 * Employee Class.
 *
 * We actually use this class in order to create and control Employee objects.
 * Those Employee objects are the ones containing each employee information.
 *
 * @author thanoskalantzis
 */
public class Employee implements Serializable {
    private int afmEmployee;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private long phone;

    //Constructors
    Employee(){}
    Employee(int afm, String fName, String lName, String mail, String pass, long number){
        this.afmEmployee = afm;
        this.firstName = fName;
        this.lastName = lName;
        this.email = mail;
        this.password = pass;
        this.phone = number;
    }

    //Setters
    public void setAfmEmployee(int afm){
        this.afmEmployee=afm;
    }
    public void setFirstName(String fName){
        this.firstName=fName;
    }
    public void setLastName(String lName){
        this.lastName=lName;
    }
    public void setEmail(String mail){
        this.email=mail;
    }
    public void setPassword(String pass){
        this.password=pass;
    }
    public void setPhone(long number){
        this.phone=number;
    }

    //Getters
    public int getAfmEmployee(){
        return this.afmEmployee;
    }
    public String getFirstName(){
        return this.firstName;
    }
    public String getLastName(){
        return this.lastName;
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
        return this.firstName+" "+this.lastName+"\nEmail: "+this.email+ ", Τηλέφωνο: "+this.phone+"\nΑΦΜ: "+this.afmEmployee;
    }

}
