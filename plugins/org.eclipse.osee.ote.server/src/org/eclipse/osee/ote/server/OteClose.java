/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.server;

import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.Descriptor;
import org.eclipse.osee.ote.core.ServiceUtility;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

public class OteClose {

	public void start(){
		BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
		Dictionary<String, Object> dict = new Hashtable<>();
		dict.put(CommandProcessor.COMMAND_SCOPE, "ote");
		dict.put(CommandProcessor.COMMAND_FUNCTION, new String[]{"x"});
		context.registerService(OteClose.class, this, dict);
	}
	
	
	@Descriptor ("Shutdown the OTE Server")
	public void x() throws Exception {
	   OteServiceStarter service = ServiceUtility.getService(OteServiceStarter.class);
	   if(service != null){
	      service.stop();
	   }
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
