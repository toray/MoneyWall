package net.tr.moneywall.youyou;

import i.o.p.os.df.Npat;
import i.o.p.os.df.NpauList;
import i.o.p.os.df.Npbd;

import java.util.ArrayList;
import java.util.List;

import net.tr.moneywall.MAdsApp;
import a.b.c.os.OffersManager;
import android.app.Activity;
import android.content.Context;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.ztzhushou.DevInit;

public class YoumiList {
	Activity mContext;
	String mUid;
	Handler mHandler = new Handler(Looper.getMainLooper());
	private static final String TAG = "YoumiList";
	public YoumiList(Activity activity, String uid) {
		mContext = activity;
		mUid = uid;
//		DevInit.setCurrentUserID(mContext, mUid);
		Log.e(TAG, "YoumiList初始化成功");
		OffersManager.getInstance(mContext).setCustomUserId(mUid);
	}
	public void getList(AppListResponse response){
		ArrayList<MAdsApp> arrayList = new ArrayList<MAdsApp>();
		getYouMiAppAds(arrayList,response);
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
