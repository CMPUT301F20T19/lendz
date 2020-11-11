package cmput301.team19.lendz;

public class BorrowerInfo {

    private String fullName;
    private String photo;
    private String timeStamp;
    private String id;
    // General constructor
    public BorrowerInfo(){}

    public BorrowerInfo(String fullName, String photo, String timeStamp,String id){
        this.fullName = fullName;
        this.photo = photo;
        this.timeStamp = timeStamp;
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
