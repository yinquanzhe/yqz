package net.ahwater.dataServ.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Created by Reeye on 2019/8/12 10:00
 * Nothing is true but improving yourself.
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Slf4j
@Component
public class MyPermissionEvaluator implements PermissionEvaluator {



    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return true;
//        Object principal = authentication.getPrincipal();
//        if (principal instanceof UserBO) {
//            UserBO user = (UserBO) authentication.getPrincipal();
//            List<RoleVO> roles = roleDao.listRoleWithPermissionsByUserId(user.getId());
//            return roles.stream()
//                    .anyMatch(e -> e.getPermissions().stream()
//                            .anyMatch(p -> {
//                                return MenuCache.menus.stream()
//                                        .filter(m -> m.getId().equals(p.getMid()))
//                                        .collect(Collectors.toList())
//                                        .stream()
//                                        .anyMatch(m1 -> m1.getKey().equals(targetDomainObject.toString())
//                                                && m1.getOperation().equals(permission.toString()));
//                            }));
//        }
//        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        log.debug("{}=={}=={}=={}", authentication, targetId, targetType, permission);
        return false;
    }

}
