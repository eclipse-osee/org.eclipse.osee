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

import org.eclipse.osee.framework.core.model.access.AccessData;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test Case for {@link AccessData}
 * 
 * @author Jeff C. Phillips
 */
public class AccessDataTest {

	@Ignore
	@Test
	public void testObjectBase() {
	}

	//   @Test
	//   public void testObjectBase() {
	//      AccessData accessData = new AccessData();
	//      Assert.assertFalse(accessData.matchesAll(PermissionEnum.READ));
	//
	//      IBasicArtifact<?> basicArtifact2 = new DefaultBasicArtifact(2, "2", "Name2");
	//      accessData.add(basicArtifact2, PermissionEnum.WRITE);
	//      IBasicArtifact<?> basicArtifact = new DefaultBasicArtifact(1, "1", "Name");
	//      accessData.add(basicArtifact, PermissionEnum.READ);
	//
	//      Assert.assertTrue(accessData.matchesAll(PermissionEnum.READ));
	//      Assert.assertFalse(accessData.matchesAll(PermissionEnum.WRITE));
	//   }
	//
	//   @Test
	//   public void testObjectBaseDeny() {
	//      AccessData accessData = new AccessData();
	//      Assert.assertFalse(accessData.matchesAll(PermissionEnum.READ));
	//
	//      IBasicArtifact<?> basicArtifact2 = new DefaultBasicArtifact(2, "2", "Name2");
	//      accessData.add(basicArtifact2, PermissionEnum.DENY);
	//      IBasicArtifact<?> basicArtifact = new DefaultBasicArtifact(1, "1", "Name");
	//      accessData.add(basicArtifact, PermissionEnum.READ);
	//
	//      Assert.assertFalse(accessData.matchesAll(PermissionEnum.READ));
	//   }
	//
	//   @Test
	//   public void testAttributeTypeFilter() {
	//      AccessData accessData = new AccessData();
	//      Assert.assertFalse(accessData.matchesAll(PermissionEnum.READ));
	//
	//      IBasicArtifact<?> basicArtifact = new DefaultBasicArtifact(1, "1", "Name");
	//      IBasicArtifact<?> basicArtifact2 = new DefaultBasicArtifact(2, "2", "Name Two");
	//      accessData.add(basicArtifact, CoreAttributeTypes.WORD_TEMPLATE_CONTENT, PermissionEnum.READ);
	//      accessData.add(basicArtifact2, CoreAttributeTypes.WORD_TEMPLATE_CONTENT, PermissionEnum.WRITE);
	//
	//      Assert.assertTrue(!accessData.getAttributeTypeMatches(basicArtifact, CoreAttributeTypes.WORD_TEMPLATE_CONTENT,
	//            PermissionEnum.READ).isEmpty());
	//      Assert.assertTrue(accessData.getAttributeTypeMatches(basicArtifact, CoreAttributeTypes.WORD_TEMPLATE_CONTENT,
	//            PermissionEnum.WRITE).isEmpty());
	//      Assert.assertTrue(accessData.matchesAll(PermissionEnum.READ));
	//      Assert.assertFalse(accessData.matchesAll(PermissionEnum.WRITE));
	//   }
	//
	//   @Test
	//   public void testArtifactTypeFilter() {
	//      AccessData accessData = new AccessData();
	//      Assert.assertFalse(accessData.matchesAll(PermissionEnum.READ));
	//
	//      IBasicArtifact<?> basicArtifact = new DefaultBasicArtifact(1, "1", "Name");
	//      accessData.add(basicArtifact, CoreArtifactTypes.AbstractSoftwareRequirement, PermissionEnum.READ);
	//      Assert.assertTrue(!accessData.getArtifactTypeMatches(basicArtifact,
	//            CoreArtifactTypes.AbstractSoftwareRequirement, PermissionEnum.READ).isEmpty());
	//      Assert.assertTrue(accessData.getArtifactTypeMatches(basicArtifact, CoreArtifactTypes.AbstractSoftwareRequirement,
	//            PermissionEnum.WRITE).isEmpty());
	//   }
	//
	//   @Test
	//   public void testMerge() {
	//      AccessData mainAccessData = new AccessData();
	//      AccessData subAccessData = new AccessData();
	//
	//      IBasicArtifact<?> basicArtifact = new DefaultBasicArtifact(1, "1", "Name");
	//      mainAccessData.add(basicArtifact, CoreArtifactTypes.AbstractSoftwareRequirement, PermissionEnum.READ);
	//      subAccessData.add(basicArtifact, CoreArtifactTypes.AbstractSoftwareRequirement, PermissionEnum.WRITE);
	//      mainAccessData.merge(subAccessData);
	//
	//      Assert.assertTrue(!mainAccessData.getArtifactTypeMatches(basicArtifact,
	//            CoreArtifactTypes.AbstractSoftwareRequirement, PermissionEnum.READ).isEmpty());
	//
	//      IBasicArtifact<?> basicArtifact2 = new DefaultBasicArtifact(2, "2", "NameTwo");
	//      mainAccessData.add(basicArtifact2, CoreArtifactTypes.AbstractSoftwareRequirement, PermissionEnum.WRITE);
	//      subAccessData.add(basicArtifact2, CoreArtifactTypes.AbstractSoftwareRequirement, PermissionEnum.READ);
	//
	//      mainAccessData.merge(subAccessData);
	//
	//      Assert.assertTrue(!mainAccessData.getArtifactTypeMatches(basicArtifact2,
	//            CoreArtifactTypes.AbstractSoftwareRequirement, PermissionEnum.READ).isEmpty());
	//
	//      mainAccessData.add(basicArtifact2, CoreArtifactTypes.AbstractSoftwareRequirement, PermissionEnum.WRITE);
	//      subAccessData.add(basicArtifact2, CoreArtifactTypes.AbstractSoftwareRequirement, PermissionEnum.DENY);
	//
	//      mainAccessData.merge(subAccessData);
	//
	//      Assert.assertFalse(!mainAccessData.getArtifactTypeMatches(basicArtifact2,
	//            CoreArtifactTypes.AbstractSoftwareRequirement, PermissionEnum.READ).isEmpty());
	//   }

}
