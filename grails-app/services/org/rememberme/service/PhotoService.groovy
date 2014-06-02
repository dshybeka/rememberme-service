package org.rememberme.service

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

  def savePhotoForUser(CommonsMultipartFile file, Long userId) {

    Boolean isSavedSuccess = false

    Long userIdForPhoto = userId as Long

    SecUser user = SecUser.get(userIdForPhoto)

    if (user) {

      Photo photo = new Photo()
      photo.fileName = file.getFileItem().name
      photo.path = photoUtil.getPhotoPath(userId)
      photo.thumbPath = photoUtil.getThumbPhotoPath(userId)
      photo.processedPath = photoUtil.getProcessedPhotoPath(userId)

      user.addToPhotos(photo)

      if (user.save(flush: true)) {
        isSavedSuccess = true
      } else {
        log.error "Error while saving photos for user with id ${userIdForPhoto}: ${user.errors} "
      }
    }

    isSavedSuccess
  }
}
