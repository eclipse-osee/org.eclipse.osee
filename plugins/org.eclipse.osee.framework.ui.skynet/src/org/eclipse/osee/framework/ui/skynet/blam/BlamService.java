/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.framework.ui.skynet.blam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.blam.test.AbstractBlamTest;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Donald G. Dunne
 */
@Component(service = BlamService.class, immediate = true)
public class BlamService {

   private static final Map<AbstractBlam, AbstractBlamTest> blamsToTestMap =
      new HashMap<AbstractBlam, AbstractBlamTest>();

   @Reference(service = //
   AbstractBlam.class, //
      cardinality = ReferenceCardinality.MULTIPLE, //
      policy = ReferencePolicy.DYNAMIC, //
      policyOption = ReferencePolicyOption.GREEDY, //
      bind = "addBlam" //
   )

   public void addBlam(AbstractBlam blam) {
      AbstractBlamTest test = getBlamTest(blam);
      // Store nulls do we don't keep trying to reload test that doesn't exist
      blamsToTestMap.put(blam, test);
   }

   public BlamService() {
      // for osgi
   }

   public static Collection<AbstractBlam> getBlams() {
      return blamsToTestMap.keySet();
   }

   public static List<AbstractBlamTest> getBlamTests() {
      List<AbstractBlamTest> tests = new ArrayList<>();
      for (AbstractBlam blam : getBlams()) {
         getBlamTest(blam);
      }
      return tests;
   }

   private static AbstractBlamTest getBlamTest(AbstractBlam blam) {
      if (blamsToTestMap.containsKey(blam)) {
         return blamsToTestMap.get(blam);
      }

      String testClassName = blam.getClass().getName() + "Test";
      try {
         Class<?> testClass = blam.getClass().getClassLoader().loadClass(testClassName);
         AbstractBlamTest test = (AbstractBlamTest) testClass.getDeclaredConstructor().newInstance();
         return test;
      } catch (ClassNotFoundException ex) {
         // No test class for this blam
      } catch (Exception ex) {
         OseeLog.logf(BlamService.class, Level.WARNING, "Error instantiating test %s: %s", testClassName,
            ex.getMessage());
      }
      return null;
   }

   public static Map<AbstractBlam, AbstractBlamTest> getBlamsToTestMap() {
      return blamsToTestMap;
   }

}
