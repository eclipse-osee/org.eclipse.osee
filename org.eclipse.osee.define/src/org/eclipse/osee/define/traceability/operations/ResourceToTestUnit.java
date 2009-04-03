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
import org.eclipse.osee.define.traceability.ITestUnitLocator;
import org.eclipse.osee.define.traceability.ITraceParser;
import org.eclipse.osee.define.traceability.TestUnit;
import org.eclipse.osee.define.utility.IResourceHandler;
import org.eclipse.osee.define.utility.UriResourceContentFinder;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class ResourceToTestUnit {

   private final UriResourceContentFinder resourceFinder;
   private final Set<ITestUnitProcessor> traceProcessors;
   private final HashCollection<ITestUnitLocator, ITraceParser> testUnitHandlers;

   public ResourceToTestUnit(final URI source, final boolean isRecursionAllowed) {
      super();
      this.testUnitHandlers = new HashCollection<ITestUnitLocator, ITraceParser>();
      this.traceProcessors = Collections.synchronizedSet(new HashSet<ITestUnitProcessor>());
      this.resourceFinder = new UriResourceContentFinder(source, isRecursionAllowed);
   }

   public void addTraceProcessor(ITestUnitProcessor traceProcessor) {
      synchronized (traceProcessors) {
         traceProcessors.add(traceProcessor);
      }
   }

   public void removeTraceProcessor(ITestUnitProcessor traceProcessor) {
      synchronized (traceProcessors) {
         traceProcessors.remove(traceProcessor);
      }
   }

   public void addTestUnitHandler(ITestUnitLocator locator, ITraceParser parser) {
      testUnitHandlers.put(locator, parser);
   }

   public void removeTestUnitLocator(ITestUnitLocator locator) {
      testUnitHandlers.removeValues(locator);
   }

   private void clear() {
      for (ITestUnitProcessor traceProcessor : traceProcessors) {
         traceProcessor.clear();
      }
      for (ITestUnitLocator locator : testUnitHandlers.keySet()) {
         resourceFinder.removeLocator(locator);
      }
      System.gc();
   }

   public void execute(IProgressMonitor monitor) throws OseeCoreException {
      monitor.beginTask("Importing Test Units", IProgressMonitor.UNKNOWN);
      List<TestUnitCollector> collectors = new ArrayList<TestUnitCollector>();
      try {
         for (ITestUnitLocator locator : testUnitHandlers.keySet()) {
            Collection<ITraceParser> parsers = testUnitHandlers.getValues(locator);
            for (ITraceParser parser : parsers) {
               TestUnitCollector testUnitCollector = new TestUnitCollector(locator, parser);
               resourceFinder.addLocator(locator, testUnitCollector);
               collectors.add(testUnitCollector);
            }
         }

         resourceFinder.execute(monitor);

         if (!monitor.isCanceled()) {
            initializeTestUnitProcessor(monitor);
         }

         for (TestUnitCollector testUnitCollector : collectors) {
            if (monitor.isCanceled()) break;
            if (!testUnitCollector.isEmpty()) {
               processCollector(monitor, testUnitCollector);
            }
         }

         if (!monitor.isCanceled()) {
            completeTestUnitProcessor(monitor);
         }
      } finally {
         collectors.clear();
         clear();
         monitor.done();
      }
   }

   private void processCollector(IProgressMonitor monitor, TestUnitCollector testUnitCollector) {
      for (String testUnitType : testUnitCollector.getTestUnitTypes()) {
         if (monitor.isCanceled()) break;
         Map<String, TestUnit> unitToTrace = testUnitCollector.getUnitsToTraceMarks(testUnitType);
         if (unitToTrace != null) {
            for (String testUnitName : unitToTrace.keySet()) {
               if (monitor.isCanceled()) break;
               TestUnit testUnit = unitToTrace.get(testUnitName);
               if (testUnit != null) {
                  notifyTestUnitProcessor(monitor, testUnit);
               }
            }
         }
      }
   }

   private void notifyTestUnitProcessor(IProgressMonitor monitor, TestUnit testUnit) {
      for (ITestUnitProcessor traceProcessor : traceProcessors) {
         traceProcessor.process(monitor, testUnit);
      }
   }

   private void initializeTestUnitProcessor(IProgressMonitor monitor) {
      for (ITestUnitProcessor traceProcessor : traceProcessors) {
         traceProcessor.initialize(monitor);
      }
   }

   private void completeTestUnitProcessor(IProgressMonitor monitor) {
      for (ITestUnitProcessor traceProcessor : traceProcessors) {
         traceProcessor.onComplete(monitor);
      }
   }

   private final class TestUnitCollector implements IResourceHandler {

      private final ITraceParser traceParser;
      private final ITestUnitLocator testUnitLocator;
      private final Map<String, Map<String, TestUnit>> testUnitToTraceMarks;

      public TestUnitCollector(ITestUnitLocator testUnitLocator, ITraceParser traceParser) {
         this.traceParser = traceParser;
         this.testUnitLocator = testUnitLocator;
         this.testUnitToTraceMarks = new HashMap<String, Map<String, TestUnit>>();
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.define.traceability.IResourceHandler#onResourceFound(java.net.URI, java.lang.String, java.nio.CharBuffer)
       */
      @Override
      public void onResourceFound(URI uriPath, String name, CharBuffer fileBuffer) {
         String testUnitType = testUnitLocator.getTestUnitType(name, fileBuffer);
         if (Strings.isValid(testUnitType) && !testUnitType.equalsIgnoreCase(ITestUnitLocator.UNIT_TYPE_UNKNOWN)) {
            HashCollection<String, String> traceMarks = traceParser.getTraceMarksByType(fileBuffer);
            if (!traceMarks.isEmpty()) {
               Map<String, TestUnit> testUnits = testUnitToTraceMarks.get(testUnitType);
               if (testUnits == null) {
                  testUnits = new HashMap<String, TestUnit>();
                  testUnitToTraceMarks.put(testUnitType, testUnits);
               }
               TestUnit testUnit = testUnits.get(name);
               if (testUnit == null) {
                  testUnit = new TestUnit(testUnitType, name);
                  testUnits.put(name, testUnit);
               }
               for (String traceType : traceMarks.keySet()) {
                  Collection<String> traceItems = traceMarks.getValues(traceType);
                  if (traceItems != null && !traceItems.isEmpty()) {
                     testUnit.addAllTraceMarks(traceType, traceItems);
                  }
               }
            }
         }
      }

      public boolean isEmpty() {
         return testUnitToTraceMarks.isEmpty();
      }

      public void reset() {
         this.testUnitToTraceMarks.clear();
      }

      public Set<String> getTestUnitTypes() {
         return testUnitToTraceMarks.keySet();
      }

      public Map<String, TestUnit> getUnitsToTraceMarks(String unitType) {
         return testUnitToTraceMarks.get(unitType);
      }
   }
}
