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
package org.eclipse.osee.event.osgi.internal;

import java.util.Map;

/**
 * @author Roberto E. Escobar
 */
public final class EventOsgiUtil {

   private EventOsgiUtil() {
      // Utility Class
   }

   public static org.eclipse.osee.event.Event toOseeEvent(org.osgi.service.event.Event event) {
      return new EventProxy(event);
   }

   public static org.osgi.service.event.Event toOsgiEvent(String topic, Map<String, ?> data) {
      return new org.osgi.service.event.Event(topic, data);
   }
}
