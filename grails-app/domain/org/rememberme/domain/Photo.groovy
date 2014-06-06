package org.rememberme.domain

import org.joda.time.LocalDate
import org.rememberme.security.SecUser

class Photo {

  String helpUid

  String fileName
  String path
  String thumbPath
  String userDescription
  String processedInformation

  Integer width
  Integer height

  Boolean isProcessed

  LocalDate createDate

  List<Face> faces
  static hasMany = [faces: Face]

  static belongsTo = [secUser: SecUser]

  static constraints = {
    secUser nullable: true
    faces nulalble: true
    userDescription nullable: true
    processedInformation nullable: true
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
    userDescription
  }

  public String getProcessedInformationDefault() {
    processedInformation
  }

  def beforeInsert = {
    createDate = new LocalDate()
  }
}
