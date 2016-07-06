package mdl.sinlov.download.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import mdl.sinlov.android.log.ALog;
import mdl.sinlov.download.app.ui.BaseDownloadActivity;
import mdl.sinlov.download.app.ui.MDLTestActivity;
import mdl.sinlov.download.app.ui.MultiDownloadActivity;

public class MainActivity extends MDLTestActivity {

    @BindView(R.id.btn_main_base_download)
    Button btnMainBaseDownload;
    @BindView(R.id.btn_main_multi_download)
    Button btnMainMultiDownload;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        ALog.initTag();
//        ALog.initTag().logLevel(ALogLevel.CHANGE_TO_WARNING_ERROR);
    }

    @Override
    protected void bindListener() {

    }

    @OnClick({R.id.btn_main_base_download, R.id.btn_main_multi_download})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_main_base_download:
                skip2Activity(BaseDownloadActivity.class);
                break;
            case R.id.btn_main_multi_download:
                skip2Activity(MultiDownloadActivity.class);
                break;
        }
    }
}
