package kr.edcan.billim.utils;

import java.util.List;

import retrofit.mime.TypedFile;

/**
 * Created by kotohana5706 on 15. 7. 13.
 */
public class Article {
    public int id;
    public int group;
    public int type;
    public int category;
    public String name;
    public String description;
    public String photo;
    public String reward;
    public String location;
    public User author;
    public User responder;
    public int state;
    public List<Comment> comments;
}
