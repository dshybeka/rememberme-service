package org.rememberme.domain

import org.rememberme.security.SecUser

class Photo {

  String fileName
  String path
  String thumbPath
  String processedPath

  String userDescription
  String processedInformation

  static belongsTo = [secUser: SecUser]

  static constraints = {
    secUser nullable: true
    userDescription nullable: true
    processedInformation nullable: true
    processedPath nullable: true
    thumbPath nullable: true
  }

  public String getPathToFile() {
    path + "//" + fileName
  }

  public String getPathToThumbFile() {
    thumbPath + "//" + fileName
  }
}
