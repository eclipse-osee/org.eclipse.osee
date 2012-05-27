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

/**
 * @author Ryan D. Brooks
 */
public interface MessageSink {

   /**
    * called once by each provider before it calls absorbMessage or absorbElement
    * @param providerName The name of the provider
    */
   public void absorbProvider(String providerName);

   /**
    * called once for each message in the message list
    * @param messageName The name of the message
    */
   public void absorbMessage(String messageName);

   /**
    * called once for each element in a message after absorbMessage is called for that message
    * @param elementName The name of the element
    */
   public void absorbElement(String elementName);
}