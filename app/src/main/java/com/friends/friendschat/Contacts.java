package com.friends.friendschat;

public class Contacts
{
    private String Uname, Status, profileimage;

    public Contacts(){

    }

    public Contacts(String uname, String status, String profileimage) {
        Uname = uname;
        Status = status;
        this.profileimage = profileimage;
    }

    public String getUname() {
        return Uname;
    }



    public String getStatus() {
        return Status;
    }



    public String getProfileimage() {
        return profileimage;
    }


}
