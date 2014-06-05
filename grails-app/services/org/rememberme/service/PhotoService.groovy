package org.rememberme.service

import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import grails.transaction.Transactional

import org.rememberme.domain.Photo
import org.rememberme.security.SecUser
import org.rememberme.util.PhotoUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.multipart.commons.CommonsMultipartFile

@Transactional
class PhotoService {

  @Autowired
  PhotoUtil photoUtil

  // TODO: resolve issue with synchronization
  def savePhotoForUser(CommonsMultipartFile file, Long userId) {

    Boolean isSavedSuccess = false

    Long userIdForPhoto = userId as Long

    SecUser user = SecUser.get(userIdForPhoto)

    if (user) {

      def dimensions = photoUtil.getImageDimensions(file)

      Photo photo = new Photo()
      photo.fileName = file.getFileItem().name
      photo.path = photoUtil.getPhotoPath(userId)
      photo.thumbPath = photoUtil.getThumbPhotoPath(userId)
      photo.processedPath = photoUtil.getProcessedPhotoPath(userId)
      photo.width = dimensions.width
      photo.height = dimensions.height

      user.addToPhotos(photo)

      if (user.save(flush: true)) {
        isSavedSuccess = true
      } else {
        log.error "Error while saving photos for user with id ${userIdForPhoto}: ${user.errors} "
      }
    }

    isSavedSuccess
  }

  def updatePhotoWithParams(def params) {

    Long userId = params.long("userId")
    Long photoId = params.long("photoId")

    Photo curPhotoDetails = Photo.get(photoId)
    if (curPhotoDetails && curPhotoDetails.path.endsWith("$userId")) {
      if (params.userDescription) {
        curPhotoDetails.userDescription = params.userDescription
      }
      if (params.processedInformation) {
        curPhotoDetails.processedInformation = params.processedInformation
      }

      curPhotoDetails.save(flush: true)
    }
  }

  def saveProcessedPhoto(Long userId, Long photoId, def info) {

    println "info " + info
println "_____________________"
    println "info face " + info.faces
    println "_____________________"
    //println "info face[0]" + info.faces[0]
//    Photo curPhotoDetails = Photo.get(photoId)
//    if (curPhotoDetails && curPhotoDetails.path.endsWith("$userId")) {
//
//    }

  }

}

