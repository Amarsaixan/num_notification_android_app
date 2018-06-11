package com.example.user.num_notification;

public class Notify {
    private String TITLE;
    private String LINK;
    private String SUMMARY;
    Notify(){

    }
    Notify(String title, String link, String summary){
        this.TITLE = title;
        this.LINK = link;
        this.SUMMARY = summary;
    }
    public String getTITLE(){
        return TITLE;
    }
    public String getLINK(){
        return LINK;
    }
    public String getSUMMARY(){
        return SUMMARY;
    }
    public void setTITLE(String title){ this.TITLE = title;}
    public void setLINK(String link){ this.LINK = link;}
    public void setSUMMARY(String summary){ this.SUMMARY = summary;}

}
