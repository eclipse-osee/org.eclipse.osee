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

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.DefaultBasicArtifact;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.access.AccessData;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Test;

/**
 * Test Case for {@link AccessData}
 *
 * @author Jeff C. Phillips
 */
public class AccessDataTest {

   @Test
   public void testAddingData() throws OseeCoreException {
      IOseeBranch branchToCheck = CoreBranches.SYSTEM_ROOT;
      IArtifactType artifactType = CoreArtifactTypes.AbstractSoftwareRequirement;
      IAttributeType attributeType = CoreAttributeTypes.PARAGRAPH_NUMBER;
      IAttributeType wordAttributeType = CoreAttributeTypes.WORD_TEMPLATE_CONTENT;

      IBasicArtifact<?> artifactToCheck = new DefaultBasicArtifact(12, GUID.create(), "Hello");

      AccessData data = new AccessData();
      data.add(branchToCheck, new AccessDetail<IOseeBranch>(branchToCheck, PermissionEnum.WRITE));

      data.add(artifactToCheck, new AccessDetail<IBasicArtifact<?>>(artifactToCheck, PermissionEnum.WRITE));
      data.add(artifactToCheck, new AccessDetail<IArtifactType>(artifactType, PermissionEnum.WRITE));
      data.add(artifactToCheck, new AccessDetail<IAttributeType>(attributeType, PermissionEnum.WRITE));
      data.add(artifactToCheck, new AccessDetail<IAttributeType>(wordAttributeType, PermissionEnum.READ));
   }
}
