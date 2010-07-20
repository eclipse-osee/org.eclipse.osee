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
package org.eclipse.osee.framework.access.test.internal;

import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.DefaultBasicArtifact;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.access.exp.AccessFilterChain;
import org.eclipse.osee.framework.core.model.access.exp.ArtifactAccessFilter;
import org.eclipse.osee.framework.core.model.access.exp.AttributeTypeAccessFilter;
import org.eclipse.osee.framework.core.model.access.exp.BranchAccessFilter;
import org.junit.Test;

/**
 * Test Case for {@link AccessFilterChain}
 *
 * @author Jeff C. Phillips
 */
public class AccessFilterChainTest {

   @Test
   public void testUseCase1() {
      //Can we edit this attribute on an artifact
      AccessFilterChain chain = new AccessFilterChain();
      IBasicArtifact<?> basicArtifact = new DefaultBasicArtifact(1, "1", "123");
      IAttributeType wordAttribute = CoreAttributeTypes.WORD_TEMPLATE_CONTENT;

      BranchAccessFilter branchAccessFilter = new BranchAccessFilter(PermissionEnum.READ);
      ArtifactAccessFilter artifactAccessFilter = new ArtifactAccessFilter(PermissionEnum.WRITE, basicArtifact);
      AttributeTypeAccessFilter attributeTypeAccessFilter =
         new AttributeTypeAccessFilter(PermissionEnum.WRITE, basicArtifact);

      chain.add(artifactAccessFilter);
      chain.add(branchAccessFilter);

      PermissionEnum agrPermission = null;
      Assert.assertTrue(chain.doFilter(basicArtifact, PermissionEnum.READ, agrPermission));
      Assert.assertTrue(chain.doFilter(basicArtifact, PermissionEnum.WRITE, agrPermission));
   }

   @Test
   public void testChain() {
      AccessFilterChain chain = new AccessFilterChain();
      IBasicArtifact<?> basicArtifact = new DefaultBasicArtifact(1, "1", "123");

      BranchAccessFilter branchAccessFilter = new BranchAccessFilter(PermissionEnum.READ);
      ArtifactAccessFilter artifactAccessFilter = new ArtifactAccessFilter(PermissionEnum.WRITE);
      AttributeTypeAccessFilter attributeTypeAccessFilter =
         new AttributeTypeAccessFilter(PermissionEnum.WRITE, basicArtifact);

      chain.add(artifactAccessFilter);
      chain.add(branchAccessFilter);

      PermissionEnum agrPermission = null;
      Assert.assertTrue(chain.doFilter(basicArtifact, PermissionEnum.READ, agrPermission));
      Assert.assertTrue(chain.doFilter(basicArtifact, PermissionEnum.WRITE, agrPermission));
   }

   @Test
   public void testChainDeny() {
      AccessFilterChain chain = new AccessFilterChain();
      BranchAccessFilter branchAccessFilter = new BranchAccessFilter(PermissionEnum.DENY);
      ArtifactAccessFilter artifactAccessFilter = new ArtifactAccessFilter(PermissionEnum.WRITE);

      chain.add(artifactAccessFilter);
      chain.add(branchAccessFilter);

      IBasicArtifact<?> basicArtifact = new DefaultBasicArtifact(1, "1", "123");
      PermissionEnum agrPermission = null;
      Assert.assertFalse(chain.doFilter(basicArtifact, PermissionEnum.READ, agrPermission));
      Assert.assertFalse(chain.doFilter(basicArtifact, PermissionEnum.WRITE, agrPermission));
   }
}
