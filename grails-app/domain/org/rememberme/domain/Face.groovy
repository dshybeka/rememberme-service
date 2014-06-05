package org.rememberme.domain

class Face {

  String faceHelpUid
  String personName
  String path

  Double height
  Double width
  Double x
  Double y
  Double angle
  Double score

  static belongsTo = [photo: Photo]

//  static hasMany = [tags: Tag, points: Point]

  static constraints = {
    path nullable: true
    personName nullable: true
    height nullable: true
    width nullable: true
    x nullable: true
    y nullable: true
    angle nullable: true
    score nullable: true
  }
}
