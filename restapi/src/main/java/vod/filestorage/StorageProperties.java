package vod.filestorage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("storage")
public class StorageProperties {

  /**
   * Folder location for storing files
   */
  private static String location = "upload-dir";

  public static String getLocation() {
    return location;
  }

  public static void setLocation(String location) {
    location = location;
  }

}
