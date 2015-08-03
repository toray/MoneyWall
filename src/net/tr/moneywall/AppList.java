package net.tr.moneywall;

import i.o.p.os.df.Npat;
import i.o.p.os.df.NpauList;
import i.o.p.os.df.Npbd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ztzhushou.DevInit;
import com.ztzhushou.GetAdListListener;

import a.b.c.os.OffersManager;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class AppList {
	Activity mContext;
	String mUid;
	Handler mHandler = new Handler(Looper.getMainLooper());

	public AppList(Activity activity, String uid) {
		mContext = activity;
		mUid = uid;
//		items = new ArrayList<MAdsApp>();
		DevInit.setCurrentUserID(mContext, mUid);
		OffersManager.getInstance(mContext).setCustomUserId(mUid);
	}


	public void getList(AppListResponse response){
		getAdsApps(response);
	}
	void getAdsApps(final AppListResponse response) {
		DevInit.getList(mContext, 1, 20, new GetAdListListener() {

			@Override
			public void getAdListSucceeded(List adList) {
				Set<HashMap> list = new HashSet<HashMap>();
				for (Object object : adList) {
					if (object instanceof HashMap) {
						HashMap ads = (HashMap) object;
						list.add(ads);
					}
				}
				final List<MAdsApp> apps = MAdsApp.parse(list);
				getYouMiAppAds(apps,response);
			}

			@Override
			public void getAdListFailed(String error) {
				Log.e("DevInitSdk", String.format("请求错误： %s，", error));
				final List<MAdsApp> apps = new ArrayList<MAdsApp>();
				getYouMiAppAds(apps,response);
			}
		});
	}

	void getYouMiAppAds(final List<MAdsApp> apps,final AppListResponse response) {
		Npbd.getInstance(mContext).mfs(100);
		Npbd.getInstance(mContext).mfq(true);
		Npbd.getInstance(mContext).meo(Npbd.pads, true, new Npat() {
			@Override
			public void mfb(Context context, final NpauList adList) {
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						apps.addAll(MAdsApp.parse(adList));
//						for (int i = 0; i < adList.size(); i++) {
//							Log.e("MAdsApp", ""+i+" "+adList.get(i).toString());
//						}
//						
//						
						initAdsAppList(apps,response);
					}
				});
				if (adList == null || adList.size() == 0) {
					Log.i("YoumiSdk", "当前没有广告哦~ 晚点再来吧");
				}
			}

			@Override
			public void mezWithErrorCode(int code) {
				Log.e("YoumiSdk", String.format("请求错误，错误代码 ： %d， 请联系客服", code));
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						initAdsAppList(apps,response);
					}
				});
			}

			@Override
			public void mez() {
				Log.e("YoumiSdk", "请求失败，请检查网络");
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						initAdsAppList(apps,response);
					}
				});
			}
		});
	}

	void initAdsAppList(List<MAdsApp> apps,AppListResponse response) {
		apps = MAdsApp.sort(apps);
		ArrayList<MAdsApp> items = new ArrayList<MAdsApp>();
		if (apps.size() > 16) {
			for (int i = 0; i < 16; i++) {
				items.add(apps.get(i));
			}
		} else {
			items.addAll(apps);
		}
		if (items.size()==0) {
			response.onError("网络错误");
		}else {
			response.onSuccess(items);
		}
		
	}
	public interface AppListResponse{
		public void onSuccess(List<MAdsApp> items);
		public void onError(String error);
		
	}
}
