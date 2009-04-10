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
package org.eclipse.osee.define.traceability;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class TraceUnitExtensionManager {

   private static final String TRACE_UNIT_HANDLER = "TraceUnitHandler";
   private static final String TRACE_UNIT_RESOURCE_LOCATOR = "TraceUnitResourceLocator";
   private static final String TRACE_UNIT_PARSER = "TraceUnitParser";

   private final Map<String, TraceHandler> contributions;

   private static TraceUnitExtensionManager instance = null;

   private TraceUnitExtensionManager() {
      contributions = new HashMap<String, TraceHandler>();
   }

   public static TraceUnitExtensionManager getInstance() {
      if (instance == null) {
         instance = new TraceUnitExtensionManager();
      }
      return instance;
   }

   public Set<String> getTraceUnitHandlerIds() throws OseeCoreException {
      checkObjectsLoaded();
      return contributions.keySet();
   }

   public Collection<TraceHandler> getAllTraceHandlers() throws OseeCoreException {
      checkObjectsLoaded();
      return contributions.values();
   }

   public TraceHandler getTraceUnitHandlerById(String id) throws OseeCoreException {
      checkObjectsLoaded();
      return contributions.get(id);
   }

   public ITraceParser getTraceParserById(String id) throws OseeCoreException {
      TraceHandler traceUnitHandler = getTraceUnitHandlerById(id);
      if (traceUnitHandler != null) {
         return traceUnitHandler.getParser();
      }
      return null;
   }

   public ITraceUnitResourceLocator getTraceUnitLocatorById(String id) throws OseeCoreException {
      TraceHandler traceUnitHandler = getTraceUnitHandlerById(id);
      if (traceUnitHandler != null) {
         return traceUnitHandler.getLocator();
      }
      return null;
   }

   public Collection<ITraceParser> getAllTraceParsers() throws OseeCoreException {
      checkObjectsLoaded();
      Set<ITraceParser> parsers = new HashSet<ITraceParser>();
      for (TraceHandler traceHandler : contributions.values()) {
         ITraceParser parser = traceHandler.getParser();
         if (parser != null) {
            parsers.add(parser);
         }
      }
      return parsers;
   }

   public Collection<ITraceUnitResourceLocator> getAllTraceUnitLocators() throws OseeCoreException {
      checkObjectsLoaded();
      Set<ITraceUnitResourceLocator> locators = new HashSet<ITraceUnitResourceLocator>();
      for (TraceHandler traceHandler : contributions.values()) {
         ITraceUnitResourceLocator locator = traceHandler.getLocator();
         if (locator != null) {
            locators.add(locator);
         }
      }
      return locators;
   }

   private void checkObjectsLoaded() throws OseeCoreException {
      if (contributions.isEmpty()) {
         List<IConfigurationElement> elements =
               ExtensionPoints.getExtensionElements(DefinePlugin.PLUGIN_ID + "." + TRACE_UNIT_HANDLER,
                     TRACE_UNIT_HANDLER);
         for (IConfigurationElement element : elements) {
            IExtension extension = ((IExtension) element.getParent());
            String identifier = extension.getUniqueIdentifier();
            String name = extension.getLabel();
            String bundleName = element.getContributor().getName();
            String parserClassName = element.getAttribute(TRACE_UNIT_PARSER);
            String locatorClassName = element.getAttribute(TRACE_UNIT_RESOURCE_LOCATOR);

            ITraceParser parser = (ITraceParser) loadClass(bundleName, parserClassName);
            ITraceUnitResourceLocator locator = (ITraceUnitResourceLocator) loadClass(bundleName, locatorClassName);
            if (parser != null && locator != null) {
               contributions.put(identifier, new TraceHandler(identifier, name, locator, parser));
            }
         }
      }
   }

   private Object loadClass(String bundleName, String className) throws OseeCoreException {
      Object object = null;
      if (Strings.isValid(bundleName) && Strings.isValid(className)) {
         try {
            Bundle bundle = Platform.getBundle(bundleName);
            Class<?> taskClass = bundle.loadClass(className);
            try {
               Method getInstance = taskClass.getMethod("getInstance", new Class[] {});
               object = getInstance.invoke(null, new Object[] {});
            } catch (Exception ex) {
               object = taskClass.newInstance();
            }
         } catch (Exception ex) {
            throw new OseeCoreException(String.format("Unable to Load: [%s - %s]", bundleName, className), ex);
         }
      }
      return object;
   }

   public final class TraceHandler {
      private String name;
      private String id;
      private ITraceUnitResourceLocator locator;
      private ITraceParser parser;

      private TraceHandler(String id, String name, ITraceUnitResourceLocator locator, ITraceParser parser) {
         super();
         this.name = name;
         this.id = id;
         this.locator = locator;
         this.parser = parser;
      }

      /**
       * @return the name
       */
      public String getName() {
         return name;
      }

      /**
       * @return the id
       */
      public String getId() {
         return id;
      }

      /**
       * @return the locator
       */
      public ITraceUnitResourceLocator getLocator() {
         return locator;
      }

      /**
       * @return the parser
       */
      public ITraceParser getParser() {
         return parser;
      }

   }
}
