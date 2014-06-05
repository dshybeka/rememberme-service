package org.rememberme.util

import org.rememberme.domain.Face
import org.rememberme.domain.Photo

class FrontModelFormatter {

  public static Map formatPhoto(Photo photo, Long userId) {

    [ id: photo.id,
      name: photo.fileName,
      userDescription: photo.userDescriptionDefault,
      processedInformation: photo.processedInformationDefault,
      url: "http://localhost:8090/RememberMe/user/${userId}/photo/${photo.id}",
      urlThumb: "http://localhost:8090/RememberMe/user/${userId}/photo/${photo.id}/thumb",
      urlProcess: "http://localhost:8090/RememberMe/user/${userId}/photo/${photo.id}/processed",
      createDate: photo.createDate,
      faces: formatFaces(photo.faces),
      isProcessed: photo.isProcessed,
      userId: userId ]
  }

  public static Map formatFaces(List<Face> faces) {

    def result = [:]

    faces?.each { Face face ->
      result << formatFace(face)
    }

    result
  }

  public static Map formatFace(Face face) {
    [ id: face.id,
      helpUid: face.faceHelpUid,
      personName: face.personName,
      path: face.path ]

  }
}
