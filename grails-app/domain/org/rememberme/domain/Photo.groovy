package org.rememberme.domain

import org.joda.time.LocalDate
import org.rememberme.security.SecUser

class Photo {

  private static String NO_INFO_MESSAGE = "No information exists for this photo."

  String helpUid

  String fileName
  String path
  String thumbPath
  String processedPath
  String userDescription
  String processedInformation

  Integer width
  Integer height

  Boolean isProcessed

  LocalDate createDate

  static belongsTo = [secUser: SecUser]

  static constraints = {
    secUser nullable: true
    userDescription nullable: true
    processedInformation nullable: true
    processedPath nullable: true
    thumbPath nullable: true
    createDate nullable: true
    isProcessed nullable: true
    helpUid nullable: true
  }

  public String getPathToFile() {
    path + "//" + fileName
  }

  public String getPathToThumbFile() {
    thumbPath + "//" + fileName
  }

  public String getUserDescriptionDefault() {
    userDescription ?: NO_INFO_MESSAGE
  }

  public String getProcessedInformationDefault() {
    processedInformation ?: NO_INFO_MESSAGE
  }

  def beforeInsert = {
    createDate = new LocalDate()
  }
}
