package cmput301.team19.lendz;

public class BorrowerInfo {

    private String fullName;
    private String borrowerId;
    private String requestDocumentId;
    private String bookID;

    // General constructor
    public BorrowerInfo(){}

    public String getRequestDocumentId() {
        return requestDocumentId;
    }

    public void setRequestDocumentId(String requestDocumentId) {
        this.requestDocumentId = requestDocumentId;
    }


    public BorrowerInfo(String fullName, String borrowerId, String requestDocumentId, String bookID){
        this.fullName = fullName;
        this.borrowerId = borrowerId;
        this.requestDocumentId = requestDocumentId;
        this.bookID = bookID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getId() {
        return borrowerId;
    }

    public void setId(String borrowerId) {
        this.borrowerId = borrowerId;
    }

    public String getBookID() {
        return bookID;
    }

    public void setBookID(String bookID) {
        this.bookID = bookID;
    }
}
