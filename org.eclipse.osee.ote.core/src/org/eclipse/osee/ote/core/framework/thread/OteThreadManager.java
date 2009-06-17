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
package org.eclipse.osee.ote.core.framework.thread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadFactory;

/**
 * @author Roberto E. Escobar
 */
public class OteThreadManager {

   private static OteThreadManager instance = null;

   private Map<String, OteThreadFactory> factories;

   private OteThreadManager() {
      this.factories = new HashMap<String, OteThreadFactory>();
   }

   public static OteThreadManager getInstance() {
      if (instance == null) {
         instance = new OteThreadManager();
      }
      return instance;
   }

   public ThreadFactory createNewFactory(String threadName) {
      OteThreadFactory factory = new OteThreadFactory(threadName);
      factories.put(threadName, factory);
      return factory;
   }

   public List<OteThread> getThreadsFromFactory(String key) {
      OteThreadFactory factory = factories.get(key);
      return factory.getThreads();
   }

   public Set<String> getFactories() {
      return factories.keySet();
   }
}