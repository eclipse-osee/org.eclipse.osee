/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds;

import static org.junit.Assert.assertEquals;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Case for {@link OptionsUtil}
 * 
 * @author Roberto E. Escobar
 */
public class OptionsUtilTest {

   private Options options;

   @Before
   public void setup() {
      options = new Options();
   }

   @Test
   public void testDefaults() {
      Options defaults = OptionsUtil.createOptions();

      assertEquals(-1, OptionsUtil.getFromTransaction(defaults));
      assertEquals(DeletionFlag.EXCLUDE_DELETED, OptionsUtil.getIncludeDeletedArtifacts(defaults));
      assertEquals(LoadLevel.ALL, OptionsUtil.getLoadLevel(defaults));
      assertEquals(false, OptionsUtil.areDeletedArtifactsIncluded(defaults));

      assertEquals(false, OptionsUtil.isCacheIncluded(defaults));
      assertEquals(true, OptionsUtil.isHeadTransaction(defaults));
      assertEquals(false, OptionsUtil.isHistorical(defaults));
   }

   @Test
   public void testSetGetLoadLevel() {
      assertEquals(LoadLevel.ARTIFACT_DATA, OptionsUtil.getLoadLevel(options));

      OptionsUtil.setLoadLevel(options, LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA);

      assertEquals(LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA, OptionsUtil.getLoadLevel(options));
   }

   @Test
   public void testSetGetFromTransaction() {
      assertEquals(-1, OptionsUtil.getFromTransaction(options));
      assertEquals(true, OptionsUtil.isHeadTransaction(options));
      assertEquals(false, OptionsUtil.isHistorical(options));

      OptionsUtil.setFromTransaction(options, 1231);

      assertEquals(1231, OptionsUtil.getFromTransaction(options));
      assertEquals(false, OptionsUtil.isHeadTransaction(options));
      assertEquals(true, OptionsUtil.isHistorical(options));

      OptionsUtil.setHeadTransaction(options);

      assertEquals(-1, OptionsUtil.getFromTransaction(options));
      assertEquals(true, OptionsUtil.isHeadTransaction(options));
      assertEquals(false, OptionsUtil.isHistorical(options));
   }

   @Test
   public void testSetGetIncludeDeleted() {
      assertEquals(DeletionFlag.EXCLUDE_DELETED, OptionsUtil.getIncludeDeletedArtifacts(options));
      assertEquals(false, OptionsUtil.areDeletedArtifactsIncluded(options));

      OptionsUtil.setIncludeDeletedArtifacts(options, true);

      assertEquals(DeletionFlag.INCLUDE_DELETED, OptionsUtil.getIncludeDeletedArtifacts(options));
      assertEquals(true, OptionsUtil.areDeletedArtifactsIncluded(options));
   }

   @Test
   public void testSetGetIncludeCache() {
      assertEquals(false, OptionsUtil.isCacheIncluded(options));

      OptionsUtil.setIncludeCache(options, true);

      assertEquals(true, OptionsUtil.isCacheIncluded(options));
   }

   @Test
   public void testReset() {
      OptionsUtil.setFromTransaction(options, 1231);
      OptionsUtil.setLoadLevel(options, LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA);
      OptionsUtil.setIncludeDeletedArtifacts(options, true);
      OptionsUtil.setIncludeCache(options, true);

      assertEquals(1231, OptionsUtil.getFromTransaction(options));
      assertEquals(DeletionFlag.INCLUDE_DELETED, OptionsUtil.getIncludeDeletedArtifacts(options));
      assertEquals(LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA, OptionsUtil.getLoadLevel(options));
      assertEquals(true, OptionsUtil.areDeletedArtifactsIncluded(options));
      assertEquals(true, OptionsUtil.isCacheIncluded(options));
      assertEquals(false, OptionsUtil.isHeadTransaction(options));
      assertEquals(true, OptionsUtil.isHistorical(options));

      OptionsUtil.reset(options);

      assertEquals(-1, OptionsUtil.getFromTransaction(options));
      assertEquals(DeletionFlag.EXCLUDE_DELETED, OptionsUtil.getIncludeDeletedArtifacts(options));
      assertEquals(LoadLevel.ALL, OptionsUtil.getLoadLevel(options));
      assertEquals(false, OptionsUtil.areDeletedArtifactsIncluded(options));
      assertEquals(false, OptionsUtil.isCacheIncluded(options));
      assertEquals(true, OptionsUtil.isHeadTransaction(options));
      assertEquals(false, OptionsUtil.isHistorical(options));
   }
}
