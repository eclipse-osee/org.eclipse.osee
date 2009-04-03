/*
 * Created on Apr 2, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
import org.eclipse.osee.framework.jdk.core.type.ObjectPair;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.osgi.framework.Bundle;

/**
 * @author Roberto E. Escobar
 */
public class TestUnitTraceExtensionManager {

   private static final String TEST_UNIT_TRACE = "TestUnitTrace";
   private static final String TEST_UNIT_RESOURCE_LOCATOR = "TestUnitResourceLocator";
   private static final String TEST_UNIT_TRACE_PARSER = "TestUnitTraceParser";
   private final Map<String, ObjectPair<ITestUnitLocator, ITraceParser>> contributions;

   private static TestUnitTraceExtensionManager instance = null;

   private TestUnitTraceExtensionManager() {
      contributions = new HashMap<String, ObjectPair<ITestUnitLocator, ITraceParser>>();
   }

   public static TestUnitTraceExtensionManager getInstance() {
      if (instance == null) {
         instance = new TestUnitTraceExtensionManager();
      }
      return instance;
   }

   public Set<String> getTestUnitTraceIds() throws OseeCoreException {
      checkObjectsLoaded();
      return contributions.keySet();
   }

   public ITraceParser getTraceParserById(String id) throws OseeCoreException {
      checkObjectsLoaded();
      ObjectPair<ITestUnitLocator, ITraceParser> testUnitTrace = contributions.get(id);
      if (testUnitTrace != null) {
         return testUnitTrace.object2;
      }
      return null;
   }

   public ITestUnitLocator getTestUnitLocatorById(String id) throws OseeCoreException {
      checkObjectsLoaded();
      ObjectPair<ITestUnitLocator, ITraceParser> testUnitTrace = contributions.get(id);
      if (testUnitTrace != null) {
         return testUnitTrace.object1;
      }
      return null;
   }

   public Collection<ITraceParser> getAllTraceParsers() throws OseeCoreException {
      checkObjectsLoaded();
      Set<ITraceParser> parsers = new HashSet<ITraceParser>();
      for (ObjectPair<ITestUnitLocator, ITraceParser> entry : contributions.values()) {
         ITraceParser parser = entry.object2;
         if (parser != null) {
            parsers.add(parser);
         }
      }
      return parsers;
   }

   public Collection<ITestUnitLocator> getAllTestUnitLocators() throws OseeCoreException {
      checkObjectsLoaded();
      Set<ITestUnitLocator> locators = new HashSet<ITestUnitLocator>();
      for (ObjectPair<ITestUnitLocator, ITraceParser> entry : contributions.values()) {
         ITestUnitLocator locator = entry.object1;
         if (locator != null) {
            locators.add(locator);
         }
      }
      return locators;
   }

   private void checkObjectsLoaded() throws OseeCoreException {
      if (contributions.isEmpty()) {
         List<IConfigurationElement> elements =
               ExtensionPoints.getExtensionElements(DefinePlugin.PLUGIN_ID + "." + TEST_UNIT_TRACE, TEST_UNIT_TRACE);
         for (IConfigurationElement element : elements) {
            IExtension extension = ((IExtension) element.getParent());
            String identifier = extension.getUniqueIdentifier();
            String bundleName = element.getContributor().getName();
            String parserClassName = element.getAttribute(TEST_UNIT_TRACE_PARSER);
            String testUnitLocatorClassName = element.getAttribute(TEST_UNIT_RESOURCE_LOCATOR);

            ITraceParser parser = (ITraceParser) loadClass(bundleName, parserClassName);
            ITestUnitLocator locator = (ITestUnitLocator) loadClass(bundleName, testUnitLocatorClassName);
            if (parser != null && locator != null) {
               contributions.put(identifier, new ObjectPair<ITestUnitLocator, ITraceParser>(locator, parser));
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
}
