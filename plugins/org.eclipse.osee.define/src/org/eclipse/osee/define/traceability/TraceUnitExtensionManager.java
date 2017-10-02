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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.define.internal.Activator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
      contributions = new HashMap<>();
   }

   public static TraceUnitExtensionManager getInstance() {
      if (instance == null) {
         instance = new TraceUnitExtensionManager();
      }
      return instance;
   }

   public Set<String> getTraceUnitHandlerIds()  {
      checkObjectsLoaded();
      return contributions.keySet();
   }

   public Collection<TraceHandler> getAllTraceHandlers()  {
      checkObjectsLoaded();
      return contributions.values();
   }

   public TraceHandler getTraceUnitHandlerById(String id)  {
      checkObjectsLoaded();
      return contributions.get(id);
   }

   public TraceHandler getTraceHandlerByName(String name)  {
      checkObjectsLoaded();
      TraceHandler toReturn = null;
      for (TraceHandler handler : getAllTraceHandlers()) {
         if (handler.getName().equals(name)) {
            toReturn = handler;
            break;
         }
      }
      return toReturn;
   }

   public ITraceParser getTraceParserById(String id)  {
      TraceHandler traceUnitHandler = getTraceUnitHandlerById(id);
      if (traceUnitHandler != null) {
         return traceUnitHandler.getParser();
      }
      return null;
   }

   public ITraceUnitResourceLocator getTraceUnitLocatorById(String id)  {
      TraceHandler traceUnitHandler = getTraceUnitHandlerById(id);
      if (traceUnitHandler != null) {
         return traceUnitHandler.getLocator();
      }
      return null;
   }

   public List<String> getAllTraceHandlerNames()  {
      List<String> handlerNames = new LinkedList<>();
      for (TraceHandler handler : getAllTraceHandlers()) {
         handlerNames.add(handler.getName());
      }
      return handlerNames;
   }

   public Collection<ITraceParser> getAllTraceParsers()  {
      checkObjectsLoaded();
      Set<ITraceParser> parsers = new HashSet<>();
      for (TraceHandler traceHandler : contributions.values()) {
         ITraceParser parser = traceHandler.getParser();
         if (parser != null) {
            parsers.add(parser);
         }
      }
      return parsers;
   }

   public Collection<ITraceUnitResourceLocator> getAllTraceUnitLocators()  {
      checkObjectsLoaded();
      Set<ITraceUnitResourceLocator> locators = new HashSet<>();
      for (TraceHandler traceHandler : contributions.values()) {
         ITraceUnitResourceLocator locator = traceHandler.getLocator();
         if (locator != null) {
            locators.add(locator);
         }
      }
      return locators;
   }

   private void checkObjectsLoaded()  {
      if (contributions.isEmpty()) {
         List<IConfigurationElement> elements =
            ExtensionPoints.getExtensionElements(Activator.PLUGIN_ID + "." + TRACE_UNIT_HANDLER, TRACE_UNIT_HANDLER);
         for (IConfigurationElement element : elements) {
            IExtension extension = (IExtension) element.getParent();
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

   private Object loadClass(String bundleName, String className)  {
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

   public static final class TraceHandler {
      private final String name;
      private final String id;
      private final ITraceUnitResourceLocator locator;
      private final ITraceParser parser;

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
