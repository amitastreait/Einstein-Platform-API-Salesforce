public class EinsteinOCRResponse {
    public String task;	
    public Probabilities[] probabilities;
    public class Probabilities {
        public Double probability;
        public String label;	
        public BoundingBox boundingBox;
    }
    public class BoundingBox {
        public Integer minX;	
        public Integer minY;	
        public Integer maxX;	
        public Integer maxY;	
    }
}
