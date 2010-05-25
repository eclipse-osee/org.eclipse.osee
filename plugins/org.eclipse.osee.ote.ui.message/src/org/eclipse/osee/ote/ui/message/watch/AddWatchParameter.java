/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.message.watch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ken J. Aguilar
 */
public class AddWatchParameter {
   private final HashMap<String, ArrayList<ElementPath>> watchMap = new HashMap<String, ArrayList<ElementPath>>(64);

   public AddWatchParameter() {
      super();
   }

   public AddWatchParameter(String message) {
      addMessage(message);
   }

   public AddWatchParameter(String message, ElementPath element) {
      addMessage(message, element);
   }

   public AddWatchParameter(Map<String, ArrayList<ElementPath>> map) {
      watchMap.putAll(map);
   }

   public void addMessage(String name, Collection<ElementPath> elements) {
      ArrayList<ElementPath> list = watchMap.get(name);
      if (list == null) {
         list = new ArrayList<ElementPath>(elements.size());
         watchMap.put(name, list);
      }
      list.addAll(elements);
   }

   public void addMessageWithAllElements(String name) {
      watchMap.put(name, null);
   }

   public void addMessage(String name, ElementPath... elements) {
      ArrayList<ElementPath> list = watchMap.get(name);
      if (list == null) {
         list = new ArrayList<ElementPath>(elements.length);
         watchMap.put(name, list);
      }
      for (ElementPath element : elements) {
         list.add(element);
      }
   }

   public void addMessage(String name) {
      ArrayList<ElementPath> list = watchMap.get(name);
      if (list == null) {
         list = new ArrayList<ElementPath>();
         watchMap.put(name, list);
      }
   }

   public void addMessage(String name, ElementPath element) {
      ArrayList<ElementPath> list = watchMap.get(name);
      if (list == null) {
         list = new ArrayList<ElementPath>(1024);
         watchMap.put(name, list);
      }
      list.add(element);
   }

   public Collection<String> getMessages() {
      return watchMap.keySet();
   }

   public Collection<ElementPath> getMessageElements(String messageNmae) {
      return watchMap.get(messageNmae);
   }

   public boolean containsMessage(String name) {
      return watchMap.containsKey(name);
   }
}
