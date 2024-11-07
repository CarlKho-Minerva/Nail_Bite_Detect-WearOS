# Nail Bite Detector

Nail Bite Detector is an Android Wear application that uses the accelerometer to detect specific movements and prompts the user to confirm if they bit their nail.

## Features

- Detects movement using the accelerometer
- Prompts the user with buttons to confirm or deny nail biting
- Vibrates when the movement threshold is met

## Permissions

The app requires the following permissions:

- `android.permission.WAKE_LOCK`
- `android.permission.BODY_SENSORS`
- `android.permission.VIBRATE`

## If you want to see the database file directly, you can use the Device File Explorer in Android Studio:

1. Open Device File Explorer: In Android Studio, go to View > Tool Windows > Device File Explorer.

2. Navigate to the Database File: In the Device File Explorer, navigate to the directory where your app's data is stored. The path is typically /data/data/com.example.my_wear_app/databases/.

3. Download the Database File: Right-click on the database file (e.g., responses.db) and select Save As to download it to your local machine.

4. Inspect the Database File: Use a SQLite database viewer (e.g., DB Browser for SQLite) to open and inspect the database file.

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