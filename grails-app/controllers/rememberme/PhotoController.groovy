package rememberme

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

import org.rememberme.domain.Photo
import org.rememberme.security.SecUser
import org.rememberme.service.PhotoService
import org.springframework.web.multipart.commons.CommonsMultipartFile

@Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
class PhotoController {

  def grailsApplication

  PhotoService photoService

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

      result.urls = []
      user.photos.each { Photo photo ->
        result.urls << [id: photo.id, name: photo.fileName, userDescription: photo.userDescription, information: photo.processedInformation ]
      }

      if (result.urls.empty) {
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
    def userId = params.userId

    if (file) {

      try {

        String pathToImage = getPhotoPath(file, userId)
        String fileName = file.getFileItem().name

        photoService.savePhotoForUser([path: pathToImage, fileName: fileName], userId)

        new File(pathToImage).mkdirs()

        def fileToSave = new File(pathToImage + "\\${fileName}").newOutputStream()
        fileToSave << file.bytes
        fileToSave.close()

        result.success = true
      } catch(all){

        log.error "Error while uploading file for user ${userId}: ${all}"

        result.success = false
        response.status = 500
      }
    }

    render result as JSON
  }

  private String getPhotoPath(def file, def userId) {
    "${grailsApplication.config.photos.default.path}/${userId}"
  }
}
