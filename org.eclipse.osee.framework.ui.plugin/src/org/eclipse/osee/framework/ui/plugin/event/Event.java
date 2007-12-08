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
package org.eclipse.osee.framework.ui.plugin.event;

public abstract class Event {

   private final Object sender;
   private final Exception exception;

   public Event(Object sender) {
      this(sender, null);
   }

   public Event(Object sender, Exception exception) {
      this.sender = sender;
      this.exception = exception;
   }

   public Object getSender() {
      return sender;
   }

   /**
    * @return Returns the exception.
    */
   public Exception getException() {
      return exception;
   }

   public void checkException() throws Exception {
      throw exception;
   }
}
