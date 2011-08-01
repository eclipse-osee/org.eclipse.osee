/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.store;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.ITestUnitProvider;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import com.google.common.collect.HashBiMap;

/**
 * @author John Misinco
 */
public class TestUnitCache implements ITestUnitProvider {

   private final static int HASHMAP_SIZE = 3000;
   private final HashCollection<CoverageItem, Integer> itemsToTestUnit = new HashCollection<CoverageItem, Integer>(
      HASHMAP_SIZE);

   private int lastId;
   private final HashBiMap<Integer, String> idToNameCache;

   private final ITestUnitStore testUnitStore;
   private boolean ensurePopulatedRanOnce;
   private boolean cacheIsDirty;

   public TestUnitCache(ITestUnitStore testUnitStore) {
      super();
      this.idToNameCache = HashBiMap.create(HASHMAP_SIZE);
      this.lastId = 0;
      this.ensurePopulatedRanOnce = false;
      this.cacheIsDirty = false;
      this.testUnitStore = testUnitStore;
   }

   private Integer getKey(String testUnitName) throws OseeCoreException {
      ensurePopulated();
      Integer key = idToNameCache.inverse().get(testUnitName);
      if (key == null) {
         return ++lastId;
      } else {
         return key;
      }
   }

   public void put(String testUnitName) throws OseeCoreException {
      ensurePopulated();
      int key = getKey(testUnitName);
      put(key, testUnitName);
   }

   public void put(Integer key, String testUnitName) throws OseeCoreException {
      ensurePopulated();
      if (idToNameCache.containsKey(key) && !idToNameCache.get(key).equalsIgnoreCase(testUnitName)) {
         throw new OseeArgumentException("TestUnit key: [%s] has already been used", key);
      } else {
         idToNameCache.put(key, testUnitName);
         cacheIsDirty = true;
         lastId = Math.max(lastId, key);
      }
   }

   public Set<Entry<Integer, String>> getAllCachedTestUnitEntries() throws OseeCoreException {
      ensurePopulated();
      return idToNameCache.entrySet();
   }

   public Collection<String> getAllCachedTestUnitNames() throws OseeCoreException {
      ensurePopulated();
      return idToNameCache.values();
   }

   private Collection<String> getTestUnitsHelper(CoverageItem coverageItem, String searchName) throws OseeCoreException {
      ensurePopulated();
      Collection<Integer> entries = itemsToTestUnit.getValues(coverageItem);
      Set<String> names = new LinkedHashSet<String>();
      if (entries != null) {
         for (Integer entry : entries) {
            if (idToNameCache.containsKey(entry)) {
               String name = idToNameCache.get(entry);
               names.add(Strings.intern(name));
               if (searchName != null && searchName.equals(name)) {
                  break;
               }
            } else {
               names.add("UNRESOLVED_ID");
            }
         }
         return names;
      } else {
         return java.util.Collections.emptyList();
      }
   }

   @Override
   public Collection<String> getTestUnits(CoverageItem coverageItem) throws OseeCoreException {
      return getTestUnitsHelper(coverageItem, null);
   }

   @Override
   public void addTestUnit(CoverageItem coverageItem, String testUnitName) throws OseeCoreException {
      ensurePopulated();
      Collection<String> testUnitNames = getTestUnitsHelper(coverageItem, testUnitName);
      if (!testUnitNames.contains(testUnitName)) {
         if (idToNameCache.inverse().get(testUnitName) == null) {
            put(testUnitName);
         }
         int key = idToNameCache.inverse().get(testUnitName);
         itemsToTestUnit.put(coverageItem, key);
      }
   }

   @Override
   public void removeTestUnit(CoverageItem coverageItem, String testUnitName) throws OseeCoreException {
      ensurePopulated();
      Integer value = idToNameCache.inverse().get(testUnitName);
      if (value != null) {
         itemsToTestUnit.removeValue(coverageItem, value);
      } else {
         OseeLog.format(Activator.class, Level.WARNING, "TestUnitName: [%s] is not associated with CoverageItem: [%s]",
            testUnitName, coverageItem.toString());
      }
   }

   @Override
   public void setTestUnits(CoverageItem coverageItem, Collection<String> testUnitNames) throws OseeCoreException {
      ensurePopulated();
      itemsToTestUnit.removeValues(coverageItem);
      Collection<String> entries = getTestUnits(coverageItem);
      List<String> notAdded = Collections.setComplement(testUnitNames, entries);
      for (String testUnitName : notAdded) {
         Integer key = getKey(testUnitName);
         put(key, testUnitName);
         itemsToTestUnit.put(coverageItem, key);
      }
   }

   @Override
   public String toXml(CoverageItem coverageItem) throws OseeCoreException {
      String toReturn = "";
      Collection<Integer> values = itemsToTestUnit.getValues(coverageItem);
      if (values != null) {
         List<Integer> testIdEntries = (List<Integer>) values;
         java.util.Collections.sort(testIdEntries);
         toReturn = Collections.toString(";", testIdEntries);
         persist();
      }
      return toReturn;
   }

   @Override
   public void fromXml(CoverageItem coverageItem, String xml) {
      itemsToTestUnit.removeValues(coverageItem);
      Set<Integer> entries = new TreeSet<Integer>();
      String[] testUnitIds = xml.split(";");
      for (String key : testUnitIds) {
         Integer iKey = new Integer(key);
         entries.add(iKey);
      }
      itemsToTestUnit.put(coverageItem, entries);
   }

   public void persist() throws OseeCoreException {
      ensurePopulated();
      if (cacheIsDirty) {
         testUnitStore.store(this);
         cacheIsDirty = false;
      }
   }

   private synchronized void ensurePopulated() throws OseeCoreException {
      if (!ensurePopulatedRanOnce) {
         ensurePopulatedRanOnce = true;
         try {
            testUnitStore.load(this);
         } catch (OseeCoreException ex) {
            throw ex;
         }
      }
   }

}