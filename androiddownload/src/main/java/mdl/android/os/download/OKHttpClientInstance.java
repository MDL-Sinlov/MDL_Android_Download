package mdl.android.os.download;

import com.squareup.okhttp.OkHttpClient;

public class OKHttpClientInstance {
    private static OKHttpClientInstance sInstance;

    private OkHttpClient mOkHttpClient;

    public synchronized static OKHttpClientInstance getInstance() {
        if (sInstance == null) {
            sInstance = new OKHttpClientInstance();
        }
        return sInstance;
    }

    private OKHttpClientInstance() {
        mOkHttpClient = new OkHttpClient();
    }

    public OkHttpClient client() {
        return mOkHttpClient;
    }

}
