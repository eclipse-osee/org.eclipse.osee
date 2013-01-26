package org.eclipse.osee.ote.server;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

public class OteClose {

	public void start(){
		BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
		Dictionary<String, Object> dict = new Hashtable<String, Object>();
		dict.put("osgi.command.scope", "ote");
		dict.put("osgi.command.function", new String[]{"x"});
		context.registerService(OteClose.class, this, dict);
	}
	
	
	public void x() throws Exception {
		BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
		Bundle systemBundle = context.getBundle(0);
		systemBundle.stop();
		boolean canExit = false;
		while(!canExit){
			try{
				Thread.sleep(20);
			} catch (Throwable th){
			}
			canExit = true;
			try{
				for(Bundle b:context.getBundles()){
					if(b.getState() != Bundle.ACTIVE){
						canExit = false;
					}
				}
			} catch (Throwable th){
				canExit = true;
			}
		}
		System.exit(0);
	}
	
}
