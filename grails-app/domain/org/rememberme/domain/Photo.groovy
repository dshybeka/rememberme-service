package org.rememberme.domain

import org.rememberme.security.SecUser

class Photo {

  private static String NO_INFO_MESSAGE = "No information exists for this photo."

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

  public String getUserDescription() {
    userDescription ?: NO_INFO_MESSAGE
  }

  public String getProcessedInformation() {
    processedInformation ?: NO_INFO_MESSAGE
  }
}
