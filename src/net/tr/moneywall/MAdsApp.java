package net.tr.moneywall;

import i.o.p.os.df.Npal;
import i.o.p.os.df.Npau;
import i.o.p.os.df.NpauList;
import i.o.p.os.df.Npbd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.dig.zdy.AdInfo;
import com.ko.game.tools.MiApiConnect;
import com.ztzhushou.AdType;
import com.ztzhushou.DevInit;
import com.ztzhushou.OnAddPointsListener;

public class MAdsApp {
	
	
	enum AppChannelType{
		YOUMI, DIANLE, QUMI, APP_REC,
	}
	
	AppChannelType mAppChannelType;
	String id;
	String name;
	String icon;
	String url;
	int points;
	int type;
	Object app;
	boolean isCheck;
	int t=2;
	
	public MAdsApp(Npau app){
		this.mAppChannelType = AppChannelType.YOUMI;
		this.id = String.valueOf(app.mdg());
		this.name = app.mdm();
		this.icon = app.mdt();
		this.points = app.mdx();
		this.type=t;
		this.app = app;
		
		System.out.println("==========name============"+name);
	}
	
	public MAdsApp(AdInfo app){
		this.mAppChannelType = AppChannelType.QUMI;
		this.id = String.valueOf(app.getAdId());
		this.name = app.getAdName();
		this.icon = app.getAdIconUrl();
		this.points = app.getAdPoint();
		this.type=t;
		this.app = app;
	}
	
	public MAdsApp(HashMap app){
		this.mAppChannelType = AppChannelType.DIANLE;
		this.id = app.get("pack_name").toString();
		this.name = app.get("name").toString();
		this.icon = app.get("icon").toString();
		this.points = Integer.parseInt(app.get("number").toString());
		this.app = app;
		this.type=t;
		System.out.println("======================"+name);
	}
	
	public MAdsApp(JSONObject app) throws JSONException{
		this.mAppChannelType = AppChannelType.APP_REC;
		this.name = app.getString("n");
		this.icon = app.getString("i");
		this.isCheck = true;
		this.type=t;
		this.app = app;
	}
	
	public static List<MAdsApp> parse(NpauList adList){
		List<MAdsApp> apps = new ArrayList<MAdsApp>();
		if (adList != null && adList.size() > 0) {
            for (int i = 0; i < adList.size(); ++i) {
            	Npau npau = adList.get(i);
//            	if(npau.mdi()==Npal.NOT_COMPLETE && npau.mde()==Npam.EXPERIENCE){
            	if(npau.mdi()==Npal.NOT_COMPLETE){
            		MAdsApp app = new MAdsApp(npau);
                	apps.add(app);
            	}
            }
        }
		return apps;
	}
	
	public static List<MAdsApp> parse(List<AdInfo> adList){
		List<MAdsApp> apps = new ArrayList<MAdsApp>();
		if (adList != null && adList.size() > 0) {
            for (int i = 0; i < adList.size(); ++i) {
            	AdInfo adInfo = adList.get(i);
            	MAdsApp app = new MAdsApp(adInfo);
            	apps.add(app);
            }
        }
		return apps;
	}
	
	
	public static List<MAdsApp> parse(Set<HashMap> adList){
		List<MAdsApp> apps = new ArrayList<MAdsApp>();
		if (adList != null && adList.size() > 0) {
           for (HashMap map : adList) {
        	   MAdsApp app = new MAdsApp(map);
        	   apps.add(app);
           }
        }
		return apps;
	}
	
	public static List<MAdsApp> sort(List<MAdsApp> apps){
		Comparator<MAdsApp> comp = new SortComparator();  
		Collections.sort(apps,comp);
		return apps;
	}
	
	public static class SortComparator implements Comparator<MAdsApp> {

		@Override
		public int compare(MAdsApp lhs, MAdsApp rhs) {
			return rhs.points - lhs.points;
		}  
	}  
	
	public void download(Context context){
		if(mAppChannelType == AppChannelType.QUMI){
			MiApiConnect.getMiapiConnectInstance().downloadAd(context, this.id);
		}else if(mAppChannelType == AppChannelType.YOUMI){
			Npbd.getInstance(context).mfg((Npau)this.app);
		}else if(mAppChannelType == AppChannelType.APP_REC){
			try {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				Uri uri = Uri.parse(((JSONObject)app).getString("u"));
				intent.setData(uri);  
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(mAppChannelType == AppChannelType.DIANLE){
			System.out.println("=================download==============="+name);
			HashMap a = (HashMap) this.app;
			DevInit.download(context, name, AdType.ADLIST, new OnAddPointsListener() {
				
				@Override
				public void addPointsSucceeded(String adName, String packname, int point) {
					System.out.println("==============addPointsSucceeded============"+adName+"====potint===="+point);
				}
				
				@Override
				public void addPointsFailed(String error) {
					System.out.println("==============addPointsFailed============"+error);
				}
			});
		}
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getIcon() {
		return icon;
	}

	public int getPoints() {
		return points;
	}
	
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public boolean isCheck() {
		return isCheck;
	}
	
	public int gettype() {
		return type;
	}

	public int settype() {
		return type;
	}
	
}
