package vod.models;

import org.springframework.data.annotation.Id;

public class Customer
{
    @Id
    private String id;
    private String firstname;
    private String lastname;

    public Customer()
    {}

    public Customer(String firstName, String lastName)
    {
        this.firstname = firstName;
        this.lastname = lastName;
    }

    @Override
    public String toString()
    {
        return String.format("[Customer[id = %s, firstName = %s, lastName = %s]",id, firstname, lastname);
    }

    public String getId()
    {
        return this.id;
    }
    public String getFirstname()
    {
        return this.firstname;
    }
    public String getLastname()
    {
        return this.lastname;
    }
    public void setId(String id)
    {
        this.id = id;
    }
    public void setFirstname(String firstname)
    {
        this.firstname = firstname;
    }
    public void setLastname(String lastname)
    {
        this.lastname = lastname;
    }
}
