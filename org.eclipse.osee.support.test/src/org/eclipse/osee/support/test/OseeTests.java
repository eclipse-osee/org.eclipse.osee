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
package org.eclipse.osee.support.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import junit.framework.Test;
import junit.framework.TestCase;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public abstract class OseeTests {

   public static String EXTENSION_POINT = "org.eclipse.osee.support.test.OseeTest";

   public static List<Test> getOseeTests(OseeTestType oseeTestType) {
      List<Test> tasks = new ArrayList<Test>();
      List<IConfigurationElement> iExtensions = ExtensionPoints.getExtensionElements(EXTENSION_POINT, "OseeTest");
      for (IConfigurationElement element : iExtensions) {
         String className = element.getAttribute("classname");
         String bundleName = element.getContributor().getName();
         try {
            if (className != null && bundleName != null) {
               Bundle bundle = Platform.getBundle(bundleName);
               Class<?> interfaceClass = bundle.loadClass(className);
               IOseeTest oseeTest = (IOseeTest) interfaceClass.getConstructor().newInstance();
               if (oseeTest.getTestTypes().contains(oseeTestType)) {
                  if (oseeTest instanceof TestCase) {
                     OseeLog.log(Activator.class, Level.SEVERE,
                           "OseeTests only valid for Test Suites.  Ignorning Invalid TestCase: \"" + className + "\"");
                  } else {
                     Method suiteMethod = interfaceClass.getMethod("suite", new Class[0]);
                     tasks.add((Test) suiteMethod.invoke(null, new Object[0]));
                  }
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE,
                  "Problem loading IOseeTest extension \"" + className + "\".  Ignorning.", ex);
         }
      }
      return tasks;
   }

}
