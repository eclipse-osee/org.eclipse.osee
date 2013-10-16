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

import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.junit.Assert;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoverageOptionManager;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.CoverageUnitFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @author John R. Misinco
 */
public class TestUnitCacheTest {

   @Mock
   private ITestUnitStore testUnitStore;

   private TestUnitCache tc;

   @Before
   public void setUp() throws OseeCoreException {
      MockitoAnnotations.initMocks(this);

      when(testUnitStore.getNextTestUnitId()).thenAnswer(new Answer<Integer>() {
         private int id;

         @Override
         public Integer answer(InvocationOnMock invocation) throws Throwable {
            return id++;
         }
      });

      tc = new TestUnitCache(testUnitStore);
   }

   @Test
   public void testPut() throws OseeCoreException {
      tc.put("test1");
      tc.put("test2");
      Assert.assertEquals(2, tc.getAllCachedTestUnitNames().size());

      boolean exceptioned = false;
      try {
         tc.put(1, "test3");
      } catch (OseeArgumentException ex) {
         exceptioned = true;
         Assert.assertEquals("TestUnit key: [1] has already been used", ex.getLocalizedMessage());
      }
      Assert.assertTrue(exceptioned);

      tc.put("test1");
      Assert.assertEquals(2, tc.getAllCachedTestUnitNames().size());

      tc.put(4, "test4");
      Assert.assertEquals(3, tc.getAllCachedTestUnitNames().size());
   }

   @Test
   public void testGetAllCachedTestUnitEntries() throws OseeCoreException {
      HashMap<Integer, String> entries = new HashMap<Integer, String>();
      for (int i = 0; i < 10; i++) {
         entries.put(i, "test" + Integer.toString(i));
         tc.put(i, "test" + Integer.toString(i));
      }

      Set<Entry<Integer, String>> cacheEntries = tc.getAllCachedTestUnitEntries();
      int numFound = 0;
      for (Entry<Integer, String> cacheEntry : cacheEntries) {
         if (entries.containsKey(cacheEntry.getKey()) && entries.get(cacheEntry.getKey()).equals(cacheEntry.getValue())) {
            numFound++;
         }
      }

      Assert.assertTrue(numFound == entries.size());
   }

   @Test
   public void testGetAllCachedTestUnitNames() throws OseeCoreException {
      String[] entries = {"test1", "test2", "test3", "test4"};
      for (String entry : entries) {
         tc.put(entry);
      }

      Set<Entry<Integer, String>> cacheEntries = tc.getAllCachedTestUnitEntries();
      List<String> foundNames = new ArrayList<String>();
      for (Entry<Integer, String> cacheEntry : cacheEntries) {
         foundNames.add(cacheEntry.getValue());
      }

      Assert.assertTrue(foundNames.size() == entries.length);
      Assert.assertTrue(Collections.setComplement(foundNames, Arrays.asList(entries)).isEmpty());
   }

   @Test
   public void testAddAndGetTestUnits() throws OseeCoreException {
      Collection<String> expected = new ArrayList<String>();
      CoverageItem ci = createCoverageItem(tc);
      for (int i = 0; i < 10; i++) {
         String testUnitName = "test" + Integer.toString(i);
         expected.add(testUnitName);
         tc.put(i, testUnitName);
         tc.addTestUnit(ci, testUnitName);
      }

      Collection<String> actual = tc.getTestUnits(ci);
      Assert.assertTrue(Collections.setComplement(expected, actual).isEmpty());

   }

   @Test
   public void testRemoveTestUnit() throws OseeCoreException {
      Collection<String> expected = new ArrayList<String>();
      CoverageItem ci = createCoverageItem(tc);
      for (int i = 0; i < 10; i++) {
         String testUnitName = "test" + Integer.toString(i);
         expected.add(testUnitName);
         tc.put(i, testUnitName);
         tc.addTestUnit(ci, testUnitName);
      }

      Collection<String> actual = tc.getTestUnits(ci);
      Assert.assertTrue(Collections.setComplement(expected, actual).isEmpty());

      tc.removeTestUnit(ci, "test1");
      tc.removeTestUnit(ci, "test5");

      expected.remove("test1");
      expected.remove("test5");
      actual = tc.getTestUnits(ci);

      Assert.assertTrue(Collections.setComplement(expected, actual).isEmpty());

   }

   @Test
   public void testSetTestUnits() throws OseeCoreException {
      Collection<String> expected = new ArrayList<String>();
      CoverageItem ci = createCoverageItem(tc);
      for (int i = 0; i < 10; i++) {
         String testUnitName = "test" + Integer.toString(i);
         expected.add(testUnitName);
         tc.put(testUnitStore.getNextTestUnitId(), testUnitName);
      }

      tc.setTestUnits(ci, expected);
      Collection<String> actual = tc.getTestUnits(ci);
      Assert.assertTrue(Collections.setComplement(expected, actual).isEmpty());

      expected.add("test12");
      tc.setTestUnits(ci, expected);
      actual = tc.getTestUnits(ci);
      Assert.assertTrue(Collections.setComplement(expected, actual).isEmpty());

   }

   @Test
   public void testToXml() throws OseeCoreException {
      HashMap<Integer, String> entries = new HashMap<Integer, String>();
      for (int i = 0; i < 10; i++) {
         entries.put(i, "test" + Integer.toString(i));
         tc.put(testUnitStore.getNextTestUnitId(), "test" + Integer.toString(i));
      }

      CoverageItem ci = createCoverageItem(tc);
      tc.addTestUnit(ci, "test2");
      tc.addTestUnit(ci, "test1");
      tc.addTestUnit(ci, "test10");

      String expected = "1;2;10";
      String actual = tc.toXml(ci);
      Assert.assertEquals(expected, actual);
   }

   @Test
   public void testFromXml() throws OseeCoreException {
      for (int i = 0; i < 10; i++) {
         tc.put(i, "test" + Integer.toString(i));
      }
      CoverageItem ci = createCoverageItem(tc);

      String fromXml = "1;8;2;10";
      tc.fromXml(ci, fromXml);
      Collection<String> units = tc.getTestUnits(ci);
      Collection<String> expected = new ArrayList<String>();
      expected.add("test1");
      expected.add("test2");
      expected.add("test8");

      Assert.assertTrue(Collections.setComplement(expected, units).isEmpty());
   }

   @Test
   public void testReset() throws OseeCoreException {
      CoverageItem ci = createCoverageItem(tc);
      Collection<String> expected = new ArrayList<String>();

      for (int i = 0; i < 10; i++) {
         tc.put(i, "test" + Integer.toString(i));
      }

      tc.setTestUnits(ci, expected);
      Collection<String> actual = tc.getTestUnits(ci);
      Assert.assertTrue(Collections.setComplement(expected, actual).isEmpty());

      tc.reset();

      actual = tc.getTestUnits(ci);
      Assert.assertTrue(actual.isEmpty());
   }

   private CoverageItem createCoverageItem(TestUnitCache tc) throws OseeCoreException {
      CoverageUnit parent = CoverageUnitFactory.createCoverageUnit(null, "Top", "C:/UserData/", null);
      CoverageItem ci1 = new CoverageItem(parent, CoverageOptionManager.Deactivated_Code, "1");
      ci1.setName("this is text");
      return CoverageItem.createCoverageItem(parent, ci1.toXml(), CoverageOptionManagerDefault.instance(), tc);
   }
}
