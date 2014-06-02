import org.remeberme.sec.RemebermeRestAuthTokenJsonRenderer
import org.rememberme.util.PhotoUtil

// Place your Spring DSL code here
beans = {

  xmlns context:"http://www.springframework.org/schema/context"

  context.'component-scan'('base-package': "org.rememberme")

  restAuthenticationTokenJsonRenderer(RemebermeRestAuthTokenJsonRenderer) {}

  photoUtil(PhotoUtil) {
    grailsApplication = ref("grailsApplication")
  }

}
