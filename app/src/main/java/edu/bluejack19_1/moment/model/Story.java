package edu.bluejack19_1.moment.model;

public class Story {

    private String imageUrl;
    private long timeStart;
    private long timeEnd;
    private String storyID;
    private String userID;

    public Story(String imageUrl, long timeStart, long timeEnd, String storyID, String userID) {
        this.imageUrl = imageUrl;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.storyID = storyID;
        this.userID = userID;
    }

    public Story() {}

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getStoryID() {
        return storyID;
    }

    public void setStoryID(String storyID) {
        this.storyID = storyID;
    }
}
