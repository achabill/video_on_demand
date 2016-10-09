package vod.models;

/**
 * Any property value instance.
 */
public class PropertyValue
{
    public String property;
    public String value;

    public PropertyValue(){}
    public PropertyValue(String property, String value)
    {
        this.property = property;
        this.value = value;
    }

    public String getProperty()
    {
        return property;
    }
    public String getValue()
    {
        return value;
    }
    public void setProperty(String property)
    {
        this.property = property;
    }
    public void setValue(String value)
    {
        this.value = value;
    }
}
