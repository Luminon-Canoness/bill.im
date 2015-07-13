package kr.edcan.billim.utils;

import java.util.List;

/**
 * Created by kotohana5706 on 15. 7. 12.
 */
public class User {
    public int id;
    public String name;
    public String photo;
    public String phone;
    public String description;
    public List<Group> groups;
    public boolean enabled;
    public String token;
    public int give;
    public int take;
    public int exchange;

    public User(int id, String name, String photo, String phone, String description, List<Group> groups, boolean enabled, String token, int give, int take, int exchange) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.phone = phone;
        this.description = description;
        this.groups = groups;
        this.enabled = enabled;
        this.token = token;
        this.give = give;
        this.take = take;
        this.exchange = exchange;
    }
}