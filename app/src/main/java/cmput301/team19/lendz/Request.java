package cmput301.team19.lendz;

public class Request {

    private User requester;
    private RequestStatus status;

    public Request(User requester, RequestStatus status) {
        this.requester = requester;
        this.status = status;
    }

    public User getRequester() {
        return requester;
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }
}
