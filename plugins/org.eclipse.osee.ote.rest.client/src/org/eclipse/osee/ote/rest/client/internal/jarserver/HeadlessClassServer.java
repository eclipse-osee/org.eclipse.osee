package org.eclipse.osee.ote.rest.client.internal.jarserver;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.osee.framework.plugin.core.server.ClassServer;
import org.eclipse.osee.ote.core.BundleInfo;



public class HeadlessClassServer {

	private ClassServer classServer;
	private List<BundleInfo> jars;
	
	public HeadlessClassServer(int port, InetAddress address, List<File> bundleFolders) throws IOException{
		Thread.currentThread().setName("BundleClassServer");
		classServer = new ClassServer(port, address);
		jars = new ArrayList<BundleInfo>();
		for(File dir:bundleFolders){
			if(dir.exists() && dir.isDirectory()){
				findJarFiles(dir, jars);
			} else if(dir.exists()){//it may be a jar
			   BundleInfo info = getBundleInfo(dir);
            if(info != null){
               jars.add(info);
            }
			}
		}
		BundleResourceFinder bundleResourceFinder = new BundleResourceFinder(jars); 
		classServer.addResourceFinder(bundleResourceFinder);
		classServer.start();
	
	}
	
	public void stop(){
		classServer.terminate();
	}
	
	public List<BundleInfo> getBundles(){
		return jars;
	}

	private void findJarFiles(File dir, final List<BundleInfo> jars) {
		dir.listFiles(new FileFilter(){
			@Override
			public boolean accept(File pathname) {
				if(pathname.isDirectory()){
					findJarFiles(pathname, jars);
				} else {
				   BundleInfo info = getBundleInfo(pathname);
				   if(info != null){
				      jars.add(info);
				   }
				}
				return false;
			}
		});
	}
	
	private BundleInfo getBundleInfo(File file){
	   BundleInfo bundleInfo = null;
      try {
         if(file.getAbsolutePath().endsWith(".jar")){
            bundleInfo = new BundleInfo(file.toURI().toURL(), getHostName().toExternalForm(), true);
            if(bundleInfo.getSymbolicName() != null && !bundleInfo.getSymbolicName().equalsIgnoreCase("unknown") && bundleInfo.getVersion() != null){
               if(bundleInfo.getManifest() == null){
                  bundleInfo = null;
               } 
            }  
         }
      } catch (MalformedURLException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      }
      return bundleInfo;
	}

	public URL getHostName(){
		return classServer.getHostName();
	}
}
