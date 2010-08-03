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

package org.eclipse.osee.framework.ui.skynet.test.artifact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.DefaultBasicArtifact;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.access.AccessDataQuery;
import org.eclipse.osee.framework.core.services.IAccessControlService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPromptChange;
import org.eclipse.osee.framework.ui.skynet.artifact.prompt.IHandlePromptChange;
import org.eclipse.osee.framework.ui.skynet.artifact.prompt.IPromptFactory;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link ArtifactPromptChange}
 * 
 * @author Jeff C. Phillips
 */
public class ArtifactPromptChangeTest {
   private static IAttributeType TEST_ATTRIBUTE_TYPE = CoreAttributeTypes.NAME;

   @Test
   public void test() {
      boolean persist = true;
      List<Artifact> artifacts = new ArrayList<Artifact>();
      IBasicArtifact<?> user = new DefaultBasicArtifact(1, "1", "one");

      IAccessControlService accessService = new MockAccessControlService();

      Assert.assertFalse(ArtifactPromptChange.promptChangeAttribute(user, accessService, new TestPromptFactory(
         CoreAttributeTypes.Annotation, persist), TEST_ATTRIBUTE_TYPE, artifacts));

      Assert.assertTrue(ArtifactPromptChange.promptChangeAttribute(user, accessService, new TestPromptFactory(
         TEST_ATTRIBUTE_TYPE, persist), TEST_ATTRIBUTE_TYPE, artifacts));
   }

   private static class MockAccessControlService implements IAccessControlService {

      @SuppressWarnings("unused")
      @Override
      public boolean hasPermission(Object object, PermissionEnum permission) throws OseeCoreException {
         return true;
      }

      @SuppressWarnings("unused")
      @Override
      public void removePermissions(IOseeBranch branch) throws OseeCoreException {
         boolean removePermission = true;
      }

      @SuppressWarnings("unused")
      @Override
      public AccessDataQuery getAccessData(IBasicArtifact<?> userArtifact, Collection<?> itemsToCheck) throws OseeCoreException {
         return new AccessDataQuery(null);
      }

   }

   private static class TestPromptFactory implements IPromptFactory {
      private final IAttributeType attributeType;
      private final boolean persist;

      public TestPromptFactory(IAttributeType attributeType, boolean persist) {
         super();
         this.attributeType = attributeType;
         this.persist = persist;
      }

      @SuppressWarnings("unused")
      @Override
      public IHandlePromptChange createPrompt() throws OseeCoreException, UnsupportedOperationException {
         return new TestPromptChange(attributeType, persist);
      }
   }
   private static class TestPromptChange implements IHandlePromptChange {
      private final IAttributeType attributeType;
      private final boolean persist;

      public TestPromptChange(IAttributeType attributeType, boolean persist) {
         super();
         this.attributeType = attributeType;
         this.persist = persist;
      }

      @SuppressWarnings("unused")
      @Override
      public boolean promptOk() throws OseeCoreException {
         return true;
      }

      @SuppressWarnings("unused")
      @Override
      public boolean store() throws OseeCoreException {
         return persist && attributeType.equals(TEST_ATTRIBUTE_TYPE);
      }
   }
}
