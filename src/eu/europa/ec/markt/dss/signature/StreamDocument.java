package eu.europa.ec.markt.dss.signature;


import eu.europa.ec.markt.dss.DSSUtils;
import eu.europa.ec.markt.dss.DigestAlgorithm;
import eu.europa.ec.markt.dss.exception.DSSException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @see eu.europa.ec.markt.dss.signature.DSSDocument implementation to handle big files. It writes data to temporary
 * files.
 */
public class StreamDocument implements DSSDocument {
  final Logger logger = LoggerFactory.getLogger(StreamDocument.class);

  private static final int MAX_SIZE_IN_MEMORY = 1024 * 5;
  String documentName;
  MimeType mimeType;
  File temporaryFile;

  //TODO if file is small enough you can read it into byte[] and cache it
  public StreamDocument(InputStream stream, String documentName, MimeType mimeType) {
    logger.debug("Document name: " + documentName + ", mime type: " + mimeType);
    createTemporaryFileOfStream(stream);
    this.documentName = documentName;
    this.mimeType = mimeType;
  }

  private void createTemporaryFileOfStream(InputStream stream) {
    logger.debug("");
    byte[] bytes = new byte[MAX_SIZE_IN_MEMORY];

    FileOutputStream out = null;

    try {
      temporaryFile = File.createTempFile("digidoc4j", ".tmp");
      out = new FileOutputStream(temporaryFile);
      int result;
      while ((result = stream.read(bytes)) > 0) {
        out.write(bytes, 0, result);
      }
      out.flush();
    } catch (IOException e) {
      logger.error(e.getMessage());
      throw new DSSException(e);
    } finally {
      IOUtils.closeQuietly(out);
    }
  }


  @Override
  public InputStream openStream() throws DSSException {
    logger.debug("");
    try {
      return getTemporaryFileAsStream();
    } catch (FileNotFoundException e) {
      logger.error(e.getMessage());
      throw new DSSException(e);
    }
  }

  FileInputStream getTemporaryFileAsStream() throws FileNotFoundException {
    logger.debug("");
    return new FileInputStream(temporaryFile);
  }

  @Override
  public byte[] getBytes() throws DSSException {
    logger.debug("");
    try {
      return IOUtils.toByteArray(getTemporaryFileAsStream());
    } catch (IOException e) {
      logger.error(e.getMessage());
      throw new DSSException(e);
    }
  }

  @Override
  public String getName() {
    logger.debug("");
    return documentName;
  }

  @Override
  public String getAbsolutePath() {
    logger.debug("");
    String absolutePath = temporaryFile.getAbsolutePath();
    return absolutePath;
  }

  @Override
  public MimeType getMimeType() {
    logger.debug("Mime type: " + mimeType);
    return mimeType;
  }

  @Override
  public void setMimeType(MimeType mimeType) {
    logger.debug("Mime type: " + mimeType);
    this.mimeType = mimeType;
  }

  @Override
  public void save(String filePath) {
    logger.debug("File Path: " + filePath);
    try {
      FileOutputStream fileOutputStream = new FileOutputStream(filePath);
      try {
        IOUtils.copy(getTemporaryFileAsStream(), fileOutputStream);
      } finally {
        fileOutputStream.close();
      }
    } catch (IOException e) {
      logger.error(e.getMessage());
      throw new DSSException(e);
    }
  }

  @Override
  public String getDigest(DigestAlgorithm digestAlgorithm) {
    logger.debug("Digest algorithm: " + digestAlgorithm);
    byte[] digestBytes;
    try {
      digestBytes = DSSUtils.digest(digestAlgorithm, getTemporaryFileAsStream());
    } catch (FileNotFoundException e) {
      logger.error(e.getMessage());
      throw new DSSException(e);
    }
    return DSSUtils.base64Encode(digestBytes);
  }
}