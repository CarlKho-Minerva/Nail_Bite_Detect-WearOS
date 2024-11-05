# My Wear App

My Wear App is an Android Wear application that uses the accelerometer to detect specific movements and prompts the user to confirm if they bit their nail.

## Features

- Detects movement using the accelerometer
- Prompts the user with buttons to confirm or deny nail biting
- Vibrates when the movement threshold is met

## Permissions

The app requires the following permissions:

- `android.permission.WAKE_LOCK`
- `android.permission.BODY_SENSORS`
- `android.permission.VIBRATE`

## How to Change the App Name in Android Studio

1. Open the `res/values/strings.xml` file.
2. Locate the `<string name="app_name">My Wear App</string>` element.
3. Change the value `My Wear App` to your desired app name.
4. Update the `android:label` attribute in the `AndroidManifest.xml` file if necessary.

### Example

```xml
<!-- filepath: /Users/cvk/AndroidStudioProjects/mywearapp/wear/src/main/res/values/strings.xml -->
<resources>
    <string name="app_name">New App Name</string>
    <!-- other strings -->
</resources>
```

```xml
<!-- filepath: /Users/cvk/AndroidStudioProjects/mywearapp/wear/src/main/AndroidManifest.xml -->
<application
    android:allowBackup="true"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:supportsRtl="true"
    android:theme="@android:style/Theme.DeviceDefault">
    <!-- other configurations -->
</application>
```

## Building and Running

1. Clone the repository.
2. Open the project in Android Studio.
3. Build and run the project on an Android Wear device or emulator.

## License

This project is licensed under the MIT License.
