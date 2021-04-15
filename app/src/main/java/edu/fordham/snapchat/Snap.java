package edu.fordham.snapchat;

public class Snap {

    private  String mId;
    private String mFrom;
    private String mName;
    private String mUrl;
    private String mMessage;

    // Needed for Firebase
    public Snap() {
    }

    public Snap(String imageName, String fromEmail, String url, String msg) {
        mName = imageName;
        mFrom = fromEmail;
        mUrl = url;
        mMessage = msg;
    }

    public String getId() {
        return mId;
    }

    public void setId(String n) {
        mId = n;
    }

    public String getName() {
        return mName;
    }

    public void setName(String n) {
        mName = n;
    }

    public String getFrom() {
        return mFrom;
    }

    public void setFrom(String n) {
        mFrom = n;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String e) {
        mUrl = e;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String e) {
        mMessage = e;
    }
}
