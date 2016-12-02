package vod.filearchive;


import archive.model.Document;
import archive.model.DocumentMetadata;
import archive.service.IArchiveService;
import org.apache.log4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Acha Bill <achabill12[at]gmail[dot]com>
 */

@Service
public class ArchiveServiceClient implements IArchiveService {

  private static final Logger LOG = Logger.getLogger(ArchiveServiceClient.class);

  String protocol = "http";
  String hostname = "localhost";
  Integer port = 8090;
  String baseUrl = "archive";

  RestTemplate restTemplate;

  @Override
  public DocumentMetadata save(Document document) {
    try {
      return doSave(document);
    } catch (RuntimeException e) {
      LOG.error("Error while uploading file", e);
      throw e;
    } catch (IOException e) {
      LOG.error("Error while uploading file", e);
      throw new RuntimeException("Error while uploading file", e);
    }
  }

  private DocumentMetadata doSave(Document document) throws IOException, FileNotFoundException {
    String tempFilePath = writeDocumentToTempFile(document);
    MultiValueMap<String, Object> parts = createMultipartFileParam(tempFilePath);
    String dateString = DocumentMetadata.DATE_FORMAT.format(document.getDocumentDate());
    DocumentMetadata documentMetadata = getRestTemplate().postForObject(getServiceUrl() + "/upload?person={name}&date={date}",
      parts,
      DocumentMetadata.class,
      document.getPersonName(),
      dateString);
    return documentMetadata;
  }

  @Override
   public void deleteAllDocuments() throws IOException {
    getRestTemplate().delete(getServiceUrl() + "/");
   }

  @Override
  public byte[] getDocumentFile(String id) {
    return getRestTemplate().getForObject(getServiceUrl() + "/document/{id}?full=true", byte[].class, id);
  }

  @Override
  public Object getDocumentMultipart(String id, HttpServletRequest request, HttpServletResponse response) throws Exception {
    getRestTemplate().getForObject(getServiceUrl() + "/document/{id}", Object.class, id);
    return null;
  }

  @Override
  public String deleteDocument(String id) throws IOException {
    getRestTemplate().delete(getServiceUrl() + "/document/{id}", id);
    return id;
  }

  @Override
  public List<DocumentMetadata> findDocuments(String personName, Date date, String contentType) {
    String dateString = null;
    if (date != null) {
      dateString = DocumentMetadata.DATE_FORMAT.format(date);
    }
    DocumentMetadata[] result = getRestTemplate().getForObject(getServiceUrl() + "documents?person={name}&date={date}&contenttype={contentType}", DocumentMetadata[].class, personName, dateString);
    return Arrays.asList(result);
  }

  private MultiValueMap<String, Object> createMultipartFileParam(String tempFilePath) {
    MultiValueMap<String, Object> parts = new LinkedMultiValueMap<String, Object>();
    parts.add("file", new FileSystemResource(tempFilePath));
    return parts;
  }

  private String writeDocumentToTempFile(Document document) throws IOException, FileNotFoundException {
    Path path;
    path = Files.createTempDirectory(document.getUuid());
    String tempDirPath = path.toString();
    File file = new File(tempDirPath, document.getFileName());
    FileOutputStream fo = new FileOutputStream(file);
    fo.write(document.getFileData());
    fo.close();
    return file.getPath();
  }

  public String getServiceUrl() {
    StringBuilder sb = new StringBuilder();
    sb.append(getProtocol()).append("://");
    sb.append(getHostname());
    if (getPort() != null) {
      sb.append(":").append(getPort());
    }
    sb.append("/").append(getBaseUrl()).append("/");
    return sb.toString();
  }

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public String getHostname() {
    return hostname;
  }

  public void setHostname(String hostname) {
    this.hostname = hostname;
  }

  public Integer getPort() {
    return port;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public RestTemplate getRestTemplate() {
    if (restTemplate == null) {
      restTemplate = createRestTemplate();
    }
    return restTemplate;
  }

  public void setRestTemplate(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  private RestTemplate createRestTemplate() {
    restTemplate = new RestTemplate();
    return restTemplate;
  }

}
