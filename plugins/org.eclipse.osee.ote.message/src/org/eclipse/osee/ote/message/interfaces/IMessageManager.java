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

import java.util.Collection;
import org.eclipse.osee.ote.core.TestException;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.MemType;

/**
 * @author Andrew M. Finkbeiner
 */
public interface IMessageManager<T extends MessageData, U extends Message<? extends ITestEnvironmentMessageSystemAccessor, T, U>> {
   void destroy();

   <CLASSTYPE extends U> CLASSTYPE createMessage(Class<CLASSTYPE> messageClass) throws TestException;

   <CLASSTYPE extends U> int getReferenceCount(CLASSTYPE classtype);

   <CLASSTYPE extends U> CLASSTYPE findInstance(Class<CLASSTYPE> clazz, boolean writer);

   Collection<U> getAllMessages();

   Collection<U> getAllReaders();

   Collection<U> getAllWriters();

   Collection<U> getAllReaders(MemType type);

   Collection<U> getAllWriters(MemType type);

   void init() throws Exception;

   void publishMessages(boolean publish);

   boolean isPhysicalTypeAvailable(MemType physicalType);

   IMessageRequestor<T, U> createMessageRequestor(String name);
}
