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
package org.eclipse.osee.ote.message.interfaces;

import org.eclipse.osee.ote.core.TestException;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;

/**
 * @author Ken J. Aguilar
 */
public interface IMessageRequestor<T extends MessageData, U extends Message<? extends ITestEnvironmentMessageSystemAccessor, T, U>> {
   <CLASSTYPE extends U> CLASSTYPE getMessageReader(Class<CLASSTYPE> type) throws TestException;

   <CLASSTYPE extends U> CLASSTYPE getMessageWriter(Class<CLASSTYPE> type) throws TestException;

   void remove(U message) throws TestException;

   void dispose();
}
