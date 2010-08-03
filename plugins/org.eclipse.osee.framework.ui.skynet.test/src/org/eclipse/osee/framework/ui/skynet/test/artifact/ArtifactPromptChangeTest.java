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
import org.eclipse.osee.framework.core.data.IOseeBranch;
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
   private static String ATTR_NAME = "attributeName";

   @Test
   public void test() {
      boolean persist = true;
      List<Artifact> artifacts = new ArrayList<Artifact>();
      IBasicArtifact<?> user = new DefaultBasicArtifact(1, "1", "one");

      Assert.assertFalse(ArtifactPromptChange.promptChangeAttribute(user, new MockAccessControlService(),
         new TestPromptFactory("attributeNam", persist), ATTR_NAME, artifacts));

      Assert.assertTrue(ArtifactPromptChange.promptChangeAttribute(user, new MockAccessControlService(),
         new TestPromptFactory(ATTR_NAME, persist), ATTR_NAME, artifacts));
   }

   private class MockAccessControlService implements IAccessControlService {

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

   private class TestPromptFactory implements IPromptFactory {
      private final String attributeName;
      private final boolean persist;

      public TestPromptFactory(String attributeName, boolean persist) {
         super();
         this.attributeName = attributeName;
         this.persist = persist;
      }

      @SuppressWarnings("unused")
      @Override
      public IHandlePromptChange createPrompt() throws OseeCoreException, UnsupportedOperationException {
         return new TestPromptChange(attributeName, persist);
      }
   }
   private class TestPromptChange implements IHandlePromptChange {
      private final String attributeName;
      private final boolean persist;

      public TestPromptChange(String attributeName, boolean persist) {
         super();
         this.attributeName = attributeName;
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
         return persist && attributeName.equals(ATTR_NAME);
      }
   }
}
