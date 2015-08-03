package net.tr.moneywall.youyou;

import a.b.c.AdManager;
import a.b.c.os.OffersManager;
import android.app.Activity;

public class YYMoneyWallManager {
	static YYMoneyWallManager instance;
	Activity mContext;
	String uid;
	
	public static YYMoneyWallManager get(){
		if (instance==null) {
			instance = new YYMoneyWallManager();
		}
		return instance;
	}
	public void init(Activity context, String uid) {
		this.uid = uid;
		this.mContext = context;
		AdManager.getInstance(mContext).init("0c3de68f509c00a2",
				"7eaf8908df982138", false);
		OffersManager.getInstance(mContext).onAppLaunch();
	}
}
