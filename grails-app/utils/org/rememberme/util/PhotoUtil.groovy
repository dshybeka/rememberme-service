package org.rememberme.util

import java.awt.image.BufferedImage

import javax.imageio.ImageIO

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.multipart.commons.CommonsMultipartFile

@Component
class PhotoUtil {

  @Autowired
  def grailsApplication

  public String getPhotoPath(def userId) {
    "${grailsApplication.config.photos.default.path}/${userId}"
  }

  public String getThumbPhotoPath(def userId) {
    "${grailsApplication.config.photos.default.path}/${userId}/thumb"
  }

  public String getProcessedPhotoPath(def userId) {
    "${grailsApplication.config.photos.default.path}/${userId}/process"
  }

  public Map getImageDimensions(CommonsMultipartFile file) {

    BufferedImage image = ImageIO.read(file.inputStream)
    [width: image.width, height: image.height]
  }

}
