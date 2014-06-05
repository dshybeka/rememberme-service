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
println "send photo to front!"
    Long userId = params.long("userId")
    Long photoId = params.long("photoId")

    Photo photoDetails = Photo.get(photoId)
    if (photoDetails && photoDetails.path.endsWith("$userId")) {

      File photo = new File(photoDetails.pathToFile)

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
    if (photoDetails && photoDetails.path.endsWith("$userId")) {

      File photo = new File(photoDetails.pathToThumbFile)

      response.contentType = 'image/png'
      response.outputStream << photo.bytes
      response.outputStream.flush()
    } else {
      response.status = 404
    }
  }

  def getPhotoDetails() {

    def result = [:]

    Long userId = params.long("userId")
    Long photoId = params.long("photoId")

    Photo curPhotoDetails = Photo.get(photoId)
    if (curPhotoDetails && curPhotoDetails.path.endsWith("$userId")) {

      result.data = [ id: curPhotoDetails.id,
        name: curPhotoDetails.fileName,
        userDescription: curPhotoDetails.userDescriptionDefault,
        processedInformation: curPhotoDetails.processedInformationDefault,
        url: "http://localhost:8090/RememberMe/user/${userId}/photo/${curPhotoDetails.id}",
        urlThumb: "http://localhost:8090/RememberMe/user/${userId}/photo/${curPhotoDetails.id}/thumb",
        urlProcess: "http://localhost:8090/RememberMe/user/${userId}/photo/${curPhotoDetails.id}/process",
        createDate: curPhotoDetails.createDate,
        userId: userId ]
      result.success = true
    } else {
      result.success = false
      response.status = 404
    }

    render result as JSON
  }

  def getUserPhotos() {

    Long userId = params.long("userId")

    def result = [:]

    SecUser user = SecUser.get(userId)
    if (user) {

      result.data = []
      user.photos.each { Photo curPhotoDetails ->
        result.data << [ id: curPhotoDetails.id,
          name: curPhotoDetails.fileName,
          userDescription: curPhotoDetails.userDescriptionDefault,
          processedInformation: curPhotoDetails.processedInformationDefault,
          url: "http://localhost:8090/RememberMe/user/${userId}/photo/${curPhotoDetails.id}",
          urlThumb: "http://localhost:8090/RememberMe/user/${userId}/photo/${curPhotoDetails.id}/thumb",
          urlProcess: "http://localhost:8090/RememberMe/user/${userId}/photo/${curPhotoDetails.id}/process",
          createDate: curPhotoDetails.createDate,
          userId: userId ]
      }

      if (result.data.empty) {
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

  def savePhotoDetails() {

    def result = [:]

    Long userId = params.long("userId")
    Long photoId = params.long("photoId")

    if (userId && photoId) {
      println "udpate photo"
      photoService.updatePhotoWithParams(params + request.JSON)
      result.success = true
    } else {
      result.success = false
      response.status = 404
    }

    render result as JSON
  }

  def processPhoto() {

    println "properties " + params

    def result = [:]

    Long userId = params.long("userId")
    Long photoId = params.long("photoId")
    String helpUid = params.helpUid
println "helpUid " + helpUid
    if (userId && photoId && helpUid) {

      photoService.processPhoto(userId, photoId, helpUid)

      result.success = true
    } else {
      result.success = false
      response.status = 404
    }

    render result as JSON
  }
}
