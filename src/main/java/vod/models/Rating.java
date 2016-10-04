package vod.models;

/**
 * A rating object.
 */
public class Rating
{
    //region Properties
    /**
     * The number of one star ratings the item has got.
     */
    private int onestar;
    /**
     * The number of two star ratings the item has got.
     */
    private int twostars;
    /**
     * The number of three star ratings the item has got.
     */
    private int threestars;
    /**
     * The number of four star ratings the item has got.
     */
    private int fourstars;
    /**
     * The number of five star ratings the item has got.
     */
    private int fivestars;
    //endregion

    //region Constructors
    public Rating()
    {}
    //endregion

    //region Getters
    /**
     * Gets the {@link Rating#onestar} instance.
     * @return the one star rating.
     */
    public int getOnestar()
    {
        return this.onestar;
    }

    /**
     * Gets the {@link Rating#twostars} instance.
     * @return the 2 star rating.
     */
    public int getTwostars()
    {
        return this.twostars;
    }

    /**
     * Gets the {@link Rating#threestars} instance.
     * @return the 3 star rating.
     */
    public int getThreestars()
    {
        return this.threestars;
    }

    /**
     * Gets the {@link Rating#fourstars} instance.
     * @return 4 star ratings.
     */
    public int getFourstars()
    {
        return this.fourstars;
    }

    /**
     * Gets the {@link Rating#fivestars} instance.
     * @return 5 star ratings.
     */
    public int getFivestars()
    {
        return this.fivestars;
    }
    //endregion

    //region Setters
    /**
     * Sets the {@link Rating#onestar} instance.
     * @param onestar The {@link Rating#onestar} instance.
     */
    public void setOnestar(int onestar)
    {
        this.onestar = onestar;
    }

    /**
     * Sets the {@link Rating#twostars} instance.
     * @param twostars The {@link Rating#twostars} instance.
     */
    public void setTwostars(int twostars)
    {
        this.twostars = twostars;
    }

    /**
     * Sets the {@link Rating#threestars} instance.
     * @param threestars The {@link Rating#threestars} instance.
     */
    public void setThreestars(int threestars)
    {
        this.threestars = threestars;
    }

    /**
     * Sets the {@link Rating#fourstars} instance.
     * @param fourstars The {@link Rating#fourstars} instance.
     */
    public void setFourstars(int fourstars)
    {
        this.fourstars = fourstars;
    }

    /**
     * Set the {@link Rating#fivestars} instance.
     * @param fivestars The {@link Rating#fivestars} instance.
     */
    public void setFivestars(int fivestars)
    {
        this.fivestars = fivestars;
    }
    //endregion
}
