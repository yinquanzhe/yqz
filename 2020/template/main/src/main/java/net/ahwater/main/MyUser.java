package net.ahwater.main;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.ahwater.base.web.security.User;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Reeye on 2020/4/21 15:29
 * Nothing is true but improving yourself.
 */
@Data
@AllArgsConstructor
public class MyUser implements User {

    private String name;

    @Override
    public String identity() {
        return name;
    }

    @Override
    public List<String> getRoles() {
        return Arrays.asList("A", "B");
    }

    @Override
    public List<String> getPermissions() {
        return Arrays.asList("P1", "P2");
    }

}