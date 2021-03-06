package com.aispeech.aios.adapter.vendor.GDCAR;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.aispeech.aios.adapter.bean.PoiBean;
import com.aispeech.aios.adapter.config.AiosApi;
import com.aispeech.aios.adapter.config.Configs;
import com.aispeech.aios.adapter.config.Configs.MapConfig;
import com.aispeech.aios.adapter.node.HomeNode;
import com.aispeech.aios.adapter.util.APPUtil;
import com.aispeech.aios.adapter.vendor.GD.GDOperate;

/**
 * Created by zzj on 2016/3/10.
 * 高德车机版接口
 */
public class GDCAROperator {

    private static final String TAG = "AIOS-Adapter-GDCAROperator";
    private Context mContext;
    private static GDCAROperator mIntance;

    private GDCAROperator(Context context) {
        this.mContext = context;
    }


    public static synchronized GDCAROperator getInstance(Context context) {
        if (null == mIntance) {
            mIntance = new GDCAROperator(context);
        }
        return mIntance;
    }

    /**
     * 开始导航接口
     *
     * @param p 目标poi信息
     */
    public void startNavigation(PoiBean p) {
    	if (APPUtil.getInstance().isInstalled(Configs.MapConfig.PACKAGE_GDMAPFORCATJ)) {
			Intent intent = new Intent("AUTONAVI_STANDARD_BROADCAST_RECV");
			intent.putExtra("KEY_TYPE", 10038);
			intent.putExtra("LAT", p.getLatitude());
			intent.putExtra("LON",p.getLongitude());
			intent.putExtra("DEV", 0);
			intent.putExtra("STYLE", 2);
			mContext.sendBroadcast(intent);
		} else if (APPUtil.getInstance().isInstalled(Configs.MapConfig.PACKAGE_GDMAPFORCAT)) {
            String lat = String.valueOf(p.getLatitude());
            String lon = String.valueOf(p.getLongitude());
            String cat = "android.intent.category.DEFAULT";
            String dat = "androidauto://navi?sourceApplication=AIOS-Adapter&lat=" + lat + "&lon=" + lon + "&dev=0&style=2";
            String pkg = "com.autonavi.amapauto";
            Intent i = new Intent();
            i.setData(android.net.Uri.parse(dat));
            i.setPackage(pkg);
            i.addCategory(cat);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            mContext.startActivity(i);
        } else if(APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_GDMAP)){
        	 String lat = String.valueOf(p.getLatitude());
             String lon = String.valueOf(p.getLongitude());
             String cat = "android.intent.category.DEFAULT";
             String dat = "androidamap://navi?sourceApplication=AIOS-Adapter&lat=" + lat + "&lon=" + lon + "&dev=0&style=2";
             String pkg = "com.autonavi.minimap";
             Intent i = new Intent();
             i.setData(android.net.Uri.parse(dat));
             i.setPackage(pkg);
             i.addCategory(cat);
             i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
             mContext.startActivity(i);
    	} else {
            Toast.makeText(mContext, "请先安装高德地图车机版", Toast.LENGTH_LONG).show();
            HomeNode.getInstance().getBusClient().publish(AiosApi.Other.UI_CLICK);//停掉交互
        }
    }

    /**
     * 显示当前位置
     */
    public void locate() {
    	if (APPUtil.getInstance().isInstalled(Configs.MapConfig.PACKAGE_GDMAPFORCATJ)) {
    		Intent i = new Intent("AUTONAVI_STANDARD_BROADCAST_RECV");
    		i.putExtra("KEY_TYPE", 10008);
            mContext.sendBroadcast(i);
        } else if (APPUtil.getInstance().isInstalled(Configs.MapConfig.PACKAGE_GDMAPFORCAT)) {
            String act = "android.intent.action.VIEW";
            String cat = "android.intent.category.DEFAULT";
            String data = "androidauto://myLocation?sourceApplication=AIOS-Adapter";
            String pkg = "com.autonavi.amapauto";
            Intent i = new Intent();
            i.setAction(act);
            i.addCategory(cat);
            i.setData(android.net.Uri.parse(data));
            i.setPackage(pkg);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            mContext.startActivity(i);
        }else if(APPUtil.getInstance().isInstalled(MapConfig.PACKAGE_GDMAP)){
   		 	GDOperate.getInstance(mContext).locate();
        }else {
            Toast.makeText(mContext, "请先安装高德地图", Toast.LENGTH_LONG).show();
            HomeNode.getInstance().getBusClient().publish(AiosApi.Other.UI_CLICK);//停掉交互
        }
    }

    /**
     * 打开地图操作
     */
    public void openMap() {
        String act = "android.intent.action.VIEW";
        String cat = "android.intent.category.DEFAULT";
        String data = "androidauto://rootmap?sourceApplication=AIOS-Adapter";
        String pkg = "com.autonavi.amapauto";
        Intent i = new Intent();
        i.setAction(act);
        i.addCategory(cat);
        i.setData(android.net.Uri.parse(data));
        i.setPackage(pkg);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        mContext.startActivity(i);

    }

    /**
     * 退出高德
     */
    public void closeMap() {//强制结束掉高德APP，需要root权限
/*    	Intent intent = new Intent("android.intent.action.VIEW",android.net.Uri.parse("androidauto://naviExit?sourceApplication=TXZAssistant"));
		intent.setPackage(Configs.MapConfig.PACKAGE_GDMAPFORCAT);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		mContext.startActivity(intent);*/
    	
    	if (APPUtil.getInstance().isInstalled(Configs.MapConfig.PACKAGE_GDMAPFORCATJ)) {
    		Intent mIntent = new Intent("AUTONAVI_STANDARD_BROADCAST_RECV");
    		mIntent.putExtra("KEY_TYPE",10010);
    		mContext.sendBroadcast(mIntent);

    		new Handler().postDelayed(new Runnable() {

    			@Override
    			public void run() {
    				try {
    					 APPUtil.getInstance().forceStopPackage(Configs.MapConfig.PACKAGE_GDMAPFORCATJ);
    				} catch (Exception e) {
    					e.printStackTrace();
    				}
    			}
    		}, 1000);

    	}
    	if (APPUtil.getInstance().isInstalled(Configs.MapConfig.PACKAGE_GDMAPFORCAT)) {
    		Intent intent = new Intent("AUTONAVI_STANDARD_BROADCAST_RECV");
    		intent.putExtra("KEY_TYPE", 10021);
    		mContext.sendBroadcast(intent);
        	
        	new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                	 try {
                         APPUtil.getInstance().forceStopPackage(Configs.MapConfig.PACKAGE_GDMAPFORCAT);
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                }
            }, 1000);
    	}
		
    }

}
