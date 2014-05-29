package rememberme

import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovyx.net.http.*

import java.awt.Color
import java.awt.Graphics
import java.awt.Image
import java.awt.image.BufferedImage

import javax.imageio.ImageIO

import org.apache.commons.codec.binary.Base64

import Luxand.FSDK
import Luxand.FSDK.FSDK_FaceTemplate
import Luxand.FSDK.FSDK_Features
import Luxand.FSDK.HImage

class ImageApiController {

  private final int width = 640;
  private final int height = 480;

  def list() {

    render "trololo"
  }
// dd64e2c4-438e-4cb4-a8b0-2ba0c341f49b
  def doPostCall() {

    String fileName = "E:/study/self-prog/yeoman/rememberMe-web/app/images/front-smile4.jpg"

    String encodedFile = encodeFileToBase64Binary(fileName)
    byte[] encodedFileBytes = encodeFileToBase64BinaryBytes(fileName)

//    String xmlMessage = """<?xml version="1.0" encoding="utf-8"?>
//                              <ImageRequestBinary xmlns:xsd="http://www.w3.org/2001/XMLSchema"
//                                                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
//                              <api_key>d45fd466-51e2-4701-8da8-04351c872236</api_key>
//                              <api_secret>171e8465-f548-401d-b63b-caf0dc28df5f</api_secret>
//                              <imagefile_data>${encodeFileToBase64Binary(fileName)}</imagefile_data>
//                              <original_filename>sample1.jpg</original_filename>
//                              </ImageRequestBinary>"""


    def http = new HTTPBuilder( 'http://betafaceapi.com/service_json.svc/UploadNewImage_File' )
String showThis = "Torororolr"
    http.request( POST, JSON  ) {
//      headers.'Content-Type' = 'application/xml'
//      headers.'Cache-Control' = 'no-cache(optional)'
//      headers.'Pragma' = 'no-cache(optional)'

      //uri.path = '/'
      //requestContentType = XML
      body = ["api_key": 'd45fd466-51e2-4701-8da8-04351c872236',
        "api_secret": '171e8465-f548-401d-b63b-caf0dc28df5f',
        "imagefile_data": encodedFileBytes,
        "original_filename": 'samr533.jpg']

      response.success = { resp, json ->
        showThis = json
        println "resp " + json
        println "POST response status: ${resp.statusLine}"
        //assert resp.statusLine.statusCode == 201
      }
//
//      // called only for a 404 (not found) status code:
//      response.'404' = { resp -> println 'Not found' }
    }
    render showThis
  }

  public void makeCall(String filePath) {

    String fileName = filePath
    Image a = null

    HImage imageHandle = new HImage()

    def imageWidthByReference = new int[1];
    def imageHeightByReference = new int[1];

    if (FSDK.LoadImageFromFileW(imageHandle, fileName) == FSDK.FSDKE_OK) {


      FSDK.GetImageWidth(imageHandle, imageWidthByReference)
      FSDK.GetImageHeight(imageHandle, imageHeightByReference)
      Integer imageWidth = imageWidthByReference[0]
      Integer imageHeight = imageHeightByReference[0]
      double ratio = java.lang.Math.min((width + 0.4) / imageWidth,
          (height + 0.4) / imageHeight)
      HImage image2Handle = new HImage()
      FSDK.CreateEmptyImage(image2Handle)
      FSDK.ResizeImage(imageHandle, ratio, image2Handle)
      FSDK.CopyImage(image2Handle, imageHandle)
      FSDK.FreeImage(image2Handle)


      // save image Integero awt.Image
      def awtImage = new Image[1];
      if (FSDK.SaveImageToAWTImage(imageHandle, awtImage, FSDK.FSDK_IMAGEMODE.FSDK_IMAGE_COLOR_24BIT) != FSDK.FSDKE_OK){
        println "Error displaying picture!"
      } else {
        def img = awtImage[0]
        BufferedImage bimg = null

        //        FSDK_Features features = new FSDK_Features();
        //        def attrValues = new String[1]
        //        float [] ConfidenceMale = new float[1];
        //        float [] ConfidenceFemale = new float[1];
        //        FSDK.DetectFacialAttributeUsingFeatures(imageHandle, features, attrValues, 1024)
        //        FSDK.GetValueConfidence(attrValues[0], "Male", ConfidenceMale);
        //        FSDK.GetValueConfidence(attrValues[0], "Female", ConfidenceFemale);
        //
        //        println "values " + ConfidenceMale
        //        println "values " + ConfidenceFemale

        FSDK.TFacePosition.ByReference facePosition = new FSDK.TFacePosition.ByReference()
        if (FSDK.DetectFace(imageHandle, facePosition) != FSDK.FSDKE_OK){
          println "No faces found!"
        } else {
          bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB)
          Graphics gr = bimg.getGraphics()
          gr.drawImage(img, 0, 0, null)
          gr.setColor(Color.green)

          int left = facePosition.xc - facePosition.w / 2
          int top = facePosition.yc - facePosition.w / 2
          gr.drawRect(left, top, facePosition.w, facePosition.w)

          FSDK_Features.ByReference facialFeatures = new FSDK_Features.ByReference()
          FSDK.DetectFacialFeaturesInRegion(imageHandle, (FSDK.TFacePosition)facePosition, facialFeatures)

          FSDK_Features features = new FSDK_Features();
          def attrValues = new String[1]
          float [] ConfidenceMale = new float[1];
          float [] ConfidenceFemale = new float[1];
          FSDK.DetectFacialAttributeUsingFeatures(imageHandle, features, attrValues, 1024)
          FSDK.GetValueConfidence(attrValues[0], "Male", ConfidenceMale);
          FSDK.GetValueConfidence(attrValues[0], "Female", ConfidenceFemale);

          println "values " + ConfidenceMale
          println "values " + ConfidenceFemale

          for (int i = 0; i < FSDK.FSDK_FACIAL_FEATURE_COUNT; ++i){
            if (i<2)
              gr.setColor(Color.blue)
            else if (i==2)
              gr.setColor(Color.green)

            gr.drawOval(facialFeatures.features[i].x, facialFeatures.features[i].y, 3, 3)
          }

          gr.dispose()
        }


        //        img = awtImage[0]

        File outputfile = new File("d:/bimage.jpg");
        ImageIO.write(bimg, "jpg", outputfile);

      }

      FSDK.FreeImage(imageHandle)
    }
  }

  public FSDK_FaceTemplate.ByReference getFaceTemplate(String fileName) {

    FSDK_FaceTemplate.ByReference facialFeatures

    HImage imageHandle = new HImage()

    def imageWidthByReference = new int[1];
    def imageHeightByReference = new int[1];

    if (FSDK.LoadImageFromFileW(imageHandle, fileName) == FSDK.FSDKE_OK) {

      FSDK.GetImageWidth(imageHandle, imageWidthByReference)
      FSDK.GetImageHeight(imageHandle, imageHeightByReference)
      Integer imageWidth = imageWidthByReference[0]
      Integer imageHeight = imageHeightByReference[0]
      double ratio = java.lang.Math.min((width + 0.4) / imageWidth,
          (height + 0.4) / imageHeight)
      HImage image2Handle = new HImage()
      FSDK.CreateEmptyImage(image2Handle)
      FSDK.ResizeImage(imageHandle, ratio, image2Handle)
      FSDK.CopyImage(image2Handle, imageHandle)
      FSDK.FreeImage(image2Handle)


      // save image Integero awt.Image
      def awtImage = new Image[1]
      if (FSDK.SaveImageToAWTImage(imageHandle, awtImage, FSDK.FSDK_IMAGEMODE.FSDK_IMAGE_COLOR_24BIT) != FSDK.FSDKE_OK){
        println "Error displaying picture!"
      } else {
        def img = awtImage[0]
        BufferedImage bimg = null

        FSDK.TFacePosition.ByReference facePosition = new FSDK.TFacePosition.ByReference()
        if (FSDK.DetectFace(imageHandle, facePosition) != FSDK.FSDKE_OK){
          println "No faces found!"
        } else {
          bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB)
          Graphics gr = bimg.getGraphics()
          gr.drawImage(img, 0, 0, null)
          gr.setColor(Color.green)

          int left = facePosition.xc - facePosition.w / 2
          int top = facePosition.yc - facePosition.w / 2
          gr.drawRect(left, top, facePosition.w, facePosition.w)

          facialFeatures = new FSDK_FaceTemplate.ByReference()
          FSDK.GetFaceTemplate(imageHandle, facialFeatures)

          FSDK_Features features = new FSDK_Features();
          def attrValues = new String[1]
          float [] ConfidenceMale = new float[1];
          float [] ConfidenceFemale = new float[1];
          FSDK.DetectFacialAttributeUsingFeatures(imageHandle, features, "Gender", attrValues, 10024)
          FSDK.GetValueConfidence(attrValues[0], "Male", ConfidenceMale);
          FSDK.GetValueConfidence(attrValues[0], "Female", ConfidenceFemale);

          println "values male? " + ConfidenceMale + " is female? " + ConfidenceFemale

        }

        String newFileName = fileName.substring(fileName.length() - 5, fileName.length() - 4)
        File outputfile = new File("d:/bimage${newFileName}.jpg");
        ImageIO.write(bimg, "jpg", outputfile);
      }

      FSDK.FreeImage(imageHandle)
    }

    facialFeatures
  }

  public Boolean isSamePerson(def similarity) {

    Boolean result = false

    def threshold = new float[1]
    FSDK.GetMatchingThresholdAtFAR(0.02, threshold)
    println "now threshold is " + threshold + " and similarity " + similarity

    if (similarity[0] > threshold[0]) {
      println "Same person!"
      result = true
    } else {
      println "Persons are different"
    }

    result
  }



  private String encodeFileToBase64Binary(String fileName)
  throws IOException {

    File file = new File(fileName);
    byte[] bytes = loadFile(file);
    byte[] encoded = Base64.encodeBase64(bytes);
    String encodedString = new String(encoded);

    return encodedString;
  }

  private byte[] encodeFileToBase64BinaryBytes(String fileName)
  throws IOException {

    File file = new File(fileName);
    byte[] bytes = loadFile(file);
    byte[] encoded = Base64.encodeBase64(bytes);

    return encoded
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

  public void detectFaces(String filePath) {

    String fileName = filePath
    Image a = null

    HImage imageHandle = new HImage()

    def imageWidthByReference = new int[1];
    def imageHeightByReference = new int[1];

    if (FSDK.LoadImageFromFileW(imageHandle, fileName) == FSDK.FSDKE_OK) {

      FSDK.GetImageWidth(imageHandle, imageWidthByReference)
      FSDK.GetImageHeight(imageHandle, imageHeightByReference)
      Integer imageWidth = imageWidthByReference[0]
      Integer imageHeight = imageHeightByReference[0]
      double ratio = java.lang.Math.min((width + 0.4) / imageWidth,
          (height + 0.4) / imageHeight)
      HImage image2Handle = new HImage()
      FSDK.CreateEmptyImage(image2Handle)
      FSDK.ResizeImage(imageHandle, ratio, image2Handle)
      FSDK.CopyImage(image2Handle, imageHandle)
      FSDK.FreeImage(image2Handle)

      def awtImage = new Image[1];
      if (FSDK.SaveImageToAWTImage(imageHandle, awtImage, FSDK.FSDK_IMAGEMODE.FSDK_IMAGE_COLOR_24BIT) != FSDK.FSDKE_OK){
        println "Error displaying picture!"
      } else {
        def img = awtImage[0]
        BufferedImage bimg = null

        FSDK.TFacePosition.ByReference facePosition = new FSDK.TFacePosition.ByReference()
        if (FSDK.DetectFace(imageHandle, facePosition) != FSDK.FSDKE_OK){
          println "No faces found!"
        } else {
          bimg = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB)
          Graphics gr = bimg.getGraphics()
          gr.drawImage(img, 0, 0, null)
          gr.setColor(Color.green)

          int left = facePosition.xc - facePosition.w / 2
          int top = facePosition.yc - facePosition.w / 2
          gr.drawRect(left, top, facePosition.w, facePosition.w)

          //          FSDK_Features.ByReference facialFeatures = new FSDK_Features.ByReference()
          //          FSDK.DetectFacialFeaturesInRegion(imageHandle, (FSDK.TFacePosition)facePosition, facialFeatures)
          //
          //          FSDK_Features features = new FSDK_Features();
          //          def attrValues = new String[1]
          //          float [] ConfidenceMale = new float[1];
          //          float [] ConfidenceFemale = new float[1];
          //          FSDK.DetectFacialAttributeUsingFeatures(imageHandle, features, attrValues, 1024)
          //          FSDK.GetValueConfidence(attrValues[0], "Male", ConfidenceMale);
          //          FSDK.GetValueConfidence(attrValues[0], "Female", ConfidenceFemale);
          //
          //          println "values " + ConfidenceMale
          //          println "values " + ConfidenceFemale

          //          for (int i = 0; i < FSDK.FSDK_FACIAL_FEATURE_COUNT; ++i){
          //            if (i<2)
          //              gr.setColor(Color.blue)
          //            else if (i==2)
          //              gr.setColor(Color.green)
          //
          //            gr.drawOval(facialFeatures.features[i].x, facialFeatures.features[i].y, 3, 3)
          //          }

          gr.dispose()
        }


        //        img = awtImage[0]

        File outputfile = new File("d:/detectedbimage.jpg");
        ImageIO.write(bimg, "jpg", outputfile);

      }

      FSDK.FreeImage(imageHandle)
    }
  }
}
