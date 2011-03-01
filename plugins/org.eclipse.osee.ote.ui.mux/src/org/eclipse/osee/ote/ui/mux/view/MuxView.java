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
package org.eclipse.osee.ote.ui.mux.view;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.jdk.core.util.network.PortUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.PeriodicDisplayTask;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;
import org.eclipse.osee.ote.message.IInstrumentationRegistrationListener;
import org.eclipse.osee.ote.message.instrumentation.IOInstrumentation;
import org.eclipse.osee.ote.message.interfaces.ITestEnvironmentMessageSystem;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.service.ITestConnectionListener;
import org.eclipse.osee.ote.ui.mux.MuxToolPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view shows data obtained from the model. The
 * sample creates a dummy model on the fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be presented in the view. Each view can present the
 * same model objects using different labels and icons, if needed. Alternatively, a single label provider can be shared
 * between views in order to ensure that objects of the same type are presented in the same way everywhere.
 * <p>
 */

public class MuxView extends ViewPart implements ITestConnectionListener, IInstrumentationRegistrationListener, Remote {
   private IOInstrumentation muxProbe;
   private PeriodicDisplayTask task;
   private ListenerThread thread;
   private int port;
   private final Map<Integer, MuxChannelComposite> channelComposites;

   private IInstrumentationRegistrationListener exportedThis;
   private TabFolder tabFolder;

   public static final String VIEW_ID = "osee.test.muxTool.views.MuxView";
   class NameSorter extends ViewerSorter {
   }

   /**
    * The constructor.
    */
   public MuxView() {
      super();
      channelComposites = new TreeMap<Integer, MuxChannelComposite>();
   }

   /**
    * This is a callback that will allow us to create the viewers and initialize them.
    */
   @Override
   public void createPartControl(Composite parent) {
      tabFolder = new TabFolder(parent, SWT.WRAP);
      addChannelToView(1);
      try {
         thread = new ListenerThread();
      } catch (Exception e) {
         OseeLog.log(MuxView.class, Level.SEVERE, "Mux View could not start listening thread", e);
         MessageDialog.openError(parent.getShell(), "Error", "Mux View could not initialize. See Error Log for details");
         return;
      }
      thread.start();

      task = new PeriodicDisplayTask(Display.getDefault(), 333) {
         @Override
         protected void update() {
            try {
            	for(MuxChannelComposite mux:channelComposites.values()){
            		mux.refresh();
            	}
            } catch (Throwable t) {
               OseeLog.log(MuxToolPlugin.class, Level.SEVERE, "problems refreshing viewer", t);
               stop();
            }
         }
      };
      task.start();

      // TODO: Change to use OteHelpContext
      HelpUtil.setHelp(parent, "mux_view", "org.eclipse.osee.ote.help.ui");
      MuxToolPlugin.getDefault().getOteClientService().addConnectionListener(this);
   }

   
   private MuxChannelComposite addChannelToView(int channel){
	   if(!channelComposites.containsKey(channel)){
		   Composite chanTabComp = new Composite(tabFolder, SWT.NONE);
		   MuxChannelComposite muxChannelComposite = new MuxChannelComposite(chanTabComp, SWT.NONE, channel);
		   GridLayout chanLayout = new GridLayout(1, false);
		   chanTabComp.setLayout(chanLayout);
		   channelComposites.put(channel, muxChannelComposite);
		   
		   int index = 0;
		   for(MuxChannelComposite muxChannel : channelComposites.values()){
			   if(muxChannelComposite == muxChannel){
				   break;
			   }
			   index++;
		   }
		   TabItem chanTab = new TabItem(tabFolder, SWT.NONE, index);
		   chanTab.setText("Channel "+ channel);
		   chanTab.setControl(chanTabComp);
		   return muxChannelComposite;
	   } else {
		   return null;
	   }
   }
   
   /**
    * Passing the focus request to the viewer's control.
    */
   @Override
   public void setFocus() {
      // msgViewer1.getControl().setFocus();
   }

   @Override
   public void dispose() {
      MuxToolPlugin.getDefault().getOteClientService().removeConnectionListener(this);
      ITestEnvironment env = MuxToolPlugin.getDefault().getOteClientService().getConnectedEnvironment();
      if (env != null) {
         try {
            ((ITestEnvironmentMessageSystem) env).removeInstrumentationRegistrationListener(exportedThis);
         } catch (RemoteException ex) {
            OseeLog.log(MuxView.class, Level.WARNING, "could not deregister instrumentation registration listener", ex);
         }
         IServiceConnector connector = MuxToolPlugin.getDefault().getOteClientService().getConnector();
         try {
            connector.unexport(this);
         } catch (Exception ex) {
            OseeLog.log(MuxView.class, Level.WARNING, "could not unexport this", ex);
         }
      }
      if (muxProbe != null) {
         try {
            muxProbe.unregister(thread.address);
         } catch (RemoteException ex) {
            OseeLog.log(MuxView.class, Level.WARNING, "could not disconnect from mux probe", ex);
         }
         muxProbe = null;
      }
      if (task != null) {
         task.stop();
      }
      thread.shutdown();

      super.dispose();
   }

   class ListenerThread extends Thread {
      private volatile boolean done = false;
      private final DatagramChannel channel;
      private final InetSocketAddress address;

      public ListenerThread() throws IOException {
         super("Mux View Listener Thread");
         channel = DatagramChannel.open();
         port = PortUtil.getInstance().getValidPort();
         address = new InetSocketAddress(InetAddress.getLocalHost(), port);
         channel.socket().bind(address);
         OseeLog.log(MuxToolPlugin.class, Level.INFO,
            "MuxView connection - host: " + address.getHostName() + "    port: " + address.getPort());
      }

      @Override
      public void run() {
         final ByteBuffer buffer = ByteBuffer.wrap(new byte[256]);
         try {
            while (!done) {
               buffer.clear();
               channel.receive(buffer);
               buffer.flip();
               final int channel = buffer.array()[0];
               MuxChannelComposite composite = channelComposites.get(channel);
               if(composite != null){
            	   composite.onDataAvailable(buffer);
               } else {
            	   PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable(){

					@Override
					public void run() {
						 MuxChannelComposite muxChannelComposite = addChannelToView(channel);
		            	   muxChannelComposite.onDataAvailable(buffer);
					}
            	   });
               }
            }
         } catch (InterruptedIOException e) {
            Thread.currentThread().interrupt();
         } catch (IOException e) {
            if (!isInterrupted()) {
               OseeLog.log(MuxToolPlugin.class, Level.SEVERE, "Interrupted", e);
            }
         } finally {
            try {
               channel.close();
            } catch (IOException e) {
               // do nothing
            }
         }
      }

      public void shutdown() {
         done = true;
         interrupt();
         try {
            thread.join(5000);
            assert !channel.isOpen();
         } catch (InterruptedException e) {
            OseeLog.log(MuxView.class, Level.SEVERE, "could not join wiht listener thread", e);
         }
      }
   }

   @Override
   public void onConnectionLost(IServiceConnector connector) {
      handleConnectionLostStatus();
   }

   @Override
   public void onPostConnect(final ConnectionEvent event) {
      final ITestEnvironmentMessageSystem environment = (ITestEnvironmentMessageSystem) event.getEnvironment();
      if (environment != null) {
         // we are connected
         try {
            exportedThis = (IInstrumentationRegistrationListener) event.getConnector().findExport(MuxView.this);
            if (exportedThis == null) {
               exportedThis = (IInstrumentationRegistrationListener) event.getConnector().export(MuxView.this);
            }
            environment.addInstrumentationRegistrationListener(exportedThis);
         } catch (Exception ex) {
            OseeLog.log(MuxView.class, Level.SEVERE, "could not register for instrumentation events", ex);
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  MessageDialog.openError(Displays.getActiveShell(), "Connect Error",
                     "Could not register for instrumentation events. See Error Log for details");
               }

            });

         }
      }

   }

   private void detach() {

   }

   @Override
   public void onPreDisconnect(ConnectionEvent event) {
      final ITestEnvironmentMessageSystem environment = (ITestEnvironmentMessageSystem) event.getEnvironment();
      try {
         environment.removeInstrumentationRegistrationListener(exportedThis);
      } catch (RemoteException ex1) {
         OseeLog.log(MuxToolPlugin.class, Level.SEVERE, "Problem unregistering instrumentation registration listener",
            ex1);
      }

      if (muxProbe != null) {
         try {
            muxProbe.unregister(thread.address);
         } catch (RemoteException ex) {
            OseeLog.log(MuxToolPlugin.class, Level.SEVERE, "Problem unregistering socket address", ex);
         } finally {
            muxProbe = null;
         }
      }
      handleConnectionLostStatus();
   }

   private void handleConnectionLostStatus() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
        	 for(MuxChannelComposite mux:channelComposites.values()){
         		mux.updateColors(false);
         		mux.setMuxProbe(null);
         	}
            // we are not connected
            if (task != null) {
               task.stop();
            }
         }
      });
   }

   @Override
   public void onDeregistered(String name) throws RemoteException {
      if (muxProbe != null && name.equals("MUXIO")) {
         muxProbe = null;
         handleConnectionLostStatus();
      }
   }

   @Override
   public void onRegistered(String name, IOInstrumentation instrumentation) throws RemoteException {
      try {
         if (muxProbe == null && name.equals("MUXIO")) {
            muxProbe = instrumentation;
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  if (task != null) {
                     task.start();
                  }
                  for(MuxChannelComposite mux:channelComposites.values()){
               		mux.updateColors(false);
               		mux.setMuxProbe(muxProbe);
               	}
               }

            });
            muxProbe.register(thread.address);

         }
      } catch (RemoteException ex) {
         OseeLog.log(MuxToolPlugin.class, Level.SEVERE,
            "Problem registering socket address with remote instrumentation service", ex);
      }

   }
}
