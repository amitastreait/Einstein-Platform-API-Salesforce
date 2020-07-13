public class EinsteinAPIService {

    public static FINAL STRING OAUTH_END_POINT = 'https://api.einstein.ai/v2/oauth2/token';
    
    private static String getAccessToken() {
        
        /* Get the key file from the File Object that we have downloaded from Einstein platfom API*/
        ContentVersion base64Content = [SELECT Title, VersionData 
                                        FROM ContentVersion
                                        WHERE Title='einstein_platform' OR 
                                        Title='predictive_services'
                                        ORDER BY Title 
                                        LIMIT 1];
        
        String keyContents = base64Content.VersionData.tostring();
        keyContents = keyContents.replace('-----BEGIN RSA PRIVATE KEY-----', '');
        keyContents = keyContents.replace('-----END RSA PRIVATE KEY-----', '');
        keyContents = keyContents.replace('\n', '');
        
        // Get a new token
        JWT jwt = new JWT('RS256');
        // jwt.cert = 'JWTCert'; 
        // Uncomment this if you used a Salesforce certificate to sign up for an Einstein Platform account
        jwt.pkcs8 = keyContents; // Comment this if you are using jwt.cert
        jwt.iss = 'developer.force.com';
        jwt.sub = 'sfdcpanther@gmail.com';
        jwt.aud =  OAUTH_END_POINT;
        jwt.exp = '3600';
        String access_token = JWTBearerFlow.getAccessToken(OAUTH_END_POINT, jwt);
        return access_token;    
    }
    public static String imageOCR(String endPoint, String sample, String model, boolean isBase64){
        String result = einsteinAPICall(endPoint, sample, model, isBase64);
        return result;
    }
    public static String predictImage(String endPoint, String sample, String model, boolean isBase64){
        String result = einsteinAPICall(endPoint, sample, model, isBase64);
        return result;
    }
    private static String einsteinAPICall(String endPoint, String sample, String model, boolean isBase64) {
        string contentType = HttpFormBuilder.GetContentType();
        String access_token = getAccessToken();
        
        //  Compose the form
        string form64 = '';
		
        form64 += HttpFormBuilder.WriteBoundary();
        form64 += HttpFormBuilder.WriteBodyParameter('modelId', EncodingUtil.urlEncode(model, 'UTF-8'));
        form64 += HttpFormBuilder.WriteBoundary();
        if(isBase64) {
            form64 += HttpFormBuilder.WriteBodyParameter('sampleBase64Content', sample);
        } else {
            form64 += HttpFormBuilder.WriteBodyParameter('sampleLocation', sample);
        }
        form64 += HttpFormBuilder.WriteBoundary(HttpFormBuilder.EndingType.CrLf);

        blob formBlob = EncodingUtil.base64Decode(form64);
        string contentLength = string.valueOf(formBlob.size());
        
        HttpRequest httpRequest = new HttpRequest();

        httpRequest.setBodyAsBlob(formBlob);
        httpRequest.setHeader('Connection', 'keep-alive');
        httpRequest.setHeader('Content-Length', contentLength);
        httpRequest.setHeader('Content-Type', contentType);
        httpRequest.setMethod('POST');
        httpRequest.setTimeout(120000);
        httpRequest.setHeader('Authorization','Bearer ' + access_token);
        httpRequest.setEndpoint(endPoint);

        Http http = new Http();
        try {
            HTTPResponse res = http.send(httpRequest);
            if (res.getStatusCode() == 200) {
                return res.getBody();
            }
        } catch(System.CalloutException e) {
            System.debug('ERROR:' + e);
            return e.getStackTraceString();
        }
        return null;
    }
}
