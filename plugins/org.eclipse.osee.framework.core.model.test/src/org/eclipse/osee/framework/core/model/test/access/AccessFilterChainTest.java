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
package org.eclipse.osee.framework.core.model.test.access;

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
   public void testUseCase() {
      //Can we edit this attribute on an artifact
      AccessFilterChain chain = new AccessFilterChain();
      IBasicArtifact<?> basicArtifact = new DefaultBasicArtifact(1, "1", "123");
      IAttributeType attributeType = CoreAttributeTypes.WORD_TEMPLATE_CONTENT;

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
      IBasicArtifact<?> basicArtifact = new DefaultBasicArtifact(1, "1", "123");
      IBasicArtifact<?> basicArtifact2 = new DefaultBasicArtifact(2, "2", "456");
      IAttributeType attributeType = CoreAttributeTypes.WORD_TEMPLATE_CONTENT;

      BranchAccessFilter branchAccessFilter = new BranchAccessFilter(basicArtifact, PermissionEnum.READ);
      ArtifactAccessFilter artifactAccessFilter = new ArtifactAccessFilter(basicArtifact, PermissionEnum.WRITE);
      AttributeTypeAccessFilter attributeTypeAccessFilter =
         new AttributeTypeAccessFilter(PermissionEnum.DENY, basicArtifact, attributeType);

      chain.add(artifactAccessFilter);
      chain.add(branchAccessFilter);
      chain.add(attributeTypeAccessFilter);

      PermissionEnum agrPermission = null;
      Assert.assertFalse(chain.doFilter(basicArtifact2, attributeTypeAccessFilter, PermissionEnum.WRITE, agrPermission));
   }

   @Test
   public void testChain() {
      AccessFilterChain chain = new AccessFilterChain();
      IBasicArtifact<?> basicArtifact = new DefaultBasicArtifact(1, "1", "123");

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
      IBasicArtifact<?> basicArtifact = new DefaultBasicArtifact(1, "1", "123");
      BranchAccessFilter branchAccessFilter = new BranchAccessFilter(basicArtifact, PermissionEnum.DENY);
      ArtifactAccessFilter artifactAccessFilter = new ArtifactAccessFilter(basicArtifact, PermissionEnum.WRITE);

      chain.add(artifactAccessFilter);
      chain.add(branchAccessFilter);

      PermissionEnum agrPermission = null;
      Assert.assertFalse(chain.doFilter(basicArtifact, basicArtifact, PermissionEnum.READ, agrPermission));
      Assert.assertFalse(chain.doFilter(basicArtifact, basicArtifact, PermissionEnum.WRITE, agrPermission));
   }
}
