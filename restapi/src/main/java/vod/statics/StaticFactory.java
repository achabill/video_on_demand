package vod.statics;

import java.util.ArrayList;
import java.util.List;

public class StaticFactory {
  public static List<String> getSortableMovieProperties() {
    return new ArrayList<String>() {{
      add("title");
      add("releaseyear");
      add("id");
      add("likes");
      add("views");
      add("dislikes");
      add("overallrating");
    }};
  }


  public static List<String> getMovieGenres() {
    return new ArrayList<String>() {{
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

  /**
   * Checks wether a movie property can be used to sort.
   *
   * @param property The property to sort with.
   * @return True when property is valid. False otherwise.
   */
  public static boolean isSortableProperty(String property) {
    return StaticFactory.getSortableMovieProperties().contains(property.toLowerCase());
  }

  /**
   * Checks if a string is a type of movie genre.
   *
   * @param genre The inquiery string.
   * @return True if the string is a movie genre. False otherwise.
   */
  public static boolean isMovieGenre(String genre) {
    return StaticFactory.getMovieGenres().contains(genre.toLowerCase());
  }
}
