package net.ahwater.main.accept;

/**
 * Created by Reeye on 2020/4/24 9:02
 * Nothing is true but improving yourself.
 */
public interface LockService {

    boolean tryLock(String key);

    boolean unlock(String key);

}
