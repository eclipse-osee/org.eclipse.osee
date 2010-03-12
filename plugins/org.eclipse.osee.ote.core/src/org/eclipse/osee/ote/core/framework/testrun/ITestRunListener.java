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

import org.eclipse.osee.ote.core.framework.IMethodResult;
import org.eclipse.osee.ote.core.framework.event.IEventData;

public interface ITestRunListener {

   IMethodResult preRun(IEventData eventData);

   IMethodResult preTestCase(IEventData eventData);

   IMethodResult postTestCase(IEventData eventData);

   IMethodResult postRun(IEventData eventData);
}