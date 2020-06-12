package net.ahwater.base.log;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Reeye on 2020/4/16 9:35
 * Nothing is true but improving yourself.
 */
@Data
@AllArgsConstructor
public class ExpressionRootObject {

    private final Object object;
    private final Object[] args;

}
