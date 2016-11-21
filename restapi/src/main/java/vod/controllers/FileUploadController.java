package vod.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import vod.filestorage.StorageFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vod.filestorage.StorageService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Api(value = "File upload")
@RestController
@RequestMapping(value = "/upload")
public class FileUploadController {

    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @ApiOperation(value = "Get uploaded files")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public List<String> listUploadedFiles() throws IOException {

        return storageService.loadAll().map(path ->
                        MvcUriComponentsBuilder
                                .fromMethodName(FileUploadController.class, "serveFile", path.getFileName().toString())
                                .build().toString()).collect(Collectors.toList());
    }

    @ApiOperation(value = "Get uploaded file by filename")
    @RequestMapping(value = "/{filename:.+}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+file.getFilename()+"\"")
                .body(file);
    }

    @ApiOperation(value = "Post new file")
      @RequestMapping(value = "/", method = RequestMethod.POST)
      public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                     RedirectAttributes redirectAttributes) {

          storageService.store(file);
          return "You successfully uploaded " + file.getOriginalFilename() + "!";
      }

  @ApiOperation(value = "Get file path")
  @RequestMapping(value = "/path/{filename:.+}", method = RequestMethod.GET)
  public Path getpath(@PathVariable String filename) {

    return storageService.load(filename);
  }
      @ExceptionHandler(StorageFileNotFoundException.class)
      public ResponseEntity handleStorageFileNotFound(StorageFileNotFoundException exc) {
          return ResponseEntity.notFound().build();
      }

}
