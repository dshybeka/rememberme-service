package org.rememberme.domain

class Point {

  String name
  String type
  Double x
  Double y

//  static belongsTo = [face: Face]

  static constraints = {
    name nullable: true
    type nullable: true
    x nullable: true
    y nullable: true
  }
}
