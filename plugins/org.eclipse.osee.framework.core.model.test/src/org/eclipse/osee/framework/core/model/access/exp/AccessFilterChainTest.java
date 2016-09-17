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
package org.eclipse.osee.framework.core.model.access.exp;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link AccessFilterChain}
 *
 * @author Jeff C. Phillips
 */
public class AccessFilterChainTest {

   private final ArtifactToken basicArtifact = ArtifactToken.valueOf(1, "123", COMMON);

   @Test
   public void testUseCase() {
      //Can we edit this attribute on an artifact
      AccessFilterChain chain = new AccessFilterChain();
      IAttributeType attributeType = CoreAttributeTypes.WordTemplateContent;

      BranchAccessFilter branchAccessFilter = new BranchAccessFilter(basicArtifact, PermissionEnum.READ);
      ArtifactAccessFilter artifactAccessFilter = new ArtifactAccessFilter(basicArtifact, PermissionEnum.WRITE);
      AttributeTypeAccessFilter attributeTypeAccessFilter =
         new AttributeTypeAccessFilter(PermissionEnum.DENY, basicArtifact, attributeType);

      chain.add(artifactAccessFilter);
      chain.add(branchAccessFilter);
      chain.add(attributeTypeAccessFilter);

      PermissionEnum agrPermission = null;
      Assert.assertFalse(chain.doFilter(basicArtifact, attributeTypeAccessFilter, PermissionEnum.WRITE, agrPermission));
      Assert.assertTrue(chain.doFilter(basicArtifact, basicArtifact, PermissionEnum.WRITE, agrPermission));
   }

   @Test
   public void testWrongArtifactUseCase() {
      AccessFilterChain chain = new AccessFilterChain();
      ArtifactToken basicArtifact2 = ArtifactToken.valueOf(2, "456", COMMON);
      IAttributeType attributeType = CoreAttributeTypes.WordTemplateContent;

      BranchAccessFilter branchAccessFilter = new BranchAccessFilter(basicArtifact, PermissionEnum.READ);
      ArtifactAccessFilter artifactAccessFilter = new ArtifactAccessFilter(basicArtifact, PermissionEnum.WRITE);
      AttributeTypeAccessFilter attributeTypeAccessFilter =
         new AttributeTypeAccessFilter(PermissionEnum.DENY, basicArtifact, attributeType);

      chain.add(artifactAccessFilter);
      chain.add(branchAccessFilter);
      chain.add(attributeTypeAccessFilter);

      PermissionEnum agrPermission = null;
      Assert.assertFalse(
         chain.doFilter(basicArtifact2, attributeTypeAccessFilter, PermissionEnum.WRITE, agrPermission));
   }

   @Test
   public void testChain() {
      AccessFilterChain chain = new AccessFilterChain();
      BranchAccessFilter branchAccessFilter = new BranchAccessFilter(basicArtifact, PermissionEnum.READ);
      ArtifactAccessFilter artifactAccessFilter = new ArtifactAccessFilter(basicArtifact, PermissionEnum.WRITE);

      chain.add(artifactAccessFilter);
      chain.add(branchAccessFilter);

      PermissionEnum agrPermission = null;
      Assert.assertTrue(chain.doFilter(basicArtifact, basicArtifact, PermissionEnum.READ, agrPermission));
      Assert.assertTrue(chain.doFilter(basicArtifact, basicArtifact, PermissionEnum.WRITE, agrPermission));
   }

   @Test
   public void testChainDeny() {
      AccessFilterChain chain = new AccessFilterChain();
      BranchAccessFilter branchAccessFilter = new BranchAccessFilter(basicArtifact, PermissionEnum.DENY);
      ArtifactAccessFilter artifactAccessFilter = new ArtifactAccessFilter(basicArtifact, PermissionEnum.WRITE);

      chain.add(artifactAccessFilter);
      chain.add(branchAccessFilter);

      PermissionEnum agrPermission = null;
      Assert.assertFalse(chain.doFilter(basicArtifact, basicArtifact, PermissionEnum.READ, agrPermission));
      Assert.assertFalse(chain.doFilter(basicArtifact, basicArtifact, PermissionEnum.WRITE, agrPermission));
   }
}
