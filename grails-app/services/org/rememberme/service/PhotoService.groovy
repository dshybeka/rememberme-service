package org.rememberme.service

import grails.transaction.Transactional

import org.rememberme.domain.Photo
import org.rememberme.security.SecUser

@Transactional
class PhotoService {

  def savePhotoForUser(def photoDetails, def userId) {

    def result = [:]

    Long userIdForPhoto = userId as Long

    SecUser user = SecUser.get(userIdForPhoto)

    if (user) {

      Photo photo = new Photo()
      photo.fileName = photoDetails.fileName
      photo.path = photoDetails.path

      user.addToPhotos(photo)

      if (user.save(flush: true)) {
        result.success = true
      } else {
        result.success = false
        log.error "Error while saving photos for user with id ${userIdForPhoto}: ${user.errors} "
      }
    }

    result
  }
}
