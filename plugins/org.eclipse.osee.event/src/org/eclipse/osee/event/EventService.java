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
package org.eclipse.osee.event;

import java.util.Map;

/**
 * @author Roberto E. Escobar
 */
public interface EventService {

   /**
    * Initiate asynchronous, ordered delivery of an event. This method returns to the caller before delivery of the
    * event is completed.
    */
   void postEvent(String topic, Map<String, ?> data);

   /**
    * Initiate synchronous delivery of an event. This method does not return to the caller until delivery of the event
    * is completed.
    */
   void sendEvent(String topic, Map<String, ?> data);

}
