package com.pkb149.pricedropalert.Utility;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by CoderGuru on 20-12-2017.
 */

public class AmazonSignatureCalc {
    String key;
    String dateStamp;
    String regionName;
    String serviceName;

    public AmazonSignatureCalc(String key, String dateStamp, String regionName, String serviceName){
        this.key=key;
        this.dateStamp=dateStamp;
        this.regionName=regionName;
        this.serviceName=serviceName;
    }
     public byte[] HmacSHA256(String data, byte[] key) throws Exception {
        String algorithm="HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data.getBytes("UTF8"));
    }

    public byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName) throws Exception {
        byte[] kSecret = ("AWS4" + key).getBytes("UTF8");
        byte[] kDate = HmacSHA256(dateStamp, kSecret);
        byte[] kRegion = HmacSHA256(regionName, kDate);
        byte[] kService = HmacSHA256(serviceName, kRegion);
        byte[] kSigning = HmacSHA256("aws4_request", kService);
        return kSigning;
    }
}
