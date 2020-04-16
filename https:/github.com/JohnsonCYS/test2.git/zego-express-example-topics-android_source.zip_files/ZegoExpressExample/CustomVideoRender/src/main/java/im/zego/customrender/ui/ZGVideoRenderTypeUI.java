package im.zego.customrender.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.cc.customrender.R;

import im.zego.common.ui.BaseActivity;
import im.zego.zegoexpress.ZegoExpressEngine;
import im.zego.zegoexpress.constants.ZegoVideoBufferType;
import im.zego.zegoexpress.constants.ZegoVideoFrameFormatSeries;
import im.zego.zegoexpress.entity.ZegoCustomVideoRenderConfig;
import im.zego.zegoexpress.entity.ZegoEngineConfig;

/**
 * 外部渲染返回视频数据的类型选择
 */
public class ZGVideoRenderTypeUI extends BaseActivity {

    private RadioGroup mRenderTypeGroup;



    // 是否已开启外部渲染
    private boolean isEnableExternalRender = false;

    private  ZegoEngineConfig zegoEngineConfig = new ZegoEngineConfig();

    // 加载c++ so
    static {
        System.loadLibrary("nativeCutPlane");
    }

    public static void actionStart(Activity mainActivity) {
        Intent intent = new Intent(mainActivity, ZGVideoRenderTypeUI.class);
        mainActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_render_type);


        zegoEngineConfig.customVideoRenderConfig = new ZegoCustomVideoRenderConfig();
        zegoEngineConfig.customVideoRenderConfig.bufferType = ZegoVideoBufferType.RAW_DATA;


        mRenderTypeGroup = (RadioGroup)findViewById(R.id.RenderTypeGroup);
        final int[] radioRenderTypeBtns = {R.id.RadioDecodeRGB, R.id.RadioDecodeYUV};

        // 设置RadioGroup组件的事件监听
        mRenderTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedID) {

                if (radioRenderTypeBtns[0] == radioGroup.getCheckedRadioButtonId()) {
                    // 外部渲染时抛出rgb格式的视频数据
                    zegoEngineConfig.customVideoRenderConfig.frameFormatSeries = ZegoVideoFrameFormatSeries.RGB;
                } else if (radioRenderTypeBtns[1] == radioGroup.getCheckedRadioButtonId()){
                    // 外部渲染时抛出I420格式的视频数据
                    zegoEngineConfig.customVideoRenderConfig.frameFormatSeries = ZegoVideoFrameFormatSeries.YUV;
                }
                // 推流处开启外部采集功能
            }
        });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 若开启过视频外部渲染，此处关闭
        if (isEnableExternalRender) {
            // 关闭外部渲染功能
            ZegoExpressEngine.setEngineConfig(null);
        }
    }

    public void JumpPublish(View view){

        if (zegoEngineConfig.customVideoRenderConfig != null){
            // 开启外部渲染功能
            ZegoExpressEngine.setEngineConfig(zegoEngineConfig);
            isEnableExternalRender = true;
        }

        Intent intent = new Intent(ZGVideoRenderTypeUI.this, ZGVideoRenderUI.class);
        intent.putExtra("RenderType", zegoEngineConfig.customVideoRenderConfig.frameFormatSeries.value());
        ZGVideoRenderTypeUI.this.startActivity(intent);
    }
}
