package cmput301.team19.lendz;

import android.content.res.Resources;

public enum BookStatus {
    AVAILABLE,
    REQUESTED,
    BORROWED,
    ACCEPTED;

    public String toString(Resources resources) {
        switch (this) {
            case AVAILABLE:
                return resources.getString(R.string.available);
            case REQUESTED:
                return resources.getString(R.string.requested);
            case BORROWED:
                return resources.getString(R.string.borrowed);
            case ACCEPTED:
                return resources.getString(R.string.accepted);
            default:
                return toString();
        }
    }
}
