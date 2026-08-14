# EmergencyButton

A CharaROM fork of [Panic](https://github.com/CalyxOS/platform_packages_apps_Panic) by CalyxOS, which uses Guardian Project's [PanicKit](https://github.com/guardianproject/PanicKit) library.

Package name changed from `org.calyxos.panic` to `com.android.emergencybutton` for better obscurity - blends in with system apps and avoids detection by integrity checkers that flag known privacy tools.

## What is it?

An app that allows users to uninstall selected apps and run certain actions when a system-wide panic intent is triggered. Works with [TriggerResponse](https://github.com/CharaROMAndroid/android_packages_apps_TriggerResponse) (CharaROM's fork of Ripple) as the panic trigger.

## CharaROM Integration

Requires a `frameworks/base` patch to allow silent uninstallation:

```java
// In DeletePackageHelper.java isCallerAllowedToSilentlyUninstall()
if (callingUid == snapshot.getPackageUid("com.android.emergencybutton", 0, callingUserId)) {
    return true;
}
```

Add to product makefile:

```makefile
PRODUCT_PACKAGES += \
    EmergencyButton \
    TriggerResponse
```

## License

Apache 2.0, same as upstream.
