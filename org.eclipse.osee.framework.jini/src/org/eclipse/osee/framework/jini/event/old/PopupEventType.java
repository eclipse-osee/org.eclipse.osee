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
package org.eclipse.osee.framework.jini.event.old;

/**
 * This class defines the popup event type. Any listener which desires to receive these events can establish a
 * subscription for PopupEventType.class.getCanonicalName().
 * 
 * @author David Diepenbrock
 */
public class PopupEventType extends OseeRemoteEventInstance {

   private static final long serialVersionUID = 8818248591835649870L;
   private String message;

   public PopupEventType(String eventGuid, String message) {
      super(eventGuid);
      this.message = message;
   }

   public String getMessage() {
      return message;
   }

}
