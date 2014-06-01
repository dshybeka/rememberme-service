package org.rememberme.domain

import org.rememberme.security.SecUser

class Photo {

  String fileName
  String path

  String userDescription
  String processedInformation

  static belongsTo = [secUser: SecUser]

  static constraints = {
    secUser nullable: true
    userDescription nullable: true
    processedInformation nullable: true
  }

  public String getPathToFile() {
    path + "//" + fileName
  }
}
