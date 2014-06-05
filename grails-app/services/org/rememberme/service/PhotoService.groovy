package org.rememberme.service

import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import grails.transaction.Transactional
import groovyx.net.http.HTTPBuilder

import org.apache.commons.codec.binary.Base64
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

  def processPhoto(Long userId, Long photoId) {

    Photo curPhotoDetails = Photo.get(photoId)
    if (curPhotoDetails && curPhotoDetails.path.endsWith("$userId")) {

      String fileName = curPhotoDetails.pathToFile

//      String encodedFile = encodeFileToBase64Binary(fileName)
      byte[] encodedFileBytes = encodeFileToBase64BinaryBytes(curPhotoDetails.pathToFile)
      println "encodedFileBytes " + encodedFileBytes

      def http = new HTTPBuilder('http://betafaceapi.com/service_json.svc/UploadNewImage_File' )
      def http2 = new HTTPBuilder('http://betafaceapi.com/service_json.svc/GetImageInfo' )

      if (!curPhotoDetails.helpUid) {
        http.request( POST, JSON  ) {
          body = ["api_key": 'd45fd466-51e2-4701-8da8-04351c872236',
            "api_secret": '171e8465-f548-401d-b63b-caf0dc28df5f',
            "imagefile_data": encodedFileBytes,
            "detection_flags": "cropface",
            "original_filename": curPhotoDetails.fileName]

          response.success = { resp, json ->
            println "resp " + json
            if (json.int_response == 0) {
              println "uid set"
              curPhotoDetails.helpUid = json.img_uid
            }
            println "POST response status: ${resp.statusLine}"
          }
        }
        println "call ended"
        curPhotoDetails.save(flush: true)
      }

      if (curPhotoDetails.helpUid) {
        http2.request( POST, JSON  ) {
          body = ["api_key": 'd45fd466-51e2-4701-8da8-04351c872236',
            "api_secret": '171e8465-f548-401d-b63b-caf0dc28df5f',
            "img_uid": "0217ea81-4ca6-4e15-9ff9-33f5a5465a6d "]

          response.success = { resp, json ->
            println "resp " + json

            println "POST response status: ${resp.statusLine}"
          }
        }
      }
    }

  }

//  private String encodeFileToBase64Binary(String fileName)
//  throws IOException {
//
//    File file = new File(fileName);
//    byte[] bytes = loadFile(file);
//    byte[] encoded = Base64.encodeBase64(bytes);
//    String encodedString = new String(encoded);
//
//    return encodedString;
//  }

  private byte[] encodeFileToBase64BinaryBytes(String pathToFile)
  throws IOException {

    //File file = new File(fileName)
    byte[] bytes = new File(pathToFile).bytes
   // byte[] encoded = Base64.encodeBase64(bytes);

    return Base64.encodeBase64(bytes)
  }

  private static byte[] loadFile(File file) throws IOException {
    InputStream is = new FileInputStream(file);

    long length = file.length();
    if (length > Integer.MAX_VALUE) {
      // File is too large
    }
    byte[] bytes = new byte[(int)length];

    int offset = 0;
    int numRead = 0;
    while (offset < bytes.length
    && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
      offset += numRead;
    }

    if (offset < bytes.length) {
      throw new IOException("Could not completely read file "+file.getName());
    }

    is.close();
    return bytes;
  }
}
