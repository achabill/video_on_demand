package vod.filestorage;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Path;
import java.util.stream.Stream;

@Service
public interface StorageService {

  void init();

  void store(MultipartFile file);

  Stream<Path> loadAll();

  Path load(String filename);

  Resource loadAsResource(String filename);

  void serve(String filename, HttpServletRequest request, HttpServletResponse response) throws Exception;

  void deleteAll();

  void delete(String filename);
}
