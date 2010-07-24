/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.plugin.core.util;

import java.util.HashMap;
import org.eclipse.osee.framework.plugin.core.internal.PluginCoreActivator;
import org.osgi.framework.Bundle;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;

/**
 * @author Ken J. Aguilar
 */
public class ExportClassLoader extends ClassLoader {

   private static ExportClassLoader exportClassloaderInstance;
   private final PackageAdmin packageAdmin;
   private final HashMap<String, Bundle> cache = new HashMap<String, Bundle>(1024);

   public static ExportClassLoader getInstance(){
      if(exportClassloaderInstance == null){
         exportClassloaderInstance = new ExportClassLoader();
      }
      return exportClassloaderInstance;
   }

   public ExportClassLoader(PackageAdmin packageAdmin) {
      super(ExportClassLoader.class.getClassLoader());
      this.packageAdmin = packageAdmin;
   }

   public ExportClassLoader()
   {
      this(PluginCoreActivator.getInstance().getPackageAdmin());
   }

   @Override
   protected Class<?> findClass(String name) throws ClassNotFoundException {
      try {
         Bundle bundle = getExportingBundle(name);
         if (bundle != null) {
            return bundle.loadClass(name);
         }
         throw new ClassNotFoundException("could not locate a class for " + name);
      } catch (Exception e) {
         throw new ClassNotFoundException("could not locate a class for " + name, e);
      }
   }
   
/* this helps camel find annotated classes   

@Override
protected Enumeration<URL> findResources(String name) throws IOException {
//	packageAdmin.
	Vector<URL> found = new Vector<URL>();
	String packageName = name.replace('/', '.');
	int index = packageName.lastIndexOf('.');
	if(packageName.length()-1 == index){
		packageName = packageName.substring(0, index);
	}
	ExportedPackage packages = packageAdmin.getExportedPackage(packageName);
	if(packages != null){
//		Enumeration<URL> en = packages.getExportingBundle().getEntryPaths(name);
		Bundle bundle = packages.getExportingBundle();
		URL url = bundle.getEntry(name);
		if(url != null){
		   URL resolved = FileLocator.resolve(url);
		   found.add(resolved);
		   System.out.println(resolved);
		}
		if(url == null){
		   
		}
		if(url == null){
   		url = bundle.getEntry("/libs/");
   		File file = new File(url.getFile());
   		URL newurl = FileLocator.resolve(url);
   		file = new File(newurl.getFile());
   		if(file.isDirectory()){
   			for(File jar:file.listFiles()){
   				if(jar.getName().endsWith("jar")){
   					ZipFile zipFile = new ZipFile(jar, ZipFile.OPEN_READ);
   					ZipEntry entry = zipFile.getEntry(name);
   					if(entry != null){
   						InputStream stream = zipFile.getInputStream(entry);
   						String anotherUrl;
   						try {
   							anotherUrl = newurl.toURI().toASCIIString() + jar.getName() + "!/" + entry;
   							URL goodurl = new URL(anotherUrl);
   							found.add(goodurl);
   							System.out.println(goodurl);
   						} catch (URISyntaxException ex) {
   							ex.printStackTrace();
   						}
   						
   						
   					}
   				}
   			}
   		}
		}
//		FileLocator.findEntries(bundle, path);
//		bundle.findEntries(name, filePattern, recurse)
		return found.elements();//super.findResources(name);
	} else {
		return super.findResources(name);
	}
}
*/
public Bundle getExportingBundle(String name) {
      final String pkg = name.substring(0, name.lastIndexOf('.'));
      Bundle cachedBundle = cache.get(pkg);
      if (cachedBundle != null && cachedBundle.getState() != Bundle.UNINSTALLED) {
         return cachedBundle;
      }
      ExportedPackage[] list = packageAdmin.getExportedPackages(pkg);
      if (list != null) {
         for (ExportedPackage ep : list) {
            final Bundle bundle = ep.getExportingBundle();
            final int state = bundle.getState();
            if (state == Bundle.RESOLVED || state == Bundle.STARTING
                  || state == Bundle.ACTIVE || state == Bundle.STOPPING) {
               cache.put(pkg, bundle);
               return bundle;
            }
         }
      }
      return null;
   }
   
   

}
