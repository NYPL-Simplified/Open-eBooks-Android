# Build Setup

## Java Prerequisites

1. You should have Java SE 8u101 or [a newer version of Java SE 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html) installed. (Java SE 7u111 and newer versions of Java SE 7 may also work but have not been tested.) This is because older versions of Java do not trust [Let's Encrypt](https://letsencrypt.org/) which provides our SSL certificate.

2. The `JAVA_HOME` environment variable must be set correctly. You can check what it is set to in most shells with `echo $JAVA_HOME`. If that command does not show anything, adding the following line to `~/.profile` (assuming you are on macOS) and then executing `source ~/.profile` or opening a new shell should suffice:

~~~w
# Replace NNN with your particular version of 1.8.0.
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_NNN.jdk/Contents/Home
~~~

3. You can verify that everything is set up correctly by inspecting the results of both `java -version` and `javac -version`.

## IntelliJ IDEA Prerequisites

**NOTE:** Android Studio _will not work_ with this project due to a lack of Maven support.

The latest version of IntelliJ IDEA is strongly recommended. Versions older than 2017.2.5 have not been tested.

## Maven Prerequisites

Maven version 3.3.9 or newer is required. Versions older than 3.5.0 have not been recently tested.

 The latest version can be obtained directly from [the Apache Maven Project's download page](https://maven.apache.org/download.cgi). You can also install Maven from MacPorts (via the `maven3` package) or Nixpkgs (via the `apache-maven` package). The Homebrew package is poorly maintained as of this writing and is not recommended.

 If you choose to download Maven directly from the Apache Maven Project, you will need to add the path to the `bin` directory containing `mvn` to your `PATH` environment variable. If you are unsure how to do this, ask a coworker.

## Android SDK Tools Prerequisites

The latest version of the Android SDK tools is recommended. You can download them from [the Android developer site](https://developer.android.com/studio/index.html). Be sure to download only the command line tools and _not_ Android Studio. You must download these tools even if you already have Android Studio installed.

After the tools have been downloaded and unpacked, place the resulting `tools` directory _inside a new empty directory_ that will become your Android home directory. You should then add the following lines to your `.profile`, `.bash-profile`, or similar (replacing `ANDROID_HOME_PATH` appropriately with the path to the directory _containing_ the `tools` directory):

```
export ANDROID_HOME=ANDROID_HOME_PATH
export ANDROID_NDK_HOME=$ANDROID_HOME/ndk-bundle
export PATH="$PATH:$ANDROID_HOME/platform-tools/"
```

After doing this, either open a new shell or use the `source` command to load your updated profile.

## Android SDK Setup

After the SDK tools have been installed, it is also necessary to install several SDK packages.

One way to do this is by invoking `sdkmanager` directly via the following commands—be sure to enter them one at a time as some interactive portions for agreeing to licenses are present:

```
cd $ANDROID_HOME
./tools/bin/sdkmanager --verbose 'platforms;android-21' ndk-bundle platform-tools tools 'extras;android;m2repository'
./tools/bin/sdkmanager --verbose `./tools/bin/sdkmanager --list | grep build-tools | sort -r | head -n 1 | cut -d '|' -f 1 | tr -d ' '`
```

Alternatively, you can use the GUI provided by the IntelliJ Android plugin. It is recommended to use the above commands however as it is less error-phone than trying to tick all of the appropriate checkboxes.

## Nexus Setup

**NOTE:** This section contains signing-related secrets that _must not_ be shared outside of NYPL.

Our application needs packages that are only available from our Nexus server in order to build correctly. Nexus credentials can be obtained by emailing `nypl@winniequinn.com` or by asking in the `#simplified-android` channel of [librarysimplified.slack.com](https://librarysimplified.slack.com).

Once you have your credentials, the following should be placed at `~/.m2/settings.xml` with `USERNAME` and `PASSWORD` replaced appropriately:

```
<?xml version="1.0" encoding="UTF-8"?>
<settings
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <servers>
    <server>
      <id>nypl-nexus-group</id>
      <username>USERNAME</username>
      <password>PASSWORD</password>
    </server>
  </servers>
  <profiles>
    <profile>
      <id>nypl</id>
      <properties>
          <sign.keystore>nypl-keystore.jks</sign.keystore>
          <sign.alias>nypl</sign.alias>
          <sign.storepass><![CDATA[F3Sr&ECs+bqj&8QupRwHWYCd]]></sign.storepass>
          <sign.keypass><![CDATA[UPf3cVM7Nj$Jt*=N73Mk653d]]></sign.keypass>
      </properties>
      <repositories>
        <repository>
          <id>nypl-nexus-group</id>
          <name>NYPL nexus repo group</name>
          <url>https://nexus.librarysimplified.org:8443/nexus/content/groups/external</url>
          <layout>default</layout>
        </repository>
      </repositories>
    </profile>
  </profiles>
  <activeProfiles>
    <activeProfile>nypl</activeProfile>
  </activeProfiles>
</settings>
```

## Keystore Setup

In order to sign APKs, a Java keystore is required. Download it from the following URL while logged into GitHub in your browser and then place it at `~/.m2/nypl-keystore.jks`:

https://github.com/NYPL-Simplified/Certificates/raw/master/APK%20Signing/nypl-keystore.jks

## Adobe Certificate Setup

In order for Adobe DRM to work, the correct certificate file must be bundled into the app. Download it from the following URL while logged into GitHub in your browser and then place it at `simplified-app-openebooks/src/main/assets/ReaderClientCert.sig`:

https://github.com/NYPL-Simplified/Certificates/raw/master/OpenEbooks/Android/ReaderClientCert.sig

## HelpStack Setup

**NOTE:** Care should always be taken to ensure HelpStack is functioning correctly after making any configuration changes. Configuration errors or a lack of configuration may result in errors that only appear at runtime.

If HelpStack is to be used, a configuration file must be placed at `simplified-app-simplye/src/main/assets/helpstack.conf`.

For Zendesk, you should use the following configuration:

```
helpstack.gear = zendesk
helpstack.zendesk.instance_url = ...
helpstack.zendesk.staff_email = ...
helpstack.zendesk.api_token = ...
```

For Salesforce Desk, use the following instead:

```
helpstack.gear = desk
helpstack.desk.instance_url = ...
helpstack.desk.to_help_email = ...
helpstack.desk.staff_login_email = ...
helpstack.desk.staff_login_password =  ...
```

## Generating Signed APKs

If you wish to generate a signed APK for publishing the application, you will need to set the following values correctly in `~/.gradle/gradle.properties`:

~~~
org.librarysimplified.simplye.keyAlias=
org.librarysimplified.simplye.keyPassword=
org.librarysimplified.simplye.storePassword=
~~~

In addition, you will need to obtain the correct Java keystore and either place it in the project at `simplified-app-simplye/keystore.jks` or create a symbolic link at the same location appropriately. All files matching `*.jks` are set to be ignored by Git, but care should always be taken to ensure keystores and other secrets are never committed regardless.

Once the above has been completed, executing `./gradlew assembleRelease` will generate the signed APK and place it at `./simplified-app-simplye/build/outputs/apk/simplified-app-simplye-release.apk`.

## Branding And Configurable Features

See [simplified-app-shared/README-Branding.md](simplified-app-shared/README-Branding.md)
for documentation on how to customize branding of the application.

# Building

**NOTE:** Due to an unknown issue, you must execute `./gradlew assembleDebug` one time before opening the project in Android Studio. This will pull in all dependencies that, for whatever reason, are not fetched if Gradle is executed via Android Studio.

After setup is complete, the project can be opened in Android Studio and built as normal.

