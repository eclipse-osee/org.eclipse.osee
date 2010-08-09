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
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.access.PermissionStatus;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.AccessPolicyHandler;
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
   private static IAttributeType TEST_ATTRIBUTE_TYPE = CoreAttributeTypes.Name;

   @Test
   public void test() throws OseeCoreException {
      boolean persist = true;
      List<Artifact> artifacts = new ArrayList<Artifact>();

      MockPromptFactory MockPromptFactory = new MockPromptFactory();
      MockAccessPolicyHandler policyHandler = new MockAccessPolicyHandler();
      MockPromptFactory.createPrompt(CoreAttributeTypes.Annotation, "", artifacts, persist, false);

      ArtifactPromptChange artifactPromptChange = new ArtifactPromptChange();

      Assert.assertFalse(ArtifactPromptChange.promptChangeAttribute(CoreAttributeTypes.Annotation, artifacts, persist,
         false));
      Assert.assertTrue(ArtifactPromptChange.promptChangeAttribute(TEST_ATTRIBUTE_TYPE, artifacts, persist, false));
   }

   private static class MockAccessPolicyHandler extends AccessPolicyHandler {

      public MockAccessPolicyHandler() {
         super(null, null);
      }

      @SuppressWarnings("unused")
      @Override
      public PermissionStatus hasAttributeTypePermission(Collection<? extends IBasicArtifact<?>> artifacts, IAttributeType attributeType, PermissionEnum permission, Level level) throws OseeCoreException {
         return new PermissionStatus();
      }

   }

   private static class MockPromptFactory implements IPromptFactory {
      @SuppressWarnings("unused")
      @Override
      public IHandlePromptChange createPrompt(IAttributeType attributeType, String displayName, Collection<? extends Artifact> artifacts, boolean persist, boolean multiLine) throws OseeCoreException {
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
