package org.rememberme

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

import org.rememberme.domain.Face

@Secured(['IS_AUTHENTICATED_ANONYMOUSLY'])
class FaceController {

  def updateFace() {

    log.info "Input parameters and json request: ${params + request.JSON}"

    def result = [:]

    Long userId = params.long('userId');
    String newPersonName = request.JSON.personName
    String faceId = params.faceId

    Boolean requiredParametersPresented = userId && newPersonName && faceId
    if (requiredParametersPresented) {

      Face.withTransaction {

        Face face = Face.get(faceId)

        Boolean isUpdatePermitted = userId == face.photo.secUser.id
        if (isUpdatePermitted) {

          face.personName = newPersonName
          if (face.save(flush: true)) {
            result.success = true
          } else {
            log.error "Could not save face because of errors: $face.errors"
            result.success = false
          }
        } else {
          log.warn "Update is not permitted for this user"
          result.success = false
        }
      }
    } else {
      log.warn "Could not update face info: not all required parameters presented."
      result.success = false
    }

    render result as JSON
  }

  // TODO: make it more secure!
  def getTargetFaces() {

    def result = [:]

    Long userId = params.long('userId')
    String helpUid = params.helpUid

    def results = Face.createCriteria().list {
      isNotNull 'personName'
      photo {
        secUser {
          eq 'id', userId
        }
      }

    }

    log.info "Founded face targets: $results"

    String targetsUid = ""
    results.each { Face face ->
      if (face.faceHelpUid != helpUid) {
        if (targetsUid.isEmpty()) {
          targetsUid += face.faceHelpUid
        } else {
          targetsUid += "," + face.faceHelpUid
        }
      }
    }

    if (!results.empty) {
      result.data = targetsUid
      log.info "Delete " + result.data
      result.success = true
    } else {
      result.success = false
    }

    render result as JSON
  }

  def getFaceByUid() {

    def result = [:]

    Long userId = params.long('userId')
    String helpUid = params.helpUid

    Face face = Face.findByFaceHelpUid(helpUid)

    log.info "Finded face for uid: $helpUid - $face"

    if (face) {
      result.data = [id: face.id, personName: face.personName, uid: face.faceHelpUid] // TODO: add to formatter
      result.success = true
    } else {
      result.success = false
    }

    render result as JSON
  }
}
