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

import org.eclipse.osee.event.Event;

/**
 * @author Roberto E. Escobar
 */
public class EventProxy implements Event {

   private final org.osgi.service.event.Event proxiedObject;

   public EventProxy(org.osgi.service.event.Event event) {
      this.proxiedObject = event;
   }

   @Override
   public Object getValue(String name) {
      return proxiedObject.getProperty(name);
   }

   @Override
   public String[] getKeys() {
      return proxiedObject.getPropertyNames();
   }

   @Override
   public String getTopic() {
      return proxiedObject.getTopic();
   }

   @Override
   public String toString() {
      return proxiedObject.toString();
   }

   protected org.osgi.service.event.Event getProxiedObject() {
      return proxiedObject;
   }

}
