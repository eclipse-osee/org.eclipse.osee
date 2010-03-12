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

import org.eclipse.osee.ote.core.TestCase;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.message.enums.MemType;
import org.eclipse.osee.ote.message.interfaces.IMessageManager;
import org.eclipse.osee.ote.message.interfaces.ITestAccessor;
import org.eclipse.osee.ote.message.interfaces.ITestEnvironmentMessageSystemAccessor;

/**
 * @author Andy Jury
 */
public abstract class MessageSystemTestCase extends TestCase implements ITestAccessor {
   
   private WeakReference<ITestEnvironmentMessageSystemAccessor> msgSysTestEnvironment;
   
   protected MessageSystemTestCase(TestScript testScript, boolean standAlone, boolean addToRunList) {
      super(testScript, standAlone, addToRunList);
      msgSysTestEnvironment = new WeakReference<ITestEnvironmentMessageSystemAccessor>((MessageSystemTestScript)testScript);
   }
   /**
    * TestCase Constructor.
    * 
    * @param testScript
    * @param standAlone
    */
   public MessageSystemTestCase(TestScript testScript, boolean standAlone) {
      this(testScript, standAlone, true);
   }

   /**
    * TestCase Constructor.
    * 
    * @param testScript
    */
   public MessageSystemTestCase(TestScript testScript) {
      this(testScript, false);
   }
   
   public IMessageManager<?,?> getMsgManager() {
      return msgSysTestEnvironment.get().getMsgManager();
   }
   public boolean isPhysicalTypeAvailable(MemType mux) {
      return msgSysTestEnvironment.get().isPhysicalTypeAvailable(mux);
   }
   public void associateObject(Class<?> c, Object obj) {
      msgSysTestEnvironment.get().associateObject(c, obj);
   }
}
