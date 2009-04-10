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
package org.eclipse.osee.define.traceability.operations;

import java.net.URI;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.traceability.ITraceParser;
import org.eclipse.osee.define.traceability.ITraceUnitResourceLocator;
import org.eclipse.osee.define.traceability.data.TraceMark;
import org.eclipse.osee.define.traceability.data.TraceUnit;
import org.eclipse.osee.define.utility.IResourceHandler;
import org.eclipse.osee.define.utility.UriResourceContentFinder;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class ResourceToTraceUnit {

   private final UriResourceContentFinder resourceFinder;
   private final Set<ITraceUnitProcessor> traceProcessors;
   private final HashCollection<ITraceUnitResourceLocator, ITraceParser> traceUnitHandlers;

   public ResourceToTraceUnit(final URI source, final boolean isRecursionAllowed, final boolean isFileWithMultiplePaths) {
      super();
      this.traceUnitHandlers = new HashCollection<ITraceUnitResourceLocator, ITraceParser>();
      this.traceProcessors = Collections.synchronizedSet(new HashSet<ITraceUnitProcessor>());
      this.resourceFinder = new UriResourceContentFinder(source, isRecursionAllowed, isFileWithMultiplePaths);
   }

   public void addTraceProcessor(ITraceUnitProcessor traceProcessor) {
      synchronized (traceProcessors) {
         traceProcessors.add(traceProcessor);
      }
   }

   public void removeTraceProcessor(ITraceUnitProcessor traceProcessor) {
      synchronized (traceProcessors) {
         traceProcessors.remove(traceProcessor);
      }
   }

   public void addTraceUnitHandler(ITraceUnitResourceLocator locator, ITraceParser parser) {
      traceUnitHandlers.put(locator, parser);
   }

   public void removeTestUnitLocator(ITraceUnitResourceLocator locator) {
      traceUnitHandlers.removeValues(locator);
   }

   private void clear() {
      for (ITraceUnitProcessor traceProcessor : traceProcessors) {
         traceProcessor.clear();
      }
      for (ITraceUnitResourceLocator locator : traceUnitHandlers.keySet()) {
         resourceFinder.removeLocator(locator);
      }
      System.gc();
   }

   public void execute(IProgressMonitor monitor) throws OseeCoreException {
      monitor.beginTask("Importing Test Units", IProgressMonitor.UNKNOWN);
      List<TraceUnitCollector> collectors = new ArrayList<TraceUnitCollector>();
      try {
         for (ITraceUnitResourceLocator locator : traceUnitHandlers.keySet()) {
            Collection<ITraceParser> parsers = traceUnitHandlers.getValues(locator);
            for (ITraceParser parser : parsers) {
               TraceUnitCollector testUnitCollector = new TraceUnitCollector(locator, parser);
               resourceFinder.addLocator(locator, testUnitCollector);
               collectors.add(testUnitCollector);
            }
         }

         resourceFinder.execute(monitor);

         if (!monitor.isCanceled()) {
            notifyOnInitialize(monitor);
         }

         for (TraceUnitCollector collector : collectors) {
            if (monitor.isCanceled()) break;
            if (!collector.isEmpty()) {
               processCollector(monitor, collector);
            }
         }

         if (!monitor.isCanceled()) {
            notifyOnComplete(monitor);
         }
      } finally {
         collectors.clear();
         clear();
         monitor.done();
      }
   }

   private void processCollector(IProgressMonitor monitor, TraceUnitCollector testUnitCollector) throws OseeCoreException {
      for (String testUnitType : testUnitCollector.getTraceUnitTypes()) {
         if (monitor.isCanceled()) break;
         Map<String, TraceUnit> unitToTrace = testUnitCollector.getUnitsToTraceMarks(testUnitType);
         if (unitToTrace != null) {
            for (String testUnitName : unitToTrace.keySet()) {
               if (monitor.isCanceled()) break;
               TraceUnit testUnit = unitToTrace.get(testUnitName);
               if (testUnit != null) {
                  notifyOnProcess(monitor, testUnit);
               }
            }
         }
      }
   }

   private void notifyOnProcess(IProgressMonitor monitor, TraceUnit testUnit) throws OseeCoreException {
      for (ITraceUnitProcessor traceProcessor : traceProcessors) {
         traceProcessor.process(monitor, testUnit);
      }
   }

   private void notifyOnInitialize(IProgressMonitor monitor) {
      for (ITraceUnitProcessor traceProcessor : traceProcessors) {
         traceProcessor.initialize(monitor);
      }
   }

   private void notifyOnComplete(IProgressMonitor monitor) throws OseeCoreException {
      for (ITraceUnitProcessor traceProcessor : traceProcessors) {
         traceProcessor.onComplete(monitor);
      }
   }

   private final class TraceUnitCollector implements IResourceHandler {

      private final ITraceParser traceParser;
      private final ITraceUnitResourceLocator traceUnitLocator;
      private final Map<String, Map<String, TraceUnit>> traceUnitToTraceMarks;

      public TraceUnitCollector(ITraceUnitResourceLocator traceUnitLocator, ITraceParser traceParser) {
         this.traceParser = traceParser;
         this.traceUnitLocator = traceUnitLocator;
         this.traceUnitToTraceMarks = new HashMap<String, Map<String, TraceUnit>>();
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.define.traceability.IResourceHandler#onResourceFound(java.net.URI, java.lang.String, java.nio.CharBuffer)
       */
      @Override
      public void onResourceFound(URI uriPath, String name, CharBuffer fileBuffer) {
         String traceUnitType = traceUnitLocator.getTraceUnitType(name, fileBuffer);
         if (Strings.isValid(traceUnitType) && !traceUnitType.equalsIgnoreCase(ITraceUnitResourceLocator.UNIT_TYPE_UNKNOWN)) {
            Collection<TraceMark> traceMarks = traceParser.getTraceMarks(fileBuffer);
            if (traceMarks != null && !traceMarks.isEmpty()) {
               Map<String, TraceUnit> traceUnits = traceUnitToTraceMarks.get(traceUnitType);
               if (traceUnits == null) {
                  traceUnits = new HashMap<String, TraceUnit>();
                  traceUnitToTraceMarks.put(traceUnitType, traceUnits);
               }
               TraceUnit unit = traceUnits.get(name);
               if (unit == null) {
                  unit = new TraceUnit(traceUnitType, name);
                  traceUnits.put(name, unit);
               }
               unit.addAllTraceMarks(traceMarks);
            }
         }
      }

      public boolean isEmpty() {
         return traceUnitToTraceMarks.isEmpty();
      }

      public void reset() {
         this.traceUnitToTraceMarks.clear();
      }

      public Set<String> getTraceUnitTypes() {
         return traceUnitToTraceMarks.keySet();
      }

      public Map<String, TraceUnit> getUnitsToTraceMarks(String unitType) {
         return traceUnitToTraceMarks.get(unitType);
      }
   }
}
