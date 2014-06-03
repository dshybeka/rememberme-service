package org.rememberme.service

import grails.transaction.Transactional

import java.awt.image.BufferedImage

import javax.imageio.ImageIO

import org.apache.commons.io.FilenameUtils
import org.imgscalr.Scalr
import org.rememberme.util.PhotoUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.multipart.commons.CommonsMultipartFile

@Transactional
class PhotoStoreService {

  @Autowired
  PhotoUtil photoUtil

  def storeFileForUser(CommonsMultipartFile file, Long userId) {

    Boolean isStoreSuccess = false

    if (file) {

      String pathToImage = photoUtil.getPhotoPath(userId)
      String pathToThumbImage = photoUtil.getThumbPhotoPath(userId)
//      String pathToProcessedImage = photoUtil.getPhotoPath(userId)
      String fileName = file.getFileItem().name

      new File(pathToImage).mkdirs()
      new File(pathToThumbImage).mkdirs()
//      new File(pathToProcessedImage).mkdirs()

      saveImage(pathToImage + "\\${fileName}", file)
      saveThumbnailImage(pathToThumbImage + "\\${fileName}", file)
//      saveThumbnailImage(pathToImage + "\\${fileName}", file)

      isStoreSuccess = true
    }

    isStoreSuccess
  }

  private void saveImage(String pathToImageWithName, def file) {

    def fileToSave = new File(pathToImageWithName).newOutputStream()
    fileToSave << file.bytes
    fileToSave.close()
  }

  private void saveThumbnailImage(String pathToImageWithName, CommonsMultipartFile file) {

    BufferedImage image = ImageIO.read(file.inputStream)
    image = Scalr.resize(image, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH, 200, 200, Scalr.OP_ANTIALIAS)
    String extension = FilenameUtils.getExtension(file.getFileItem().name)
    File outputfile = new File(pathToImageWithName);
    ImageIO.write(image, extension, outputfile);
  }
}
