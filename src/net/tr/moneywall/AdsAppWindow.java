package net.tr.moneywall;

import i.o.p.os.df.Npat;
import i.o.p.os.df.NpauList;
import i.o.p.os.df.Npbd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.tr.moneywall_lib.R;
import net.youmi.android.offers.OffersManager;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.nostra13.universalimageloader.ImageLoaderManager;
import com.toraysoft.manager.AppManager;
import com.toraysoft.widget.viewpagertabicon.ViewPagerTabIcon;
import com.ztzhushou.DevInit;
import com.ztzhushou.GetAdListListener;

public class AdsAppWindow extends Base {
	Activity mContext;
	View mRootView;
	ViewPagerTabIcon mViewPagerTabIcon;
	ViewPager mViewPager;
	ViewGroup view_apps;
	TextView tv_title;
	Button btn_more, btn_install;
	int count = 0;
	int points;

	List<AdsAppView> appViews;

	List<MAdsApp> items;

	AdsAppAdapter mAdsAppAdapter;

	Handler mHandler = new Handler(Looper.getMainLooper());

	int balance = 0;
	String userId;
	OnClickListener onBtnMoreClickListener;
	OnInitAppListListener onInitAppListListener;

	public AdsAppWindow(Activity context, String userId, int balance) {
		this.mContext = context;
		this.userId = userId;
		this.balance = balance;
		appViews = new ArrayList<AdsAppWindow.AdsAppView>();
		init();
	}

	void init() {
		this.mRootView = LayoutInflater.from(mContext).inflate(
				R.layout.view_ads_apps, null);
		mViewPagerTabIcon = (ViewPagerTabIcon) mRootView
				.findViewById(R.id.viewpagertabicon);
		this.view_apps = (ViewGroup) mRootView.findViewById(R.id.layout_apps);
		this.tv_title = (TextView) mRootView.findViewById(R.id.tv_title);
		this.btn_more = (Button) mRootView.findViewById(R.id.btn_more);
		this.btn_install = (Button) mRootView.findViewById(R.id.btn_install);

		setTitle(points);
		btn_install.setText(mContext.getString(R.string.btn_install_points, 0));

		btn_more.setOnClickListener(onBtnMoreClickListener);
		btn_install.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				downloadAdsApps();
			}
		});

		items = new ArrayList<MAdsApp>();
		mAdsAppAdapter = new AdsAppAdapter(items);
		mViewPagerTabIcon.setAdapter(mAdsAppAdapter);

		setBackgroundDrawable(new BitmapDrawable(mContext.getResources(),
				(Bitmap) null));

		setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

		setAnimationStyle(R.style.AppPopWindow_Anim);
		setTouchable(true);
		setFocusable(true);
		setOutsideTouchable(true);
		setContentView(mRootView);
		// dianle
		DevInit.setCurrentUserID(mContext, userId);
		// youmi
		OffersManager.getInstance(mContext).setCustomUserId(userId);
		// qumi
		// MiApiConnect.getMiapiConnectInstance().setUserId(Env.get().getUserId());
	}

	public void show(View view) {
		// getApps();
		getAdsApps();
		showAtLocation(view, Gravity.BOTTOM, 0, 0);
	}

	public void setPoints(int points) {
		this.points = points;
		if (tv_title != null) {
			setTitle(points);
		}
	}

	void setTitle(int points) {
		int p = getLastPoints();
		int defaultColor = mContext.getResources().getColor(
				R.color.text_black_first);
		int selectColor = mContext.getResources().getColor(
				R.color.text_red_first);
		int len = String.valueOf(p).length();
		SpannableString ss = new SpannableString(mContext.getString(
				R.string.ads_app_title, p));
		ss.setSpan(new ForegroundColorSpan(defaultColor), 0, 7,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ss.setSpan(new ForegroundColorSpan(selectColor), 7, 7 + len,
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ss.setSpan(new ForegroundColorSpan(defaultColor), 7 + len, ss.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv_title.setText(ss);
	}

	void getAdsApps() {
		// List<AdInfo> list =
		// MiApiConnect.getMiapiConnectInstance(mContext).getAdInfoList();
		// final List<MAdsApp> apps = MAdsApp.parse(list);
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
				getYouMiAppAds(apps);
			}

			@Override
			public void getAdListFailed(String error) {
				Log.e("DevInitSdk", String.format("请求错误： %s，", error));
				final List<MAdsApp> apps = new ArrayList<MAdsApp>();
				getYouMiAppAds(apps);
			}
		});
	}

	void getYouMiAppAds(final List<MAdsApp> apps) {
		Npbd.getInstance(mContext).mfs(100);
		Npbd.getInstance(mContext).mfq(true);
		Npbd.getInstance(mContext).meo(Npbd.pads, true, new Npat() {
			@Override
			public void mfb(Context context, final NpauList adList) {
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						apps.addAll(MAdsApp.parse(adList));
						initAdsAppList(apps);
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
						initAdsAppList(apps);
					}
				});
			}

			@Override
			public void mez() {
				Log.e("YoumiSdk", "请求失败，请检查网络");
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						initAdsAppList(apps);
					}
				});
			}
		});
	}

	void initAdsAppList(List<MAdsApp> apps) {
		apps = MAdsApp.sort(apps);
		if (onInitAppListListener != null) {
			apps = onInitAppListListener.onInit(apps);
		}
		items.clear();
		if (apps.size() > 16) {
			for (int i = 0; i < 16; i++) {
				items.add(apps.get(i));
			}
		} else {
			items.addAll(apps);
		}
		initAdsAppChecked();
		mAdsAppAdapter.notifyDataSetChanged();
	}

	void initAdsAppChecked() {
		int points = getLastPoints();
		for (MAdsApp app : items) {
			points -= app.getPoints();
			app.setCheck(true);
			if (points < 0) {
				break;
			}
		}
		setAdsAppCheckedPoints();
	}

	void setAdsAppCheckedPoints() {
		int points = 0;
		for (MAdsApp app : items) {
			if (app.isCheck()) {
				points += app.getPoints();
			}
		}
		btn_install.setText(mContext.getString(R.string.btn_install_points,
				points));
	}

	void downloadAdsApps() {
		boolean isCheck = false;
		for (MAdsApp app : items) {
			if (app.isCheck()) {
				app.download(mContext);
				isCheck = true;
			}
		}
		if (isCheck)
			dismiss();
	}

	void initAppChecked() {
		int points = getLastPoints();
		for (AdsAppView adsAppView : appViews) {
			if (adsAppView.app != null) {
				points -= adsAppView.app.getPoints();
				adsAppView.checked();
			}
			if (points < 0) {
				break;
			}
		}
		btn_install.setText(mContext.getString(R.string.btn_install_points,
				getCheckedPoints()));
	}

	int getLastPoints() {
		int p = points - balance;
		if (p <= 0) {
			p = points;
		}
		return p;
	}

	int getCheckedPoints() {
		int points = 0;
		for (AdsAppView adsAppView : appViews) {
			if (adsAppView.checked) {
				points += adsAppView.app.getPoints();
			}
		}
		return points;
	}

	class AdsAppView implements OnClickListener {
		View view_parent;
		ImageView iv_cover, iv_checked;
		TextView tv_name, tv_points;
		MAdsApp app;
		int position;
		boolean checked = false;

		public AdsAppView(View view, int position) {
			this.view_parent = view;
			iv_cover = (ImageView) view_parent.findViewById(R.id.iv_cover);
			iv_checked = (ImageView) view_parent.findViewById(R.id.iv_checked);
			tv_name = (TextView) view_parent.findViewById(R.id.tv_name);
			tv_points = (TextView) view_parent.findViewById(R.id.tv_points);
			iv_cover.setImageResource(R.drawable.icon_app_default);

			this.position = position;
		}

		void init(MAdsApp app) {
			this.app = app;
			checked = false;
			iv_checked.setVisibility(View.GONE);
			if (app != null) {
				view_parent.setVisibility(View.VISIBLE);
				iv_cover.setImageResource(R.drawable.icon_app_default);
				tv_name.setText(app.getName());
				ImageLoaderManager.get().displayImage(app.getIcon(), iv_cover);
				int points = app.getPoints();
				if (points > 0) {
					tv_points.setText(mContext.getString(
							R.string.ads_app_points, app.getPoints()));
				} else {
					tv_points.setText(mContext.getString(R.string.ads_app_rec,
							app.getPoints()));
				}
				view_parent.setOnClickListener(this);
			} else {
				view_parent.setVisibility(View.INVISIBLE);
				view_parent.setOnClickListener(null);
			}

		}

		@Override
		public void onClick(View v) {
			if (checked) {
				checked = false;
				iv_checked.setVisibility(View.GONE);
			} else {
				checked = true;
				iv_checked.setVisibility(View.VISIBLE);
			}
			btn_install.setText(mContext.getString(R.string.btn_install_points,
					getCheckedPoints()));
		}

		void checked() {
			checked = true;
			iv_checked.setVisibility(View.VISIBLE);
		}

		void download() {
			app.download(mContext);
		}
	}

	class AdsAppAdapter extends PagerAdapter {

		List<MAdsApp> items;
		int mChildCount = 0;

		public AdsAppAdapter(List<MAdsApp> items) {
			this.items = items;
		}

		@Override
		public int getCount() {
			int count = 1;
			if (items != null && items.size() > 0) {
				count = (int) Math.ceil((double) items.size() / (double) 8);
				if (count > 2) {
					count = 2;
				}
			}
			return count;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (View) arg1;
		}

		@Override
		public void notifyDataSetChanged() {
			mChildCount = getCount();
			super.notifyDataSetChanged();
			mViewPagerTabIcon.setAdapter(this);
		}

		@Override
		public int getItemPosition(Object object) {
			if (mChildCount > 0) {
				mChildCount--;
				return POSITION_NONE;
			}
			return super.getItemPosition(object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ViewHolder vh = new ViewHolder(mContext);
			vh.init(position);
			container.addView(vh.view_parent);
			return vh.view_parent;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		class ViewHolder {
			Context mContext;
			View view_parent;
			GridView gv_pre;
			int position;
			AppItemAdapter mAppItemAdapter;

			public ViewHolder(Context context) {
				this.mContext = context;
			}

			void init(int position) {
				this.position = position;
				view_parent = LayoutInflater.from(mContext).inflate(
						R.layout.view_grid, null);
				gv_pre = (GridView) view_parent.findViewById(R.id.gv_pre);
				mAppItemAdapter = new AppItemAdapter(position);
				gv_pre.setAdapter(mAppItemAdapter);
				// gv_pre.setOnItemClickListener(this);
			}

			// @Override
			// public void onItemClick(AdapterView<?> parent, View view,
			// int position, long id) {
			// }

			class AppItemAdapter extends BaseAdapter {

				int index;

				public AppItemAdapter(int index) {
					this.index = index;
				}

				@Override
				public int getCount() {
					return 8;
				}

				@Override
				public Object getItem(int position) {
					return position;
				}

				@Override
				public long getItemId(int position) {
					return position;
				}

				@Override
				public View getView(int position, View convertView,
						ViewGroup parent) {
					ItemViewHolder vh = null;
					if (convertView != null && convertView.getTag() != null) {
						vh = (ItemViewHolder) convertView.getTag();
					} else {
						vh = new ItemViewHolder();
					}
					vh.init(position);
					convertView = vh.view_parent;
					convertView.setTag(vh);
					return convertView;
				}

				class ItemViewHolder {
					View view_parent;
					ImageView iv_cover, iv_checked;
					TextView tv_name, tv_points;
					boolean isInit;

					public ItemViewHolder() {

					}

					void init(int position) {
						position = index * getCount() + position;
						if (!isInit) {
							isInit = true;
							view_parent = LayoutInflater.from(mContext)
									.inflate(R.layout.item_ads_app, null);
							iv_cover = (ImageView) view_parent
									.findViewById(R.id.iv_cover);
							iv_checked = (ImageView) view_parent
									.findViewById(R.id.iv_checked);
							tv_name = (TextView) view_parent
									.findViewById(R.id.tv_name);
							tv_points = (TextView) view_parent
									.findViewById(R.id.tv_points);
						}
						if (position < items.size()) {
							final MAdsApp app = items.get(position);
							if (app.isCheck()) {
								iv_checked.setVisibility(View.VISIBLE);
							} else {
								iv_checked.setVisibility(View.GONE);
							}
							view_parent.setVisibility(View.VISIBLE);
							iv_cover.setImageResource(R.drawable.icon_app_default);
							tv_name.setText(app.getName());
							ImageLoaderManager.get().displayImage(
									app.getIcon(), iv_cover);
							int points = app.getPoints();
							if (points > 0) {
								tv_points.setText(mContext.getString(
										R.string.ads_app_points,
										app.getPoints()));
							} else {
								tv_points.setText(mContext.getString(
										R.string.ads_app_rec, app.getPoints()));
							}
							view_parent
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											app.setCheck(!app.isCheck());
											if (app.isCheck()) {
												iv_checked
														.setVisibility(View.VISIBLE);
											} else {
												iv_checked
														.setVisibility(View.GONE);
											}
											setAdsAppCheckedPoints();
										}
									});
						} else {
							view_parent.setVisibility(View.INVISIBLE);
						}
					}
				}

			}
		}

	}

	public void setOnBtnMoreClickListener(OnClickListener onBtnMoreClickListener) {
		this.onBtnMoreClickListener = onBtnMoreClickListener;
	}

	public static interface OnInitAppListListener {
		public List<MAdsApp> onInit(List<MAdsApp> apps);
	}

	public void setOnInitAppListListener(
			OnInitAppListListener onInitAppListListener) {
		this.onInitAppListListener = onInitAppListListener;
	}
}
