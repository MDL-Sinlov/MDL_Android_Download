[TOC]

# MDL_Android_Download

- this download use Android DownloadManager
- Can check SD Path for support DownloadManager
- download info recording at SQLiteDB
- Use WeakReference for less MEM use

> Version 0.0.2 method count 112

Less Runtime :
- minSdkVersion 14
- gradle or maven
- jar [Download just like this Path](https://github.com/MDL-Sinlov/MDL_Android_Download/raw/master/mvn-repo/mdl/sinlov/android/download/0.0.1/download-0.0.1-jarLib.jar)

> eclipse just use every repo at version `download-x.x.x-jarLib.jar`

Project Runtime:
- Android Studio 2.1.2
- appcompat-v7:23.4.0
- Gradle 2.10
- com.android.tools.build:gradle:2.1.2

# Dependency

at root project `build.gradle`

```gradle
repositories {
    maven {
        url 'https://raw.githubusercontent.com/MDL-Sinlov/MDL_Android_Download/master/mvn-repo/'
    }
    jcenter()
    ...
}
```

in module `build.gradle`

```gradle
dependencies {
    compile 'mdl.sinlov.android:download:0.0.2'
}
```

# Usage

## Fast use



### init `MDLDownload`

```java
// nomal way static use
MDLDownload mdlDownload = new MDLDownload(this, DOWNLOAD_FOLDER_NAME, new TestDownloadCallback());

// last params version can use like app version for clear old download for remove DB data
// download file will not delete because it was cast to much resource
MDLDownload mdlDownload = new MDLDownload(this, DOWNLOAD_FOLDER_NAME, new TestDownloadCallback(), 2);

// get download folder
mdlDownload.getDownloadFolder();

//for get download info
ArrayList<SQLDownLoadInfo> downloadInfo = mdlDownload.getDownloadInfoByDB();

//if you are update version please clear all data

```

### new Callback

- new class `TestDownloadCallback implements OnDownloadListener` for get info

```java
private class TestDownloadCallback implements OnDownloadListener {


        @Override
        public void downloading(long downloadId, long status, MDLDownLoadInfo mdlDownLoadInfo) {
            // downloadId and status fast to query, all info in mdlDownLoadInfo
        }

        @Override
        public void downloadComplete(long downloadId, MDLDownLoadInfo mdlDownLoadInfo) {
            //all info in mdlDownLoadInfo {@link DownloadManager#STATUS_SUCCESSFUL}
        }


        @Override
        public void downloadError(long downloadId, int errorCode) {
            // errorCode in {@link DownloadManager#ERROR_UNKNOWN} and so no.
        }

        @Override
        public void downloadOutChange(long downloadId, long status) {
            // if out change download will call back at here
        }
        
        @Override
        public void downloadHistory(long downloadId, String downloadUri) {
            // history info
        }
    }
```

- must `Observer data change at lifecycle`

```java
    @Override
    protected void onResume() {
        super.onResume();
        mdlDownload.bind();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mdlDownload.unBind();
    }
```

### submit download

```java
mdlDownload.submitDownload(APK_URL, DOWNLOAD_FILE_NAME, true);
```

#License

---

Copyright 2016 sinlovgm@gmail.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.