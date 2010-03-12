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
package org.eclipse.osee.ote.core.framework.adapter;

import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.framework.IMethodResult;
import org.eclipse.osee.ote.core.framework.ITestLifecycleListener;
import org.eclipse.osee.ote.core.framework.MethodResultImpl;
import org.eclipse.osee.ote.core.framework.ReturnCode;
import org.eclipse.osee.ote.core.framework.event.IEventData;

/**
 * @author Roberto E. Escobar
 */
public class TestLifecycleListenerAdapter implements ITestLifecycleListener {

   public IMethodResult postDispose(IEventData eventData, TestEnvironment env)   {
	   return new MethodResultImpl(ReturnCode.OK);
   }

   public IMethodResult postInstantiation(IEventData eventData, TestEnvironment env)   {
	   return new MethodResultImpl(ReturnCode.OK);
   }

   public IMethodResult preDispose(IEventData eventData, TestEnvironment env)   {
	   return new MethodResultImpl(ReturnCode.OK);
   }

   public IMethodResult preInstantiation(IEventData eventData, TestEnvironment env)  {
	   return new MethodResultImpl(ReturnCode.OK);
   }
}
