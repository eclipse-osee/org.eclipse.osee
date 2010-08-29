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
package org.eclipse.osee.ote.service;

import java.io.IOException;
import org.eclipse.osee.ote.message.Message;

/**
 * Allows the lookup of message definitions
 * 
 * @author Ken J. Aguilar
 */
public interface IMessageDictionary {

   String getMessageLibraryVersion() throws IOException;

   /**
    * Generates a listing of all messages and associated elements defined in the message jar. The intent is to provide a
    * method for searching the message jar for certain messages
    * 
    * @throws IOException
    */
   void generateMessageIndex(final MessageSink sink) throws Exception;

   /**
    * Finds the class definition of a message of the given name
    * 
    * @return Returns values reference.
    * @throws ClassNotFoundException if no class definition was found with the given name
    */
   Class<? extends Message> lookupMessage(String messageName) throws ClassNotFoundException;

   void dispose();

   String getImplementationVersion(String file) throws Exception;
}
