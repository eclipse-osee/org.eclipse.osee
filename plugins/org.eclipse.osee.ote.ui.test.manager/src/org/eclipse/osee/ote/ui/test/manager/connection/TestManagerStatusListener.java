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
package org.eclipse.osee.ote.ui.test.manager.connection;

import java.io.IOException;
import java.util.Hashtable;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.status.IServiceStatusData;
import org.eclipse.osee.ote.core.environment.status.msg.CommandAddedMessage;
import org.eclipse.osee.ote.core.environment.status.msg.CommandRemovedMessage;
import org.eclipse.osee.ote.core.environment.status.msg.EnvErrorMessage;
import org.eclipse.osee.ote.core.environment.status.msg.SequentialCommandBeganMessage;
import org.eclipse.osee.ote.core.environment.status.msg.SequentialCommandEndedMessage;
import org.eclipse.osee.ote.core.environment.status.msg.TestCompleteMessage;
import org.eclipse.osee.ote.core.environment.status.msg.TestPointUpdateMessage;
import org.eclipse.osee.ote.core.environment.status.msg.TestServerCommandCompleteMessage;
import org.eclipse.osee.ote.core.environment.status.msg.TestStartMessage;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * @author Andrew M. Finkbeiner
 */
public final class TestManagerStatusListener implements EventHandler {

   private final TestManagerServiceStatusDataVisitor testManagerServiceDataVisitor;
   private ServiceRegistration<?> eventReference;
   

   public TestManagerStatusListener(TestManagerEditor testManagerEditor, ScriptManager userEnvironment) {
      this.testManagerServiceDataVisitor = new TestManagerServiceStatusDataVisitor(userEnvironment, testManagerEditor);
      Hashtable<String, Object> properties = new Hashtable<>();
      properties.put("event.topics", "ote/status/*");
      eventReference = FrameworkUtil.getBundle(getClass()).getBundleContext().registerService(EventHandler.class.getName(), this, properties);
   }

   @Override
   public void handleEvent(Event event) {
	   try {
		   IServiceStatusData statusData = null;
		   if(event.getTopic().equals(CommandAddedMessage.EVENT)){
			   CommandAddedMessage message = new CommandAddedMessage(OteEventMessageUtil.getBytes(event));	
			   statusData = message.getObject();
		   } else if(event.getTopic().equals(CommandRemovedMessage.EVENT)){
			   CommandRemovedMessage message = new CommandRemovedMessage(OteEventMessageUtil.getBytes(event));	
			   statusData = message.getObject();
		   } else if(event.getTopic().equals(EnvErrorMessage.EVENT)){
			   EnvErrorMessage message	= new EnvErrorMessage(OteEventMessageUtil.getBytes(event));	
			   statusData = message.getObject();
		   } else if(event.getTopic().equals(SequentialCommandBeganMessage.EVENT)){
			   SequentialCommandBeganMessage message = new SequentialCommandBeganMessage(OteEventMessageUtil.getBytes(event));	
			   statusData = message.getObject();
		   } else if(event.getTopic().equals(SequentialCommandEndedMessage.EVENT)){
			   SequentialCommandEndedMessage message = new SequentialCommandEndedMessage(OteEventMessageUtil.getBytes(event));	
			   statusData = message.getObject();
		   } else if(event.getTopic().equals(TestCompleteMessage.EVENT)){
			   TestCompleteMessage message = new TestCompleteMessage(OteEventMessageUtil.getBytes(event));	
			   statusData = message.getObject();
		   } else if(event.getTopic().equals(TestPointUpdateMessage.EVENT)){
			   TestPointUpdateMessage message = new TestPointUpdateMessage(OteEventMessageUtil.getBytes(event));	
			   statusData = message.getObject();
		   } else if(event.getTopic().equals(TestServerCommandCompleteMessage.EVENT)){
			   TestServerCommandCompleteMessage message	= new TestServerCommandCompleteMessage(OteEventMessageUtil.getBytes(event));	
			   statusData = message.getObject();
		   } else if(event.getTopic().equals(TestStartMessage.EVENT)){
			   TestStartMessage message	= new TestStartMessage(OteEventMessageUtil.getBytes(event));	
			   statusData = message.getObject();
		   }
		   if(statusData != null){
			   statusData.accept(testManagerServiceDataVisitor);
		   }
	   } catch (IOException e) {
		   OseeLog.log(getClass(), Level.SEVERE, event.getTopic(), e);
	   } catch (ClassNotFoundException e) {
		   OseeLog.log(getClass(), Level.SEVERE, event.getTopic(), e);
	   }

   }

   public void unregisterEventListener() {
	   eventReference.unregister();
   }
}
