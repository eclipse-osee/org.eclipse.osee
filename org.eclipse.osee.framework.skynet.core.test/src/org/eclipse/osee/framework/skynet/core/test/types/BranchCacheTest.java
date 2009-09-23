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
package org.eclipse.osee.framework.skynet.core.test.types;

import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;
import org.eclipse.osee.framework.skynet.core.types.BranchCache;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeFactory;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Low-level OseeTypeCache Test - Does not require database access
 * 
 * @author Roberto E. Escobar
 */
public class BranchCacheTest extends AbstractOseeCacheTest<Branch> {

   private static List<Branch> branchData;
   private static BranchCache cache;
   private static IOseeTypeFactory factory;

   @BeforeClass
   public static void prepareTestData() throws OseeCoreException {
      factory = new OseeTypeFactory();
      branchData = new ArrayList<Branch>();

      BranchDataAccessor branchAccessor = new BranchDataAccessor(branchData);
      cache = new BranchCache(factory, branchAccessor);

      cache.ensurePopulated();
      Assert.assertTrue(branchAccessor.wasLoaded());
   }

   public BranchCacheTest() {
      super(branchData, cache);
   }

   @Test
   public void testSystemRootBranch() throws OseeCoreException {
      Branch systemRootBranch = cache.getSystemRootBranch();
      Assert.assertNotNull(systemRootBranch);
   }

   @Test
   public void testBranchHierarchy() throws OseeCoreException {
      OseeTypesUtil.checkHierarchy(cache, "AAA", "BBB", "CCC");
      OseeTypesUtil.checkHierarchy(cache, "BBB", "DDD", "EEE");
      OseeTypesUtil.checkHierarchy(cache, "CCC", "FFF", "GGG", "HHH");
   }

   @Test
   public void testMergeBranches() throws OseeCoreException {
   }

   @Override
   public void testDirty() throws OseeCoreException {
      //      AttributeType attributeType = OseeTypesUtil.createAttributeType(attrCache, factory, "GUID", "AttributeDirtyTest");
      //      Assert.assertTrue(attributeType.isDirty());
      //      attributeType.clearDirty();
      //
      //      String initialValue = attributeType.getName();
      //      attributeType.setName("My Name Has Changes");
      //      Assert.assertTrue(attributeType.isDirty());
      //
      //      // Remains Dirty
      //      attributeType.setName(initialValue);
      //      Assert.assertTrue(attributeType.isDirty());
      //
      //      //      attributeType.setFields(name, baseAttributeTypeId, attributeProviderNameId, baseAttributeClass,
      //      //            providerAttributeClass, fileTypeExtension, defaultValue, oseeEnumType, minOccurrences, maxOccurrences,
      //      //            description, taggerId);

   }

   private final static class BranchDataAccessor extends OseeTestDataAccessor<Branch> {

      private final List<Branch> data;

      public BranchDataAccessor(List<Branch> data) {
         super();
         this.data = data;
      }

      @Override
      public void load(AbstractOseeCache<Branch> cache, IOseeTypeFactory factory) throws OseeCoreException {
         super.load(cache, factory);
         data.add(OseeTypesUtil.createBranch(cache, factory, "AAA", "Root", -1, BranchType.SYSTEM_ROOT,
               BranchState.CREATED, false));
         data.add(OseeTypesUtil.createBranch(cache, factory, "BBB", "B-Branch", -1, BranchType.BASELINE,
               BranchState.CREATED, false));
         data.add(OseeTypesUtil.createBranch(cache, factory, "CCC", "C-Branch", -1, BranchType.BASELINE,
               BranchState.MODIFIED, false));

         data.add(OseeTypesUtil.createBranch(cache, factory, "DDD", "D-Branch", -1, BranchType.WORKING,
               BranchState.MODIFIED, false));
         data.add(OseeTypesUtil.createBranch(cache, factory, "EEE", "E-Branch", -1, BranchType.WORKING,
               BranchState.MODIFIED, false));

         data.add(OseeTypesUtil.createBranch(cache, factory, "FFF", "F-Branch", -1, BranchType.WORKING,
               BranchState.MODIFIED, false));
         data.add(OseeTypesUtil.createBranch(cache, factory, "GGG", "G-Branch", -1, BranchType.WORKING,
               BranchState.MODIFIED, true));
         data.add(OseeTypesUtil.createBranch(cache, factory, "HHH", "H-Branch", -1, BranchType.WORKING,
               BranchState.MODIFIED, true));
         int typeId = 500;
         for (Branch type : data) {
            type.setId(typeId++);
            cache.cacheType(type);
         }
         BranchCache branchCache = (BranchCache) cache;
         loadBranchHierarchy(branchCache);
         loadMergeBranches(branchCache);
      }

      private void loadBranchHierarchy(BranchCache cache) throws OseeCoreException {
         OseeTypesUtil.createBranchHierarchy(cache, "AAA", "BBB", "CCC");
         OseeTypesUtil.createBranchHierarchy(cache, "BBB", "DDD", "EEE");
         OseeTypesUtil.createBranchHierarchy(cache, "CCC", "FFF", "GGG", "HHH");
      }

      private void loadMergeBranches(BranchCache branchCache) {

      }
   }
}
