package org.rememberme.domain

class Tag {

  Double confidence
  String name
  String value

//  static belongsTo = [face: Face]

  static constraints = {
    confidence nullable: true
    name nullable: true
    value nullable: true
  }
}
