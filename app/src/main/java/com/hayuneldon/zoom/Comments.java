package com.hayuneldon.zoom;

import java.util.ArrayList;

/**
 * Created by 2017hchong on 1/11/2016.
 */
public class Comments {

    private ArrayList<String> CommentBy = new ArrayList<String>();
    private ArrayList<String> CommentData = new ArrayList<String>();

    //A constructor to be used for actual initialization of object from information fetched from
    //other sources like a web-service or on device storage (SQLite database).
    public Comments(ArrayList<String> commentBy, ArrayList<String> commentData)
    {
        super();
        CommentBy = commentBy;
        CommentData = commentData;
    }

    //My impromptu constructor used to initialize Comments object with a set of preset sample comments
    //Make sure you remove this constructor when using for actual app.
    public Comments() {
        super();

        CommentBy.clear();
        CommentData.clear();

        //Standard Blue User
        CommentBy.add("Average_Joe");
        CommentData.add("I love seeing movies made from Comics characters!!");

        //Standard Black User
        CommentBy.add("Naseem");
        CommentData.add("Ssh! This is just a tutorial/demo dont attract attention here."+"\n\n"+"You don't know what kind of people will be attracted towards this discussion on the web.");

        //Joker
        CommentBy.add("Joker");
        CommentData.add("Ooh! A new discussion! Do you want to hear a joke?");

        //Batman
        CommentBy.add("Batman");
        CommentData.add("Where did you get your hands on an Android phone in Arkham?");

        //Joker
        CommentBy.add("Joker");
        CommentData.add("@Batman Party pooper! I was just about to crack a joke. And, how did you know I am here on the web?");

        //Batman
        CommentBy.add("Batman");
        CommentData.add("Because I'm Batman! And, I'm the world's greatest detective; I figured it out."+"/n/n"+"@Joker Now, tell me where'd you get an Android device in a padded cell?");

        //Standard Blue User
        CommentBy.add("Average_Joe");
        CommentData.add("@Batman Don't you have, like, a big Batcomputer you use to find out criminals?");

        //Standard Black User
        CommentBy.add("Naseem");
        CommentData.add("@Batman Yes. I'm interested in how did you find out Joker was trolling in this discussion? I'd like to know your technique.");

        //Deadpool
        CommentBy.add("Deadpool!");
        CommentData.add("Hey Batman, you here? I just read your status on Facebook that you were on this discussion app. Thought I'd join in."+"\n\n\n"+"@Naseem Oh! Don't bother knowing his technique. He left me mid chat on FB when when Joker updated his status on Twitter talking about a new discussion app he had found to troll for the Lulz.");

    }

    public String getCommentBy(int position) {
        return CommentBy.get(position);
    }

    public void setCommentBy(String commentBy, int position) {
        CommentBy.add(position, commentBy);
    }

    public String getCommentData(int position) {
        return CommentData.get(position);
    }

    public void setCommentData(String commentData, int position) {
        CommentData.add(position, commentData);
    }

    public int numberOfComments() {
        return CommentBy.size();
    }

}