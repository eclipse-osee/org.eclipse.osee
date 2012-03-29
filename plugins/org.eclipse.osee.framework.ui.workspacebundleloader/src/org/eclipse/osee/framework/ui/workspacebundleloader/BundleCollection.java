package org.eclipse.osee.framework.ui.workspacebundleloader;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BundleCollection {

	Map<String, List<BundleInfoLite>> bundleByName = new HashMap<String, List<BundleInfoLite>>();
	Map<URL, BundleInfoLite> bundleByURL = new HashMap<URL, BundleInfoLite>();

	public BundleCollection(){
		
	}
	
	public void add(BundleInfoLite bundle){
		List<BundleInfoLite> bundles = bundleByName.get(bundle.getSymbolicName());
		if(bundles == null){
			bundles = new ArrayList<BundleInfoLite>();
			bundleByName.put(bundle.getSymbolicName(), bundles);
		}
		bundles.add(bundle);
		bundleByURL.put(bundle.getSystemLocation(), bundle);
	}
	
	public List<BundleInfoLite> getByBundleName(String name){
		return bundleByName.get(name);
	}
	
	public BundleInfoLite getByURL(URL url){
		return bundleByURL.get(url);
	}

	public List<BundleInfoLite> getInstalledBundles() {
		List<BundleInfoLite> bundles = new ArrayList<BundleInfoLite>();
		for(BundleInfoLite info: bundleByURL.values()){
			if(info.isInstalled()){
				bundles.add(info);
			}
		}
		return bundles;
	}

	public List<BundleInfoLite> getLatestBundles() {
		List<BundleInfoLite> latest = new ArrayList<BundleInfoLite>();
		for(List<BundleInfoLite> infolist:this.bundleByName.values()){
			if(infolist.size() == 1){
				latest.add(infolist.get(0));
			} else {
				long lastModified = 0;
				BundleInfoLite newest = null;
				for(BundleInfoLite lite:infolist){
					long newLastModified = new File(lite.getSystemLocation().getFile()).lastModified();
					if(newLastModified > lastModified){
						lastModified = newLastModified;
						newest = lite;
					}
				}
				latest.add(newest);
			}
		}
		return latest;
	}
	
	
	
}
