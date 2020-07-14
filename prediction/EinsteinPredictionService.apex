public class EinsteinPredictionService {

    public static String VISION_API = 'https://api.einstein.ai/v2/vision';
    public static String PREDICT = VISION_API + '/predict';
    
    public static String MODEL = 'GeneralImageClassifier';
    
    public static String LANGUAGE_API = 'https://api.einstein.ai/v2/language';
    public static String SENTIMENT = LANGUAGE_API + '/sentiment';
    public static String INTENT = LANGUAGE_API + '/intent';
    
    public static void identifyImage() {
        String sample = 'https://icatcare.org/app/uploads/2018/07/Thinking-of-getting-a-cat.png';
        String result = EinsteinAPIService.predictImage(PREDICT, sample, MODEL, false);
        parseResponse(result);
    }
    
    public static void identifyImageBase64() {
        List<ContentDocumentLink> contentLink = [SELECT ContentDocumentId, LinkedEntityId  
                                                 FROM ContentDocumentLink where LinkedEntityId ='0010o00002jzULuAAM'];
        if(!contentLink.isEmpty()){
            ContentVersion content = [SELECT Title,VersionData FROM ContentVersion 
                                      where ContentDocumentId =: contentLink.get(0).ContentDocumentId 
                                      LIMIT 1];
            String sample = EncodingUtil.base64Encode(content.VersionData);
            String result = EinsteinAPIService.predictImage(PREDICT, sample, MODEL, true);
            parseResponse(result);
        }
    }
    private static void parseResponse(String result){
        EinsteinPredictionResponse response = (EinsteinPredictionResponse)
            				System.JSON.deserialize(result, EinsteinPredictionResponse.class);
        for(EinsteinPredictionResponse.Probabilities prob : response.probabilities){
            System.debug(System.LoggingLevel.DEBUG, prob.label+' '+prob.probability);
        }
    }    
}
