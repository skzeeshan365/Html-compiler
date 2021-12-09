package com.reiserx.htmlcompiler.Models;

public class updateAppss {

    public String url, version;

    public updateAppss() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public updateAppss(String url, String version) {
        this.url = url;
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
