package com.fz.admin.framework.datapermission.util;

import com.fz.admin.framework.datapermission.annotation.DataPermission;
import com.fz.admin.framework.datapermission.aop.DataPermissionContextHolder;

import java.util.concurrent.Callable;

/**
 * 数据权限 Util
 *
 *
 */
public class DataPermissionUtils {

    private static DataPermission DATA_PERMISSION_DISABLE;

    @DataPermission(enable = false)
    private static DataPermission getDisableDataPermissionDisable() {
        if (DATA_PERMISSION_DISABLE == null) {

            DATA_PERMISSION_DISABLE = DisableDataPermission.class.getAnnotation(DataPermission.class);

            // DATA_PERMISSION_DISABLE = DataPermissionUtils.class
            //         .getDeclaredMethod("getDisableDataPermissionDisable")
            //         .getAnnotation(DataPermission.class);
        }
        return DATA_PERMISSION_DISABLE;
    }

    /**
     * 忽略数据权限，执行对应的逻辑
     *
     * @param runnable 逻辑
     */
    public static void executeIgnore(Runnable runnable) {
        DataPermission dataPermission = getDisableDataPermissionDisable();
        DataPermissionContextHolder.add(dataPermission);
        try {
            // 执行 runnable
            runnable.run();
        } finally {
            DataPermissionContextHolder.remove();
        }
    }

    /**
     * 忽略数据权限，执行对应的逻辑
     *
     * @param callable 逻辑
     * @return 执行结果
     */

    public static <T> T executeIgnore(Callable<T> callable) throws Exception {
        DataPermission dataPermission = getDisableDataPermissionDisable();
        DataPermissionContextHolder.add(dataPermission);
        try {
            // 执行 callable
            return callable.call();
        } finally {
            DataPermissionContextHolder.remove();
        }
    }


    @DataPermission(enable = false)
    private static class DisableDataPermission {

    }

}
