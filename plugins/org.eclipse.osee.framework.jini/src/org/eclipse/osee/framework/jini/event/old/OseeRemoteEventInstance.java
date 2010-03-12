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

import java.io.Serializable;

/**
 * This class provides all that is needed for sending RemoteEvents. It should be extended for each specific type of
 * event.
 * <p>
 * Listeners can subscribe using <code>&lt;SubClass&gt;.class.getCanonicalName()</code> as the event type ID (unless
 * the subclass overrides getEventType()).
 * 
 * @author David Diepenbrock
 */
public abstract class OseeRemoteEventInstance implements Serializable {

   public final String eventGuid;
   private final Class<? extends OseeRemoteEventInstance> myClass;

   public OseeRemoteEventInstance(String eventGuid) {
      this.eventGuid = eventGuid;
      myClass = this.getClass();
   }

   /**
    * A serializable object which uniquely identifies this event type. Most commonly this will be the class name of the
    * specific EventInstance class. Listeners who have registered using an eventID which evaluate true using .equals()
    * will receive notification of this event. By default the canonical class name is used. It is not recommended for
    * subclasses to override this method.
    * 
    * @return a serializable object which uniquely identifies this event type.
    */
   public String getEventType() {
      return myClass.getCanonicalName();
   }

   public String getEventGuid() {
      return eventGuid;
   }
}
