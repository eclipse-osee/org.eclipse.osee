/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs.server.internal.applications;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.ws.rs.core.Application;
import org.eclipse.osee.jaxrs.server.internal.JaxRsVisitable;
import org.eclipse.osee.jaxrs.server.internal.JaxRsVisitor;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractJaxRsApplicationContainer implements JaxRsVisitable {

   private final Map<String, JaxRsApplicationEntry> applications =
      new ConcurrentHashMap<>();

   private final String applicationContext;

   public AbstractJaxRsApplicationContainer(String applicationContext) {
      super();
      this.applicationContext = applicationContext;
   }

   public String getApplicationContext() {
      return applicationContext;
   }

   public void add(String key, Bundle bundle, Application application) {
      JaxRsApplicationEntry entry = new JaxRsApplicationEntry(bundle, application);
      applications.put(key, entry);
   }

   public void remove(String key) {
      applications.remove(key);
   }

   public boolean isEmpty() {
      return applications.isEmpty();
   }

   public int size() {
      return applications.size();
   }

   @Override
   public void accept(JaxRsVisitor visitor) {
      visitor.onStartApplicationContainer(applicationContext, size());
      try {
         for (Entry<String, JaxRsApplicationEntry> entry : applications.entrySet()) {
            String componentName = entry.getKey();
            JaxRsApplicationEntry value = entry.getValue();
            Bundle bundle = value.getBundle();
            Application application = value.getApplication();
            visitor.onApplication(applicationContext, componentName, bundle, application);
         }
      } finally {
         visitor.onEndApplicationContainer();
      }
   }

   protected Application getApplication() {
      Application toReturn = null;
      if (applications.size() > 1) {
         toReturn = newCompositeApplication();
      } else {
         JaxRsApplicationEntry entry = getFirstEntry();
         toReturn = entry != null ? entry.getApplication() : null;
      }
      return toReturn;
   }

   private JaxRsApplicationEntry getFirstEntry() {
      return !applications.isEmpty() ? applications.values().iterator().next() : null;
   }

   private final Application newCompositeApplication() {
      final Set<Class<?>> classes = new LinkedHashSet<>();
      final Set<Object> singletons = new LinkedHashSet<>();
      for (JaxRsApplicationEntry appEntry : applications.values()) {
         Application application = appEntry.getApplication();
         classes.addAll(application.getClasses());
         singletons.addAll(application.getSingletons());
      }
      return new Application() {
         @Override
         public Set<Class<?>> getClasses() {
            return classes;
         }

         @Override
         public Set<Object> getSingletons() {
            return singletons;
         }
      };
   }

   @Override
   public String toString() {
      return " applicationContext=" + applicationContext;
   }

   private final class JaxRsApplicationEntry {
      private final Bundle bundle;
      private final Application application;

      public JaxRsApplicationEntry(Bundle bundle, Application application) {
         super();
         this.bundle = bundle;
         this.application = application;
      }

      public Bundle getBundle() {
         return bundle;
      }

      public Application getApplication() {
         return application;
      }
   }
}