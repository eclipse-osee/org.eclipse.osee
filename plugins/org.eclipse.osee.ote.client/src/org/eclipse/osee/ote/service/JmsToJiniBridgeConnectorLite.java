/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.server.ExportException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.connection.service.IServicePropertyChangeListener;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;
import org.eclipse.osee.framework.jdk.core.util.Network;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.MessageService;
import org.eclipse.osee.framework.messaging.NodeInfo;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.messaging.services.messages.ServiceDescriptionPair;
import org.eclipse.osee.framework.messaging.services.messages.ServiceHealth;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.eclipse.osee.ote.core.OteBaseMessages;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;

/**
 * @author Andrew M. Finkbeiner
 */
public class JmsToJiniBridgeConnectorLite implements IServiceConnector, OseeMessagingStatusCallback {
   public static final String TYPE = "jmstojini";
   public static final String OTE_EMBEDDED_BROKER_PROP = "OTEEmbeddedBroker";
   private static final class ExportInfo {
      private final Exporter exporter;
      private final Object exportedObject;

      private ExportInfo(Exporter exporter, Object exportedObject) {
         this.exportedObject = exportedObject;
         this.exporter = exporter;
      }
   }
   
   private MessageService messageService;
   private OteServiceRequestHandler myOteServiceRequestHandler = new OteServiceRequestHandler();
   private EnhancedProperties properties;
   private final HashMap<Object, ExportInfo> exports = new HashMap<Object, ExportInfo>();
   private final ExportClassLoader exportClassLoader;
   private final List<IServicePropertyChangeListener> propertyChangeListeners =
		   new CopyOnWriteArrayList<IServicePropertyChangeListener>();
   private final String uniqueServerId;
   private ServiceHealth serviceHealth;
   public Object service;

    Object myLock = new Object();

   public JmsToJiniBridgeConnectorLite(ServiceHealth serviceHealth, MessageService messageService) {
	   this.serviceHealth = serviceHealth;
	   this.uniqueServerId = serviceHealth.getServiceUniqueId();
	   this.exportClassLoader = ExportClassLoader.getInstance();
	   this.messageService = messageService;
   }
   
   public void setServiceHealth(ServiceHealth serviceHealth){
	   this.serviceHealth = serviceHealth;
	   for(IServicePropertyChangeListener change: propertyChangeListeners){
		   change.propertyChanged(this, "user_list", getProperty("user_list", "N/A"));
	   }
   }

@Override
   public Object export(Object callback) throws ExportException {
      try {
         Exporter exporter = createExporter();
         Object exportedObject = exporter.export((Remote) callback);
         exports.put(callback, new ExportInfo(exporter, exportedObject));
         return exportedObject;
      } catch (UnknownHostException e) {
         throw new ExportException("failed to export", e);
      }
   }

   @Override
   public void unexport(Object callback) throws Exception {
      ExportInfo info = exports.remove(callback);
      if (info != null) {
         info.exporter.unexport(false);
      }
   }

   @Override
   public Object findExport(Object callback) {
      ExportInfo info = exports.get(callback);
      if (info != null) {
         return info.exportedObject;
      }
      return null;
   }

   @Override
   public void stop() throws Exception {
      for (ExportInfo info : exports.values()) {
         info.exporter.unexport(false);
      }
      exports.clear();
   }

   private Exporter createExporter() throws UnknownHostException {
      return new BasicJeriExporter(TcpServerEndpoint.getInstance(Network.getValidIP().getHostAddress(), 0),
         new BasicILFactory(null, null, exportClassLoader), false, false);
   }

   @Override
   public String getConnectorType() {
      return TYPE;
   }

   @Override
   public Serializable getProperty(String property, Serializable defaultValue) {
	   for(ServiceDescriptionPair pair:serviceHealth.getServiceDescription()){
		   if(pair.getName().equals(property)){
			   return pair.getValue();
		   }
	   }
	   if(property.equals(OTE_EMBEDDED_BROKER_PROP)){
		   try {
			   return new NodeInfo(OTE_EMBEDDED_BROKER_PROP, new URI(serviceHealth.getBrokerURI()));
		   } catch (URISyntaxException e) {
			   OseeLog.log(Activator.class, Level.SEVERE, e);
		   }
	   }
	   return defaultValue;
   }

   @Override
   public Object getService() {
	   if(this.service == null){
		   ConnectionNode connectionNode;
		   try {
			   connectionNode = messageService.get(new NodeInfo("oteserver", new URI(serviceHealth.getBrokerURI())));
			   connectionNode.subscribeToReply(OteBaseMessages.RequestOteHost, myOteServiceRequestHandler);
			   connectionNode.send(OteBaseMessages.RequestOteHost, serviceHealth.getServiceUniqueId(), this);
			   synchronized(myLock){
				   myLock.wait(30*1000);
			   }
		   } catch (OseeCoreException e) {
			   OseeLog.log(Activator.class, Level.SEVERE, e);
		   } catch (URISyntaxException e) {
			   OseeLog.log(Activator.class, Level.SEVERE, e);
		   } catch (InterruptedException e) {
			   OseeLog.log(Activator.class, Level.SEVERE, e);
		   }
	   }
	   return service;
   }

   @Override
   public boolean ping() {
	  if(this.service == null){
		  return false;
	  } else {
		  try{
			  ((IHostTestEnvironment)this.service).getProperties();
		  } catch (Throwable th){
			  return false;
		  }
	  }
	  return true;
   }

   @Override
   public void addPropertyChangeListener(IServicePropertyChangeListener listener) {
      propertyChangeListeners.add(listener);
   }

   @Override
   public void removePropertyChangeListener(IServicePropertyChangeListener listener) {
      propertyChangeListeners.remove(listener);
   }

   @Override
   public void setProperty(String key, Serializable value) {
//      properties.setProperty(key, value);
//      for (IServicePropertyChangeListener listener : propertyChangeListeners) {
//         listener.propertyChanged(this, key, value);
//      }
	   System.out.println("no");
   }

   @Override
   public URI upload(File file) throws Exception {
      return null;
   }

   @Override
   public String getUniqueServerId() {
      return uniqueServerId;
   }

   @Override
   public EnhancedProperties getProperties() {
      return properties;
   }

   @Override
   public void init(Object service) throws UnknownHostException, ExportException {

   }
   
   class OteServiceRequestHandler extends OseeMessagingListener {
	   @Override
	   public void process(Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
		   try {
			   ByteArrayInputStream bais = new ByteArrayInputStream((byte[]) message);
			   ObjectInputStream ois = new ObjectInputStream(bais);
			   Object msg = ois.readObject();
			   msg.toString();
			   Object obj = ois.readObject();
			   IHostTestEnvironment hostEnv = (IHostTestEnvironment) obj;

			   String id = hostEnv.getProperties().getProperty("id").toString();
			   if (serviceHealth.getServiceUniqueId().equals(id)){
				   JmsToJiniBridgeConnectorLite.this.service = hostEnv;
				   synchronized(myLock){
					   myLock.notifyAll();
				   }
			   }
		   } catch (IOException ex) {
			   OseeLog.log(Activator.class, Level.SEVERE, ex);
		   } catch (ClassNotFoundException ex) {
			   OseeLog.log(Activator.class, Level.SEVERE, ex);
		   } catch (Exception ex) {
			   OseeLog.log(Activator.class, Level.SEVERE, ex);
		   }
	   }
   }

   @Override
   public void success() {
   }

   @Override
   public void fail(Throwable th) {
	   OseeLog.log(Activator.class, Level.SEVERE, th);
   }

}
