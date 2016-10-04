package vod.helpers;

import java.util.ArrayList;
import java.util.List;

public class StaticFactory
{
    public static List<String> sortableMovieProperties()
    {
        return new ArrayList<String>(){{
            add("title");
            add("releasedate");
            add("id");
            add("likes");
            add("view");
            add("dislikes");
            add("overralrating");
        }};
    }


    public static List<String> movieGenres()
    {
        return new ArrayList<String>(){{
            add("action");
            add("adventure");
            add("comedy");
            add("crime");
            add("drame");
            add("Fantasy");
            add("historical");
            add("historical_fiction");
            add("horror");
            add("magical_realism");
            add("mystery");
            add("paranoid");
            add("philosophical");
            add("political");
            add("romance");
            add("saga");
            add("satire");
            add("science_Fiction");
            add("slice_of_life");
            add("speculative");
            add("thriller");
            add("urban");
            add("western");
            add("animation");
        }};
    }
}
