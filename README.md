# Knot It Android App  [![Google Play](http://developer.android.com/images/brand/en_generic_rgb_wo_45.png)](https://play.google.com/store/apps/details?id=com.forkthecode.knotit)

This repository contains the source code for the Knot It Android app.

[![Download from Google Play](http://knotit.forkthecode.com/images/cover.jpg)](https://play.google.com/store/apps/details?id=com.forkthecode.knotit)


Please see the [issues](https://github.com/ForkTheCode/Knot-It/issues) section to
report any bugs or feature requests and to see the list of known issues.

## License

* [MIT License](https://github.com/ForkTheCode/Knot-It/blob/master/LICENSE)

## Building

### With Gradle

The easiest way to build is to install [Android Studio](https://developer.android.com/sdk/index.html) v1.+
with [Gradle](https://www.gradle.org/) v2.2.1.
Once installed, then you can import the project into Android Studio:

1. Open `File`
2. Import Project
3. Select `build.gradle` under the project directory
4. Click `OK`

Then, Gradle will do everything for you.

### With Maven

Notes: Although Maven support is not dropped as yet, to say the least, we have shifted our focus to use Gradle as our
main build system.

The build requires [Maven](http://maven.apache.org/download.html)
v3.1.1+ and the [Android SDK](http://developer.android.com/sdk/index.html)
to be installed in your development environment. In addition you'll need to set
the `ANDROID_HOME` environment variable to the location of your SDK:

```bash
export ANDROID_HOME=/opt/tools/android-sdk
```

## Contributing

Please fork this repository and contribute back using
[pull requests](https://github.com/ForkTheCode/Knot-It/pulls).

Any contributions, large or small, major features, bug fixes, additional
language translations, unit/integration tests are welcomed and appreciated
but will be thoroughly reviewed and discussed.
You can also help us by providing translations. Translate [strings.xml](https://github.com/ForkTheCode/Knot-It/blob/master/KnotIt/app/src/main/res/values/strings.xml) file.
