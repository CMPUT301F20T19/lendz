package cmput301.team19.lendz;

public class BorrowerInfo {

    private String fullName;
    private String id;
    // General constructor
    public BorrowerInfo(){}

    public BorrowerInfo(String fullName,String id){
        this.fullName = fullName;
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
