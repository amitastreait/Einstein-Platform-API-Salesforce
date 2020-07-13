public class EinsteinOCRService {
    public static FINAL String  OCR_API         = 'https://api.einstein.ai/v2/vision/ocr';
    public static FINAL String  OCR_MODEL       = 'OCRModel';
    public static FINAL String  OCR_MODEL_TABEL = 'OCRModel';
    
    public static void readTextFromImageByURL(){
        String sample = 'https://i1.wp.com/www.sfdcpanther.com/wp-content/uploads/2020/07/Day-1.png';
        String result = EinsteinAPIService.imageOCR(OCR_API, sample, OCR_MODEL, false);
        parseResponse(result);
    }
    
    public static void readTextFromImageByBase64(){
        List<ContentDocumentLink> contentLink = [SELECT ContentDocumentId, LinkedEntityId  
                                                 FROM ContentDocumentLink where LinkedEntityId ='0010o00002KIY2SAAX'];
        if(!contentLink.isEmpty()){
            ContentVersion content = [SELECT Title,VersionData FROM 
                                      ContentVersion 
                                      where ContentDocumentId =: contentLink.get(0).ContentDocumentId 
                                      LIMIT 1];
            String sample = EncodingUtil.base64Encode(content.VersionData);
            String result = EinsteinAPIService.imageOCR(OCR_API, sample, OCR_MODEL, true);
            parseResponse(result);
        }
    }
    private static void parseResponse(String ressult){
        EinsteinOCRResponse response = (EinsteinOCRResponse)System.JSON.deserialize(ressult, EinsteinOCRResponse.class);
        for(EinsteinOCRResponse.Probabilities prob : response.probabilities){
            System.debug(System.LoggingLevel.DEBUG, prob.label);
        }
    }
}
