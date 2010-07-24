/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.store;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.ITestUnitProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * This provider takes test unit names and converts them to ids and adds name/id entry to db table. It uses the
 * SimpleTestUnitProvider implementation to store off the nameIds instead of the names.<br>
 * <br>
 * This saves space and allows CoverageItems meta-data to be stored in DB versus slow/non-bulk-loading binary attribute.
 * 
 * @author Donald G. Dunne
 */
public class DbTestUnitProvider implements ITestUnitProvider {
   private static DbTestUnitProvider instance = new DbTestUnitProvider();
   // This structure will store the nameIds that map to DB name table
   final HashCollection<CoverageItem, Integer> coverageItemToTestUnits =
      new HashCollection<CoverageItem, Integer>(1000);

   private DbTestUnitProvider() {
      instance = this;
   }

   public static DbTestUnitProvider instance() {
      return instance;
   }

   @Override
   public void setTestUnits(CoverageItem coverageItem, Collection<String> testUnitNames) throws OseeCoreException {
      coverageItemToTestUnits.removeValues(coverageItem);
      for (String testUnitName : testUnitNames) {
         addTestUnit(coverageItem, testUnitName);
      }
   }

   public void removeTestUnits(CoverageItem coverageItem) throws OseeCoreException {
      removeTestUnits(coverageItem, null);
   }

   /**
    * @param testUnitNames if null, removes all names
    */
   public void removeTestUnits(CoverageItem coverageItem, Collection<String> testUnitNames) throws OseeCoreException {
      if (testUnitNames == null) {
         for (String name : getTestUnits(coverageItem)) {
            Integer nameId = TestUnitStore.getTestUnitId(name, false);
            if (nameId != null) {
               coverageItemToTestUnits.removeValue(coverageItem, nameId);
            }
         }
      } else {
         for (String testUnitName : testUnitNames) {
            Integer nameId = TestUnitStore.getTestUnitId(testUnitName, false);
            if (nameId != null) {
               coverageItemToTestUnits.removeValue(coverageItem, nameId);
            }
         }
      }
   }

   @Override
   public void addTestUnit(CoverageItem coverageItem, String testUnitName) throws OseeCoreException {
      Collection<String> testUnitNames = getTestUnits(coverageItem);
      if (!testUnitNames.contains(testUnitName)) {
         Integer nameId = TestUnitStore.getTestUnitId(testUnitName, true);
         coverageItemToTestUnits.put(coverageItem, nameId);
      }
   }

   @Override
   public Collection<String> getTestUnits(CoverageItem coverageItem) {
      try {
         Collection<Integer> testUnitNameIds = coverageItemToTestUnits.getValues(coverageItem);
         if (testUnitNameIds != null) {
            Set<String> names = new HashSet<String>();
            for (Integer nameId : testUnitNameIds) {
               String name = TestUnitStore.getTestUnitName(nameId);
               if (Strings.isValid(name)) {
                  names.add(name);
               }
            }
            return names;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return Collections.emptyList();
   }

   @Override
   public void fromXml(CoverageItem coverageItem, String xml) throws OseeCoreException {
      removeTestUnits(coverageItem);
      for (String nameIdStr : xml.split(";")) {
         try {
            coverageItemToTestUnits.put(coverageItem, new Integer(nameIdStr));
         } catch (NumberFormatException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   @Override
   public String toXml(CoverageItem coverageItem) throws OseeCoreException {
      Collection<Integer> testUnitNameIds = coverageItemToTestUnits.getValues(coverageItem);
      if (testUnitNameIds == null) {
         return "";
      }
      return org.eclipse.osee.framework.jdk.core.util.Collections.toString(testUnitNameIds, ";");
   }

   @Override
   public void removeTestUnit(CoverageItem coverageItem, String testUnitName) throws OseeCoreException {
      removeTestUnits(coverageItem, Arrays.asList(testUnitName));
   }
}
