/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.api.workdef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * Provides enum like capabilities to Work Pages (like ordinal, values and valueOf) while providing for the inheritance
 * of classes.
 *
 * @author Donald G. Dunne
 */
public abstract class StateTypeAdapter implements IStateToken {
   private final StateType StateType;
   private static CompositeKeyHashMap<Class<?>, String, StateTypeAdapter> classAndNameToPage =
      new CompositeKeyHashMap<>();
   private static final Map<StateTypeAdapter, Integer> pageToOrdinal = new HashMap<>(10);
   private static final Map<Class<?>, List<StateTypeAdapter>> classToPages = new HashMap<>();
   private static final CountingMap<Class<?>> classToOrdinalCount = new CountingMap<>(20);
   private String description;
   private final String pageName;

   public StateTypeAdapter(Class<?> clazz, String pageName, StateType StateType) {
      this.pageName = pageName;
      this.StateType = StateType;
      classAndNameToPage.put(clazz, pageName, this);
      classToOrdinalCount.put(clazz);
      pageToOrdinal.put(this, classToOrdinalCount.get(clazz));
   }

   public int ordinal() {
      return pageToOrdinal.get(this);
   }

   @SuppressWarnings("unchecked")
   public static <T> T valueOfPage(Class<?> clazz, String pageName) {
      return (T) classAndNameToPage.get(clazz, pageName);
   }

   public synchronized static <T> List<T> pages(Class<?> clazz) {
      if (classToPages.get(clazz) == null) {
         Set<StateTypeAdapter> pages = new HashSet<>();
         for (StateTypeAdapter page : pageToOrdinal.keySet()) {
            if (page.getClass().isAssignableFrom(clazz)) {
               pages.add(page);
            }
         }
         List<StateTypeAdapter> pagesOrdered = new ArrayList<>();
         for (int x = 1; x <= pages.size(); x++) {
            for (StateTypeAdapter page : pages) {
               if (page.ordinal() == x) {
                  pagesOrdered.add(page);
               }
            }
         }
         classToPages.put(clazz, pagesOrdered);
      }
      return Collections.castAll(classToPages.get(clazz));
   }

   @Override
   public String getDescription() {
      return description;
   }

   @Override
   public String getName() {
      return pageName;
   }

   @Override
   public StateType getStateType() {
      return StateType;
   }

   @Override
   public boolean isCompleted() {
      return getStateType().isCompleted();
   }

   @Override
   public boolean isCancelled() {
      return getStateType().isCancelled();
   }

   @Override
   public boolean isWorking() {
      return getStateType().isWorking();
   }

   @Override
   public boolean isCompletedOrCancelled() {
      return getStateType().isCompletedOrCancelled();
   }

   public void setDescription(String description) {
      this.description = description;
   }

   @Override
   public String toString() {
      return String.format("[%s][%s]", getName(), getStateType().name());
   }
}
