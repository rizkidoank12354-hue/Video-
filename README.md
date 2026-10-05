# Video Player (Android)

Minimal local video player. Uses only the Android platform `VideoView` and
`MediaController`, so there are no third-party dependencies.

Features: pick a video from storage, play/pause/seek, rotation-safe, and it can
be chosen as the app to open any `video/*` file from a file manager or gallery.

## Build the APK

Requirements: JDK 17+ and the Android SDK (Android Studio installs both).

### Option A: Android Studio
1. File > Open > select this `VideoPlayer` folder.
2. Wait for Gradle sync, then Build > Build APK(s).

### Option B: command line
Point Gradle at your SDK once (adjust the path), then build:

    echo "sdk.dir=$HOME/Android/Sdk" > local.properties
    gradle assembleDebug

(Use `gradlew` instead of `gradle` if you generate a wrapper with
`gradle wrapper`. AGP 8.7 needs Gradle 8.9 or newer.)

The APK is written to:

    app/build/outputs/apk/debug/app-debug.apk

Debug builds are signed automatically with the debug key, so the file is
directly installable:

    adb install app/build/outputs/apk/debug/app-debug.apk

Or copy it to the phone and open it (allow "install unknown apps").

## Build in the cloud (no Android Studio needed)
1. Create a new repository on GitHub and upload everything in this folder
   (including the hidden `.github` folder).
2. Open the repository's **Actions** tab. The "Build APK" workflow runs on
   every push (or press **Run workflow**).
3. When it finishes (about 3-5 minutes), open the run and download the
   **video-player-apk** artifact from the bottom of the page. Unzip it to get
   `app-debug.apk`, then install it on your phone.

## Notes
- minSdk 21 (Android 5.0), targetSdk 35.
- `VideoView` uses the device's built-in codecs. Formats your phone cannot
  decode natively (some MKV/HEVC variants) will show "Cannot play this video".
  For wider format support, swap in Media3 ExoPlayer.
