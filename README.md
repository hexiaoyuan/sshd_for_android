# sshd_for_android

SSHD For Android, based dropbear-server.

### 1. Download source code

```
> git clone https://github.com/hexiaoyuan/sshd_for_android.git
```

### 2. Build dropbear-server by youself

If you want to build dropbear by yourself:

```
> git clone https://github.com/hexiaoyuan/dropbear.git
> cd dropbear
> git checkout ndk-build
> ndk-build NDK_PROJECT_PATH=. APP_BUILD_SCRIPT=./Android.mk
> cd ..
```

now coy dropbear files to sshd4android assert:

```
> cp dropbear/libs/armeabi/* sshd_for_android/sshd4android/app/src/main/assets/
```

### 3. *NOTE* create your authorized_keys

````
touch sshd_for_android/sshd4android/app/src/main/assets/authorized_keys
````
add your public ssh-key in there if you need.

### 4.Build sshd4android by AndroidStudio
open Android-Studio Project sshd_for_android/sshd4android and build it.

