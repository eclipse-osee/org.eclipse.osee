	package org.eclipse.osee.framework.ui.workspacebundleloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.FrameworkWiring;

public class WorkspaceBundleLoadCoordinator {

	private File temporaryBundleLocationFolder;
	private Set<String> bundlesToCheck;
	private BundleCollection managedArea = new BundleCollection();
	private FrameworkWiring wiring;
	
	public WorkspaceBundleLoadCoordinator(File temporaryBundleLocationFolder) {
		bundlesToCheck = new HashSet<String>();
		this.temporaryBundleLocationFolder = temporaryBundleLocationFolder;
		if(!temporaryBundleLocationFolder.exists()){
			if(!temporaryBundleLocationFolder.mkdirs()){
				this.temporaryBundleLocationFolder = makeTempFolder();
			}
		} else if(temporaryBundleLocationFolder.exists() && !temporaryBundleLocationFolder.isDirectory()){
			this.temporaryBundleLocationFolder = makeTempFolder();
		} else if(temporaryBundleLocationFolder.exists()){
			cleanOutDirectory();
		}
		
		
		Bundle bundle = FrameworkUtil.getBundle(getClass());
		for(Bundle findit:bundle.getBundleContext().getBundles()){
			wiring = findit.adapt(FrameworkWiring.class);
			if(wiring != null){
				break;
			}
		}
		
		Thread th = new Thread(new Runnable() {
			
			@Override
			public void run() {
				int lastSize = 0;
				while(true){
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					}
					if(lastSize == bundlesToCheck.size()){
						if(lastSize != 0){
							updateBundles();
							installLatestBundles();
						}
					} else {
						lastSize = bundlesToCheck.size();
					}
					
				}
			}
		});
		th.setName("OTE BundleLoad Check");
		th.setDaemon(true);
		th.start();
	}
	
	/**
	 * should be a flat list of folders with the symbolic name of the bundle and then a version for each jar underneath each folder.
	 */
	private void cleanOutDirectory() {
		File[] symbolicNameFolders = this.temporaryBundleLocationFolder.listFiles();
		for(File folder:symbolicNameFolders){
			if(folder.isDirectory()){
				for(File file :folder.listFiles()){
					file.delete();
				}
				folder.delete();
			}
		}
	}

	private File makeTempFolder(){
		File folder = new File(System.getProperty("java.io.tmpdir"));
		File oteFolder = new File(folder, "otebundleload");
		if(!oteFolder.exists()){
			oteFolder.mkdirs();
		}
		return oteFolder;
	}
	
	File getFolder(){
		return temporaryBundleLocationFolder;
	}
	
	public synchronized void uninstallBundles(){
		for(BundleInfoLite info:managedArea.getInstalledBundles()){
			try {
				info.uninstall();
			} catch (BundleException e) {
				OseeLog.log(WorkspaceBundleLoadCoordinator.class, Level.WARNING, e);
			}
		}
		if(wiring != null){
			wiring.refreshBundles(null);
		}
	}
	
	 public static void copyFile(File source, File destination) throws IOException {
	      final FileChannel in = new FileInputStream(source).getChannel();
	      try {
	         final FileChannel out;
	         if (destination.isDirectory()) {
	            out = new FileOutputStream(new File(destination, source.getName())).getChannel();
	         } else {
	            if (destination.exists()) {
	               destination.delete(); // to work around some file permission
	            }
	            // problems
	            out = new FileOutputStream(destination).getChannel();
	         }
	         try {
	            long position = 0;
	            long size = in.size();
	            while (position < size) {
	               position += in.transferTo(position, size, out);
	            }
	         } finally {
	            Lib.close(out);
	         }
	      } finally {
	         Lib.close(in);
	      }
	   }
	
	private List<BundleInfoLite> copyDeltasToManagedFolder(List<BundleInfoLite> copies) {
		List<BundleInfoLite> lockedFiles = new ArrayList<BundleInfoLite>();
		for(BundleInfoLite info: copies){
			File folder = new File(temporaryBundleLocationFolder, info.getSymbolicName());
			folder.mkdirs();
			File newFile = new File(folder, info.getVersion() + ".jar");
			if(newFile.exists()){
				newFile.delete();
			}
			FileChannel out = null;
			FileChannel in = null;
			try {
				out = new FileOutputStream(newFile).getChannel();
				String path = info.getSystemLocation().toURI().getPath();
				in = new FileInputStream(new File(path)).getChannel();

				long position = 0;
				long size = in.size();
				while (position < size) {
					position += in.transferTo(position, size, out);
				}
				BundleInfoLite newBundle = new BundleInfoLite(newFile.toURI().toURL());
				managedArea.add(newBundle);
			} catch (IOException e) {
				OseeLog.log(WorkspaceBundleLoadCoordinator.class, Level.WARNING, e);
			} catch (URISyntaxException e) {
				OseeLog.log(WorkspaceBundleLoadCoordinator.class, Level.WARNING, e);
			} finally {
				try {
					if(in != null){
						in.close();
					}
				} catch (IOException e) {
				}
				try {
					if(out != null){
						out.close();
					}
				} catch (IOException e) {
				}
			}
		}
		return lockedFiles;
	}
	
	private List<BundleInfoLite> determineDeltasBetweenBundlesToLoad() {
		List<BundleInfoLite> bundlesToOperateOn = new ArrayList<BundleInfoLite>();
		for(String urlString:bundlesToCheck){
			try {
				URL newURL;
				try{
					newURL = new URL(urlString);
				} catch(MalformedURLException ex){
					newURL = new File(urlString).toURI().toURL();	
				}
				
				BundleInfoLite bundleInfo = new BundleInfoLite(newURL);
				List<BundleInfoLite> bundleList = managedArea.getByBundleName(bundleInfo.getSymbolicName());
				if(bundleList == null){
					bundlesToOperateOn.add(bundleInfo);
				} else {
					boolean newBundle=true;
					if(bundleList != null && bundleList.size() > 0){
						byte[] digest1 = bundleInfo.getMd5Digest();
						for(BundleInfoLite bundle:bundleList){
							byte[] digest2 = bundle.getMd5Digest();
							if (Arrays.equals(digest1, digest2)) {
								newBundle = false;
								new File(bundle.getSystemLocation().getFile()).setLastModified(System.currentTimeMillis());
							}
						}
					} 
					if(newBundle){
						bundlesToOperateOn.add(bundleInfo);
					}
				}
			} catch (MalformedURLException e) {
				OseeLog.log(WorkspaceBundleLoadCoordinator.class, Level.WARNING, e);
			} catch (IOException e) {
				OseeLog.log(WorkspaceBundleLoadCoordinator.class, Level.WARNING, e);
			}
		}
		bundlesToCheck.clear();
		return bundlesToOperateOn;
	}

	public synchronized void addBundleToCheck(String urlString){
		this.bundlesToCheck.add(urlString);
	}
	
	public synchronized void updateBundles(){
		List<BundleInfoLite> deltas = determineDeltasBetweenBundlesToLoad();
		List<BundleInfoLite> lockedFiles = copyDeltasToManagedFolder(deltas);
		for(BundleInfoLite info: lockedFiles){
			addBundleToCheck(info.getSystemLocation().toString());
			OseeLog.log(WorkspaceBundleLoadCoordinator.class, Level.WARNING, String.format("Unable to copy and load locked file: [%s]", info.getSystemLocation().toString()));
		}
	}
	
	public synchronized void installLatestBundles(){
		final List<BundleInfoLite> bundles = managedArea.getLatestBundles();
		Collection<Bundle> bundlesToRefresh = new ArrayList<Bundle>();
		Bundle systemBundle = null;
		for(BundleInfoLite info:bundles){
			if(!info.isInstalled()){
				try {
					List<BundleInfoLite> uninstallList = managedArea.getByBundleName(info.getSymbolicName());
					if(uninstallList.size() > 1){
						for(BundleInfoLite toUninstall:uninstallList){
							if(toUninstall.isInstalled()){
								Bundle bundle = toUninstall.uninstall();
								bundlesToRefresh.add(bundle);
							}
						}
					}

				} catch (BundleException e) {
					OseeLog.log(WorkspaceBundleLoadCoordinator.class, Level.WARNING, e);
				} 
			}
		}
		if(wiring != null && bundlesToRefresh.size() > 0){
			wiring.refreshBundles(bundlesToRefresh, new FrameworkListener(){
				@Override
				public void frameworkEvent(FrameworkEvent event) {
					if(FrameworkEvent.PACKAGES_REFRESHED == event.getType()){
						startBundles(bundles);
					}
				}
			});
		} else {
			startBundles(bundles);
		}
		
	}
	
	private void startBundles(Collection<BundleInfoLite> bundles){
		BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
		for(BundleInfoLite info:bundles){
			if(!info.isInstalled()){
				try {
					info.install(context);					
				} catch (BundleException e) {
				} catch (IOException e) {
				} 
			}
		}
		for(BundleInfoLite info:bundles){
			if(!info.isStarted()){
				try {
					info.start(context);					
				} catch (BundleException e) {
				} 
			}
		}
	}
}
