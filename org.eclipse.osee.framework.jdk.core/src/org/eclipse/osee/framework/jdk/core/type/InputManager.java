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
package org.eclipse.osee.framework.jdk.core.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Roberto E. Escobar
 */
public class InputManager<T> implements IInputListener<T> {

   private List<T> inputList;
   private Set<IInputListener<T>> changeListeners;

   public InputManager() {
      super();
      inputList = Collections.synchronizedList(new ArrayList<T>());
      changeListeners = Collections.synchronizedSet(new HashSet<IInputListener<T>>());
   }

   public List<T> getInputList() {
      return inputList;
   }

   public void addNode(T inNode) {
      synchronized (inputList) {
         inputList.add(inputList.size(), inNode);
      }
      synchronized (changeListeners) {
         Iterator<IInputListener<T>> iterator = changeListeners.iterator();
         while (iterator.hasNext())
            (iterator.next()).addNode(inNode);
      }
   }

   public void addNodes(Collection<T> inNode) {
      synchronized (inputList) {
         this.inputList.addAll(inNode);
      }
      synchronized (changeListeners) {
         Iterator<IInputListener<T>> iterator = changeListeners.iterator();
         while (iterator.hasNext())
            (iterator.next()).addNodes(inNode);
      }
   }

   public void removeNode(T inNode) {
      synchronized (inputList) {
         this.inputList.remove(inNode);
      }
      synchronized (changeListeners) {
         Iterator<IInputListener<T>> iterator = changeListeners.iterator();
         while (iterator.hasNext())
            (iterator.next()).removeNode(inNode);
      }
   }

   public void nodeChanged(T inNode) {
      synchronized (changeListeners) {
         Iterator<IInputListener<T>> iterator = changeListeners.iterator();
         while (iterator.hasNext())
            (iterator.next()).nodeChanged(inNode);
      }
   }

   public void inputChanged() {
      synchronized (changeListeners) {
         Iterator<IInputListener<T>> iterator = changeListeners.iterator();
         while (iterator.hasNext())
            (iterator.next()).inputChanged();
      }
   }

   public void removeAll() {
      synchronized (inputList) {
         this.inputList.clear();
      }
      synchronized (changeListeners) {
         Iterator<IInputListener<T>> iterator = changeListeners.iterator();
         while (iterator.hasNext()) {
            (iterator.next()).removeAll();
         }
      }
   }

   public void addInputListener(IInputListener<T> listener) {
      synchronized (changeListeners) {
         changeListeners.add(listener);
      }

      for (T node : inputList) {
         listener.addNode(node);
      }
   }

   public void removeInputListener(IInputListener<T> listener) {
      synchronized (changeListeners) {
         changeListeners.remove(listener);
      }
   }

   public String toString() {
      synchronized (inputList) {
         String toReturn = "";
         for (int i = 0; i < inputList.size(); i++) {
            T node = (T) inputList.get(i);
            toReturn += node.toString();
         }
         return toReturn + "\n\n";
      }
   }

   public void dispose() {
      synchronized (inputList) {
         inputList.clear();
      }
      synchronized (changeListeners) {
         changeListeners.clear();
      }
   }
}
