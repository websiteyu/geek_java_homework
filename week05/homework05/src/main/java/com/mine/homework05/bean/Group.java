package com.mine.homework05.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Data
//@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Group {
    public Group(){
        this.users = new ArrayList<User>(){{
            add(new User(3,"wangw"));
        }};
    }

    private List<User> users;
}
