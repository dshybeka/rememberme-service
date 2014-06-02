package rememberme

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

import org.rememberme.domain.Photo
import org.rememberme.security.SecUser
import org.rememberme.service.PhotoService
import org.rememberme.service.PhotoStoreService
import org.springframework.web.multipart.commons.CommonsMultipartFile

@Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
class PhotoController {

  def grailsApplication

  PhotoService photoService
  PhotoStoreService photoStoreService

  static allowedMethods = [save: "POST", check: "GET"]

  @Secured(['ROLE_USER'])
  def check() {

    log.info "Server is really up"

    render "Server up"
  }

  def getPhoto() {

    Long userId = params.long("userId")
    Long photoId = params.long("photoId")

    Photo photoDetails = Photo.get(photoId)
    if (photoDetails && userId == photoDetails.secUser.id) { // should be refactored to user photo path as we use userId there

      File photo = new File(photoDetails.pathToFile)
      println "photoDetails.pathToFile " + photoDetails.pathToFile

      response.contentType = 'image/png'
      response.outputStream << photo.bytes
      response.outputStream.flush()
    } else {
      response.status = 404
    }
  }

  def getThumbnailPhoto() {

    Long userId = params.long("userId")
    Long photoId = params.long("photoId")

    Photo photoDetails = Photo.get(photoId)
    if (photoDetails && userId == photoDetails.secUser.id) { // should be refactored to user photo path as we use userId there

      File photo = new File(photoDetails.pathToThumbFile)

      response.contentType = 'image/png'
      response.outputStream << photo.bytes
      response.outputStream.flush()
    } else {
      response.status = 404
    }
  }


  def getUserPhotos() {

    Long userId = params.long("userId")

    def result = [:]

    SecUser user = SecUser.get(userId)
    if (user) {

      result.photoDetails = []
      user.photos.each { Photo photo ->
        result.photoDetails << [id: photo.id, name: photo.fileName, userDescription: photo.userDescription, information: photo.processedInformation, url: "http://localhost:8090/RememberMe/user/${userId}/photo/${photo.id}/thumb" ]
      }

      if (result.photoDetails.empty) {
        response.status = 404
      } else {
        result.success = true
      }

    } else {
      response.status = 404
      result.success = false
    }

    render result as JSON
  }

  def save() {

    def result = [:]

    CommonsMultipartFile file = params.file
    Long userId = params.long('userId')

    if (file) {

      try {

        Boolean isSavedToDb = photoService.savePhotoForUser(file, userId)
        Boolean isSavedToFilesystem = photoStoreService.storeFileForUser(file, userId)

        result.success = isSavedToDb && isSavedToFilesystem
      } catch(all){

        log.error "Error while uploading file for user ${userId}: ${all}"

        result.success = false
      } finally {

        if (!result.success) {
          result.message = "Error while saving image. Please try again later."
          response.status = 500
        }
      }
    }

    render result as JSON
  }

}
