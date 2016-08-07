package cn.coolbhu.snailgo.permissions;

public interface PermissionCallback {
    void permissionGranted();

    void permissionRefused();
}