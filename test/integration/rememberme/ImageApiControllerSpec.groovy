package rememberme

import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import spock.lang.*
import Luxand.*

/**
 *
 */
class ImageApiControllerSpec extends Specification {

  ImageApiController controller

  def setup() {
    controller = new ImageApiController()
  }

  def cleanup() {
  }

  void "test something"() {
    given:
    String imagePath3 = "E:/study/self-prog/yeoman/rememberMe-web/app/images/front-smile4.jpg"
    String msg = """<?xml version="1.0" encoding="utf-8"?>
                       <FaceRequestId>
                        <api_key>d45fd466-51e2-4701-8da8-04351c872236</api_key>
                        <api_secret>171e8465-f548-401d-b63b-caf0dc28df5f</api_secret>
                        <imagefile_data>${encodeFileToBase64Binary(imagePath3)}</imagefile_data>
                        <original_filename>sample1.jpg</original_filename>
                      </FaceRequestId>"""

    String jsonMessage = """<?xml version="1.0" encoding="utf-8"?>
                              <ImageRequestBinary xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                                                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                              <api_key>d45fd466-51e2-4701-8da8-04351c872236</api_key>
                              <api_secret>171e8465-f548-401d-b63b-caf0dc28df5f</api_secret>
                              <detection_flags></detection_flags>
                              <imagefile_data>${encodeFileToBase64Binary(imagePath3)}</imagefile_data>
                              <original_filename>sample1.jpg</original_filename>
                              </ImageRequestBinary>"""

    when:
    def http = new HTTPBuilder("http://www.betafaceapi.com/service.svc/UploadNewImage_File")

    http.request(POST) { req ->

      headers.'Content-Type' = 'application/xml'
      headers.'Cache-Control' = 'no-cache(optional)'
      headers.'Pragma' = 'no-cache(optional)'

      requestContentType = ContentType.XML
      body = jsonMessage

      response.success = { resp, json ->
        println "json " + json
      }
    }

    then:
    assert true
  }



  //
  //  void "test make call"() {
  //    given: "start"
  //    String path = """d:/ggts-workspace/RememberMe/lib/facesdk.dll"""
  //    String imagePath = "d:/ggts-workspace/RememberMe/test/integration/resources/test.jpg"
  //    String imagePath2 = "d:/ggts-workspace/RememberMe/test/integration/resources/test4.jpg"
  //    String imagePath3 = "E:/study/self-prog/yeoman/rememberMe-web/app/images/front-smile4.jpg"
  //    Runtime.getRuntime().load0(groovy.lang.GroovyClassLoader.class, path)
  //
  //    FSDK.ActivateLibrary("""BgpYEjfSrlYMWC5czVHR1FOxFVZ1JHACDP4H5VtzE+q/GiBCMTSF6OZ4ao2cyh4K03I5hwGBxp0Pjv7fuXj8Tmz5EKGqsbPWDKzF4psydCnNRphv+juc4e5PN/xx1WljcUKAUt5HIXLB0puGdpU1jliptXTeB3V1iegUQgpd4H0=""")
  //
  //
  //    when: "Make call"
  //    FSDK.Initialize()
  //    FSDK.SetFaceDetectionParameters(true, true, 1000)
  //    controller.detectFaces(imagePath3)
  //
  //    then: "true"
  //    assert true
  //  }
  //
  //  void "test Luxand"() {
  //    given: "start"
  //    String path = """d:/ggts-workspace/RememberMe/lib/facesdk.dll"""
  //    String imagePath = "d:/ggts-workspace/RememberMe/test/integration/resources/test.jpg"
  //    String imagePath2 = "d:/ggts-workspace/RememberMe/test/integration/resources/test2.jpg"
  //    String imagePath3 = "E:/study/self-prog/yeoman/rememberMe-web/app/images/front-smile4.jpg"
  //    Runtime.getRuntime().load0(groovy.lang.GroovyClassLoader.class, path)
  //
  //    FSDK.ActivateLibrary("""BgpYEjfSrlYMWC5czVHR1FOxFVZ1JHACDP4H5VtzE+q/GiBCMTSF6OZ4ao2cyh4K03I5hwGBxp0Pjv7fuXj8Tmz5EKGqsbPWDKzF4psydCnNRphv+juc4e5PN/xx1WljcUKAUt5HIXLB0puGdpU1jliptXTeB3V1iegUQgpd4H0=""")
  //
  //    when: "do smth"
  //    FSDK.Initialize()
  //    FSDK.SetFaceDetectionParameters(true, true, 1000)
  //
  //    FSDK_FaceTemplate.ByReference result1 = controller.getFaceTemplate(imagePath)
  //    FSDK_FaceTemplate.ByReference result2 = controller.getFaceTemplate(imagePath2)
  //
  //    HImage bigImage = new HImage()
  //    FSDK.CreateEmptyImage(bigImage)
  //
  //    int[] width = new int[10]
  //    HImage image = new HImage()
  //    FSDK.LoadImageFromFile(image, path)
  //
  //    FSDK.ResizeImage(image, 2, bigImage)
  //
  //    then: "following happened after matching "
  //    assert true
  //
  //    def sim = new float[1]
  //    println "width " + FSDK.MatchFaces(result1, result2, sim)
  //    println "show me seem please " + sim
  //    controller.isSamePerson(sim)
  ////    println "is saved success? " + FSDK.SaveImageToFile(bigImage, "d:/bigTest.jpg")
  //  }
}
