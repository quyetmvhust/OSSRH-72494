package org.vnpt.watermarkengine;

public class WaterMark {
    private String base64File;
    private String securityData;

    public WaterMark(String base64File, String securityData) {
        this.base64File = base64File;
        this.securityData = securityData;
    }

    public WaterMark() {
    }

    public String getBase64File() {
        return base64File;
    }

    public void setBase64File(String base64File) {
        this.base64File = base64File;
    }

    public String getSecurityData() {
        return securityData;
    }

    public void setSecurityData(String securityData) {
        this.securityData = securityData;
    }
}
