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
package org.eclipse.osee.ote.core.framework.testrun;

import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.ote.core.TestCase;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.framework.event.BaseEvent;
import org.eclipse.osee.ote.core.framework.event.IEventData;

public class BaseTestRunListenerDataProvider implements ITestRunListenerDataProvider {

   public IEventData create(IPropertyStore properties, TestScript test, TestCase testCase) {
      return new BaseEvent(properties, test, testCase);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.framework.testrun.ITestRunListenerDataProvider#createOnPostRun(org.eclipse.osee.ote.core.framework.data.IPropertyStore, org.eclipse.osee.ote.core.TestScript)
    */
   public IEventData createOnPostRun(IPropertyStore propertyStore, TestScript test) {
      return new BaseEvent(propertyStore, test);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.framework.testrun.ITestRunListenerDataProvider#createOnPostTestCase(org.eclipse.osee.ote.core.framework.data.IPropertyStore, org.eclipse.osee.ote.core.TestScript, org.eclipse.osee.ote.core.TestCase)
    */
   public IEventData createOnPostTestCase(IPropertyStore propertyStore, TestScript test, TestCase testCase) {
      return new BaseEvent(propertyStore, test, testCase);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.framework.testrun.ITestRunListenerDataProvider#createOnPreRun(org.eclipse.osee.ote.core.framework.data.IPropertyStore, org.eclipse.osee.ote.core.TestScript)
    */
   public IEventData createOnPreRun(IPropertyStore propertyStore, TestScript test) {
      return new BaseEvent(propertyStore, test);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.core.framework.testrun.ITestRunListenerDataProvider#createOnPreTestCase(org.eclipse.osee.ote.core.framework.data.IPropertyStore, org.eclipse.osee.ote.core.TestScript, org.eclipse.osee.ote.core.TestCase)
    */
   public IEventData createOnPreTestCase(IPropertyStore propertyStore, TestScript test, TestCase testCase) {
      return new BaseEvent(propertyStore, test, testCase);
   }

}
