/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link ArtifactPasteConfiguration}.
 * 
 * @author Roberto E. Escobar
 */
public class ArtifactPasteConfigurationTest {

   @Test
   public void testConstruction() {
      ArtifactPasteConfiguration config = new ArtifactPasteConfiguration();
      Assert.assertFalse(config.isIncludeChildrenOfCopiedElements());
      Assert.assertTrue(config.isKeepRelationOrderSettings());
   }

   @Test
   public void testSetIncludeChildren() {
      ArtifactPasteConfiguration config = new ArtifactPasteConfiguration();
      config.setIncludeChildrenOfCopiedElements(true);
      Assert.assertTrue(config.isIncludeChildrenOfCopiedElements());

      config.setIncludeChildrenOfCopiedElements(false);
      Assert.assertFalse(config.isIncludeChildrenOfCopiedElements());
   }

   @Test
   public void testSetKeepRelationOrder() {
      ArtifactPasteConfiguration config = new ArtifactPasteConfiguration();
      config.setKeepRelationOrderSettings(true);
      Assert.assertTrue(config.isKeepRelationOrderSettings());

      config.setKeepRelationOrderSettings(false);
      Assert.assertFalse(config.isKeepRelationOrderSettings());
   }
}
