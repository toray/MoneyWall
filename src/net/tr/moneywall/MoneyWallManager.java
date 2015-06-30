package net.tr.moneywall;

import net.tr.wxtheme.AppConnect;
import a.b.c.AdManager;
import a.b.c.os.OffersManager;
import android.app.Activity;
import cn.aow.android.DAOW;

import com.dlnetwork.DevInit;
import com.ko.game.tools.MiApiConnect;
import com.ko.game.tools.MiApiNotifier;

public class MoneyWallManager {

	static MoneyWallManager instance;

	Activity mContext;
	String uid;

	public static MoneyWallManager get() {
		if (instance == null) {
			instance = new MoneyWallManager();
		}
		return instance;
	}

	public void init(Activity context, String uid) {
		this.uid = uid;
		this.mContext = context;
		System.out.println("=====uid=====" + uid);

		AdManager.getInstance(mContext).init("e4e3e9a4730673eb",
				"2c695aa4419f72d8", false);
		OffersManager.getInstance(mContext).onAppLaunch();

		DevInit.initGoogleContext(mContext, "c419eccbf030772e0e278bba5417da30");
		
		com.dlnetwork.DevInit.initGoogleContext(mContext,
				"c419eccbf030772e0e278bba5417da30");
		
		DAOW.getInstance(mContext).init(mContext, "96ZJ0LPQzeNtXwTCOj");
		
		MiApiConnect.ConnectMiApi(mContext, "054e23584371bb9f", "22db345215b85936");
		MiApiConnect.getMiapiConnectInstance(mContext).initZDYAd();
	}

	public void showYouMi() {
		OffersManager.getInstance(mContext).setCustomUserId(uid);
		OffersManager.getInstance(mContext).showOffersWall();
	}

	public void showQuMi() {
		MiApiConnect.getMiapiConnectInstance(mContext).setUserId(uid);
		MiApiConnect.getMiapiConnectInstance(mContext).showOffers(
				new OnMiApiNotifier());
	}

	public void showWanPu() {
		AppConnect.getInstance(mContext).showOffers(mContext, uid);
	}

	public void showDuoMeng() {
		DAOW.getInstance(mContext).setUserId(uid);
		DAOW.getInstance(mContext).show(mContext);
	}

	public void showDianLe() {
		DevInit.setCurrentUserID(mContext, uid);
		DevInit.showOffers(mContext);
	}

	class OnMiApiNotifier implements MiApiNotifier {

		@Override
		public void getUpdatePoints(int pointTotal) {

		}

		@Override
		public void getUpdatePoints(int pointTotal, int earnedpoint) {

		}

		@Override
		public void getUpdatePointsFailed(String error) {

		}
	}

}
