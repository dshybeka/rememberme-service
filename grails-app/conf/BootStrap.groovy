import org.rememberme.security.SecRole
import org.rememberme.security.SecUser
import org.rememberme.security.SecUserSecRole

class BootStrap {

  def init = { servletContext ->

    def user =new SecUser(username:"test", password:"test123")
    user.save()

    def roleUser=new SecRole(authority:"ROLE_USER")
    roleUser.save()

    new SecUserSecRole(secUser:user, secRole:roleUser).save()
  }
  def destroy = {
  }
}
