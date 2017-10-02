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

package org.eclipse.osee.framework.ui.skynet.artifact;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Annotation;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Name;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.access.PermissionStatus;
import org.eclipse.osee.framework.skynet.core.AccessPolicy;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
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

   @Test
   public void test() {
      boolean persist = true;
      List<Artifact> artifacts = new ArrayList<>();

      MockPromptFactory MockPromptFactory = new MockPromptFactory();
      AccessPolicy policyHandler = new MockAccessPolicyHandler();
      MockPromptFactory.createPrompt(Annotation, "", artifacts, persist, false);

      ArtifactPrompt artifactPromptChange = new ArtifactPrompt(MockPromptFactory, policyHandler);

      Assert.assertFalse(artifactPromptChange.promptChangeAttribute(Annotation, artifacts, persist, false));
      Assert.assertTrue(artifactPromptChange.promptChangeAttribute(Name, artifacts, persist, false));
   }

   private static class MockAccessPolicyHandler implements AccessPolicy {

      @Override
      public PermissionStatus hasArtifactTypePermission(BranchId branch, Collection<? extends ArtifactTypeId> artifactTypes, PermissionEnum permission, Level level) {
         return new PermissionStatus();
      }

      @Override
      public boolean isReadOnly(Artifact artifact) {
         return false;
      }

      @Override
      public PermissionStatus hasBranchPermission(BranchId branch, PermissionEnum permission, Level level) {
         return new PermissionStatus();
      }

      @Override
      public PermissionStatus hasAttributeTypePermission(Collection<? extends ArtifactToken> artifacts, AttributeTypeId attributeType, PermissionEnum permission, Level level) {
         return new PermissionStatus();
      }

      @Override
      public PermissionStatus hasArtifactPermission(Collection<Artifact> artifacts, PermissionEnum permission, Level level) {
         return new PermissionStatus();
      }

      @Override
      public PermissionStatus canRelationBeModified(Artifact subject, Collection<Artifact> toBeRelated, RelationTypeSide relationTypeSide, Level level) {
         return new PermissionStatus();
      }

      @Override
      public void removePermissions(BranchId branch) {
         //
      }

   }

   private static class MockPromptFactory implements IPromptFactory {
      @Override
      public IHandlePromptChange createPrompt(AttributeTypeId attributeType, String displayName, Collection<? extends Artifact> artifacts, boolean persist, boolean multiLine) {
         return new TestPromptChange(attributeType, persist);
      }
   }
   private static class TestPromptChange implements IHandlePromptChange {
      private final AttributeTypeId attributeType;
      private final boolean persist;

      public TestPromptChange(AttributeTypeId attributeType, boolean persist) {
         super();
         this.attributeType = attributeType;
         this.persist = persist;
      }

      @Override
      public boolean promptOk() {
         return true;
      }

      @Override
      public boolean store() {
         return persist && attributeType.equals(Name);
      }
   }
}
