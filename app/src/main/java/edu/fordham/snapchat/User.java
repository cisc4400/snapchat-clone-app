package edu.fordham.snapchat;

public class User {

    private String mEmail;
    private String mUid;

    // Needed for Firebase
    public User() {
    }

    public User(String email, String uid) {
        mEmail = email;
        mUid = uid;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String e) {
        mEmail = e;
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String e) {
        mUid = e;
    }

}
