package vod.helpers;

import java.util.ArrayList;
import java.util.List;

public class StaticFactory
{
    public static List<String> getSortableMovieProperties()
    {
        return new ArrayList<String>(){{
            add("title");
            add("releaseyear");
            add("id");
            add("likes");
            add("views");
            add("dislikes");
            add("overallrating");
        }};
    }


    public static List<String> getMovieGenres()
    {
        return new ArrayList<String>(){{
            add("action");
            add("adventure");
            add("comedy");
            add("crime");
            add("drame");
            add("fantasy");
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
