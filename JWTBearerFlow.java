public class JWTBearerFlow {
    
    public static String getAccessToken(String tokenEndpoint, JWT jwt) {
        
        String grantType = 'urn:ietf:params:oauth:grant-type:jwt-bearer';
        String access_token = null;
        String body = 'grant_type='+EncodingUtil.urlEncode(grantType, 'UTF-8')+'&assertion=' + jwt.assertion();
        HttpRequest req = new HttpRequest();                            
        req.setMethod('POST');
        req.setEndpoint(tokenEndpoint);
        req.setHeader('Content-type', 'application/x-www-form-urlencoded');
        req.setBody(body);
        Http http = new Http();               
        try{
            HTTPResponse res = http.send(req);
            if ( res.getStatusCode() == 200 ) {
                Map<String, Object> responseMap = (Map<String, Object>)JSON.deserializeUntyped(res.getBody());
                access_token = (String)responseMap.get('access_token');
            }else{
                System.debug('JWTBearerFlow Error Occurred '+res.getBody());
            }
        }catch(Exception ex){
            if(String.valueOf(ex).startsWith('Unauthorized endpoint')){
                System.debug('JWTBearerFlow Please check Setup->Security->Remote site settings and add '+tokenEndpoint);
            }else{
                System.debug('JWTBearerFlow  '+ex.getStackTraceString());
                System.debug('JWTBearerFlow '+ex);
            }
        }
        return access_token;
    }
}
