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
package org.eclipse.osee.ote.ui;

import java.rmi.RemoteException;
import java.util.logging.Level;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.jdk.core.util.IConsoleInputListener;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.IRemoteCommandConsole;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.service.ITestConnectionListener;

public class OteRemoteConsole implements IConsoleInputListener, ITestConnectionListener {

   private IRemoteCommandConsole remoteConsole;
   private ITestEnvironment env;

   public OteRemoteConsole() throws RemoteException {

   }

   @Override
   public void lineRead(String line) {
      try {
         String result = remoteConsole.doCommand(line);
         TestCoreGuiPlugin.getDefault().getConsole().write(result);
      } catch (RemoteException e) {
         TestCoreGuiPlugin.log(Level.SEVERE, "exception executing command "
		    + line, e);
	    TestCoreGuiPlugin.getDefault().getConsole().writeError(
		    "Exception during executing of command. See Error Log");
      }
   }

   public void close() throws RemoteException {
      if (env != null) {
         try {
			env.closeCommandConsole(remoteConsole);
		} catch (Exception e) {
	         OseeLog.log(TestCoreGuiPlugin.class, Level.INFO,
			    "failed to close remote terminal", e);
		}
      }
      if (TestCoreGuiPlugin.getDefault() != null
		&& TestCoreGuiPlugin.getDefault().getConsole() != null) {
	    TestCoreGuiPlugin.getDefault().getConsole().removeInputListener(
		    this);
      } else {
         OseeLog.log(TestCoreGuiPlugin.class, Level.INFO,
		    "OteRemoteConsole is null");
      }
      env = null;
   }

   @Override
   public void onConnectionLost(IServiceConnector connector, IHostTestEnvironment testHost) {
      if (TestCoreGuiPlugin.getDefault() != null
		&& TestCoreGuiPlugin.getDefault().getConsole() != null) {
	    TestCoreGuiPlugin.getDefault().getConsole().removeInputListener(
		    this);
      } else {
         OseeLog.log(TestCoreGuiPlugin.class, Level.INFO,
		    "OteRemoteConsole is null");
      }
      env = null;
   }

   @Override
   public void onPostConnect(ConnectionEvent event) {
      try {
         env = event.getEnvironment();
         remoteConsole = env.getCommandConsole();
         TestCoreGuiPlugin.getDefault().getConsole().addInputListener(this);
      } catch (RemoteException e) {
	  TestCoreGuiPlugin.log(Level.SEVERE,
		    "exception acquiring remote console", e);
      }
   }

   @Override
   public void onPreDisconnect(ConnectionEvent event) {
      try {
         close();
      } catch (RemoteException e) {
	  TestCoreGuiPlugin.log(Level.SEVERE,
		    "exception while closing remote console", e);
      }
   }

}
