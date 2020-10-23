package cmput301.team19.lendz;

import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

public class Book {

    private UUID id;
    private URL photo;
    private User owner;
    private BookStatus status;
    private Location location;
    private BookDescription description;

    private ArrayList<Request> pendingRequests;
    private Request acceptedRequest;

    public Book(UUID id, User owner, BookStatus status, BookDescription description) {
        this.id = id;
        this.owner = owner;
        this.status = status;
        this.description = description;
    }

    public void setAcceptedRequest(Request acceptedRequest) {
        this.acceptedRequest = acceptedRequest;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public URL getPhoto() {
        return photo;
    }

    public void setPhoto(URL photo) {
        this.photo = photo;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public BookDescription getDescription() {
        return description;
    }

    public void setDescription(BookDescription description) {
        this.description = description;
    }

    public void addPendingRequest(Request request) {
        this.pendingRequests.add(request);
    }
}
