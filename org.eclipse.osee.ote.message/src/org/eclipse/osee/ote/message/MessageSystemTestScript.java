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
package org.eclipse.osee.ote.message;

import java.lang.ref.WeakReference;
import org.eclipse.osee.ote.core.IUserSession;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.enums.ScriptTypeEnum;
import org.eclipse.osee.ote.core.environment.EnvironmentTask;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer;
import org.eclipse.osee.ote.core.environment.interfaces.IExecutionUnitManagement;
import org.eclipse.osee.ote.core.environment.interfaces.IScriptControl;
import org.eclipse.osee.ote.core.environment.interfaces.ITestLogger;
import org.eclipse.osee.ote.core.environment.interfaces.ITestStation;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;
import org.eclipse.osee.ote.core.environment.interfaces.ITimerControl;
import org.eclipse.osee.ote.message.enums.MemType;
import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;
import org.eclipse.osee.ote.message.interfaces.ITestEnvironmentMessageSystemAccessor;

/**
 * @author Andrew M. Finkbeiner
 */
public abstract class MessageSystemTestScript extends TestScript implements ITestAccessor {

   private WeakReference<ITestEnvironmentMessageSystemAccessor> msgSysEnvironment;

   public MessageSystemTestScript(TestEnvironment environment, IUserSession callback, ScriptTypeEnum scriptType, boolean isBatchable) {
      super(environment, callback, scriptType, isBatchable);
      msgSysEnvironment =
            new WeakReference<ITestEnvironmentMessageSystemAccessor>(
                  (ITestEnvironmentMessageSystemAccessor) environment);

   }

   public IMessageManager<?, ?> getMsgManager() {
      return msgSysEnvironment.get().getMsgManager();
   }

   public boolean isPhysicalTypeAvailable(MemType mux) {
      return msgSysEnvironment.get().isPhysicalTypeAvailable(mux);
   }

   public void associateObject(Class<?> c, Object obj) {
      msgSysEnvironment.get().associateObject(c, obj);
   }

   public Object getAssociatedObject(Class<?> c) {
      return msgSysEnvironment.get().getAssociatedObject(c);
   }

   public Object getAssociatedObject() {
      return msgSysEnvironment.get().getAssociatedObjects();
   }

   public IExecutionUnitManagement getExecutionUnitManagement() {
      return msgSysEnvironment.get().getExecutionUnitManagement();
   }

   public ITestStation getTestStation() {
      return msgSysEnvironment.get().getTestStation();
   }

   public ITestLogger getLogger() {
      return msgSysEnvironment.get().getLogger();
   }

   public ITimerControl getTimerCtrl() {
      return msgSysEnvironment.get().getTimerCtrl();
   }

   public IScriptControl getScriptCtrl() {
      return msgSysEnvironment.get().getScriptCtrl();
   }

   public ICancelTimer setTimerFor(ITimeout listener, int time) {
      return msgSysEnvironment.get().setTimerFor(listener, time);
   }

   public void onScriptSetup() {
      msgSysEnvironment.get().onScriptSetup();
   }

   public void onScriptComplete() throws InterruptedException {
      msgSysEnvironment.get().onScriptComplete();
   }

   public long getEnvTime() {
      return msgSysEnvironment.get().getEnvTime();
   }

//   public ITestPointTally getAttachedTestPointTally(TestScript script) {
//      return msgSysEnvironment.get().getAttachedTestPointTally(script);
//   }

   //   public EnvironmentType getEnvironmentType() {
   //      return msgSysEnvironment.getEnvironmentType();
   //   }
   public void abortTestScript() {
      msgSysEnvironment.get().abortTestScript();
   }

   public boolean addTask(EnvironmentTask task) {
      return msgSysEnvironment.get().addTask(task);
   }
}
