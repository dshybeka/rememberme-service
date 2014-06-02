package org.rememberme.util

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

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

}
