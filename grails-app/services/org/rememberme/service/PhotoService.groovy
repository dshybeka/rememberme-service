package org.rememberme.service

import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import grails.transaction.Transactional

import org.rememberme.domain.Face
import org.rememberme.domain.Photo
import org.rememberme.security.SecUser
import org.rememberme.util.PhotoUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.multipart.commons.CommonsMultipartFile

import com.google.common.collect.Lists

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

    Photo curPhotoDetails = Photo.get(photoId)
    String helpUid = info.uid

    Boolean isPhotoBelongsToUser = curPhotoDetails && curPhotoDetails.path.endsWith("$userId")
    Boolean isShouldBeProcessed = helpUid && !curPhotoDetails.isProcessed
    if (isPhotoBelongsToUser && isShouldBeProcessed) {

      log.info "Will be processed"

      curPhotoDetails.isProcessed = true
      curPhotoDetails.helpUid = helpUid

      List<Face> faces = retrieveFacesFromInfo(info)
      faces.each { Face face ->
        curPhotoDetails.addToFaces(face)
      }

      curPhotoDetails.save(flush: true)
    } else {
      log.info "Will not be processed: isPhotoBelongsToUser - $isPhotoBelongsToUser, isShouldBeProcessed - $isShouldBeProcessed "
    }

    curPhotoDetails

  }

  private List<Face> retrieveFacesFromInfo(def info) {

    List<Face> faces = Lists.newArrayList()
    info.faces.each { def faceInfo ->

      Face processFace = new Face()
      processFace.angle = faceInfo.angle
      processFace.height = faceInfo.height
      processFace.score = faceInfo.score
      processFace.width = faceInfo.width
      processFace.x = faceInfo.x
      processFace.y = faceInfo.y
      processFace.faceHelpUid = faceInfo.uid

      faces.add(processFace)
    }

    faces
  }

}

