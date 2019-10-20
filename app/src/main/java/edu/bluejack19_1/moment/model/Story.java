package edu.bluejack19_1.moment.model;

public class Story {

    private String imageurl;
    private long timestart;
    private long timeend;
    private String storyid;
    private String userid;

    public Story(String imageUrl, long timeStart, long timeEnd, String storyID, String userID) {
        this.imageurl = imageUrl;
        this.timestart = timeStart;
        this.timeend = timeEnd;
        this.storyid = storyID;
        this.userid = userID;
    }

    public Story() {}

    public String getUserID() {
        return userid;
    }

    public void setUserID(String userid) {
        this.userid = userid;
    }

    public String getImageUrl() {
        return imageurl;
    }

    public void setImageUrl(String imageurl) {
        this.imageurl = imageurl;
    }

    public long getTimeStart() {
        return timestart;
    }

    public void setTimeStart(long timestart) {
        this.timestart = timestart;
    }

    public long getTimeEnd() {
        return timeend;
    }

    public void setTimeEnd(long timeend) {
        this.timeend = timeend;
    }

    public String getStoryID() {
        return storyid;
    }

    public void setStoryID(String storyid) {
        this.storyid = storyid;
    }
}
