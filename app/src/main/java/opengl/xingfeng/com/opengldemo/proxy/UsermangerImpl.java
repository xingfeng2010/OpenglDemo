package opengl.xingfeng.com.opengldemo.proxy;

public class UsermangerImpl implements MyUserManager{
    @Override
    public void addUser(String userId, String userName) {
        System.out.println("UserManagerImpl.addUser");
    }
}
