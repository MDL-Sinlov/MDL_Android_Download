[TOC]

# MDL_Android_Download

- this download use Android DownloadManager
- Can check SD Path for support DownloadManager
- download info recording at SQLiteDB
- Use WeakReference for less MEM use

> Version 0.0.1 method count 

Less Runtime :
- minSdkVersion 14
- gradle or maven
- jar [Download just like this Path](https://github.com/MDL_Sinlov/MDL_Android_Download/raw/master/mvn-repo/mdl/sinlov/android/download/0.0.1/download-0.0.1-jarLib.jar)

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
        url 'https://raw.githubusercontent.com/MDL_Sinlov/MDL_Android_Download/master/mvn-repo/'
    }
    jcenter()
    ...
}
```

in module `build.gradle`

```gradle
dependencies {
    compile 'mdl.sinlov.android:download:0.0.1'
}
```

# Usage

## Fast use



### init `MDLDownload`

```java
MDLDownload mdlDownload = new MDLDownload(this, DOWNLOAD_FOLDER_NAME, new TestDownloadCallback());

// get download folder
mdlDownload.getDownloadFolder();

//for get download info
ArrayList<SQLDownLoadInfo> downloadInfo = mdlDownload.getDownloadInfoByDB();
```

### new Callback

- new class `TestDownloadCallback implements OnDownloadListener` for get info

```java
private class TestDownloadCallback implements OnDownloadListener {


        @Override
        public void downloading(long downloadId, long download, long total) {
            ALog.d("downloading id: " + downloadId + " download: " + download
                    + " total: " + total
            );
        }

        @Override
        public void downloadSuccess(long downloadId, String downloadUri) {
            ALog.i("downloadSuccess id: " + downloadId + " downloadUri: " + downloadUri);
        }


        @Override
        public void downloadComplete(long downloadId, String downloadUri) {
            ALog.v("downloadComplete id: " + downloadId + " downloadUri: " + downloadUri);
        }


        @Override
        public void downloadError(long downloadId, int errorCode) {
            ALog.e("downloadError id: " + downloadId + " errorCode: " + errorCode);
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