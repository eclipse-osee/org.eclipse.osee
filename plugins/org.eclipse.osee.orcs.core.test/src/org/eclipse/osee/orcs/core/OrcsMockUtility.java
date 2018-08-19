/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Name;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.RelationOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.ArtifactDataImpl;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.core.ds.VersionDataImpl;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.artifact.ArtifactImpl;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.mockito.Matchers;

/**
 * @author Ryan D. Brooks
 */
public class OrcsMockUtility {

   public static Artifact createTestArtifact(GraphData graph, BranchId branch, IArtifactType artifactType, Long artifactId, String name) {
      Artifact artifact = createTestArtifact(branch, artifactType, artifactId, name);
      artifact.setGraph(graph);
      return artifact;
   }

   public static Artifact createTestArtifact(BranchId branch, IArtifactType artifactType, Long artifactId, String name) {
      AttributeFactory attributeFactory = mock(AttributeFactory.class);
      ArtifactTypes artifactTypeCache = mock(ArtifactTypes.class);

      VersionData version = new VersionDataImpl();
      version.setBranch(branch);

      ArtifactData artifactData = new ArtifactDataImpl(version);
      artifactData.setType(artifactType);
      artifactData.setLocalId(artifactId.intValue());
      artifactData.setModType(ModificationType.NEW);

      when(artifactTypeCache.get(artifactType.getId())).thenReturn(artifactType);
      Artifact artifact = new ArtifactImpl(artifactTypeCache, artifactData, attributeFactory);

      when(attributeFactory.getMaxOccurrenceLimit(Matchers.any(AttributeTypeId.class))).thenReturn(1);

      addAttribute(artifact, Name, name);
      addAttribute(artifact, RelationOrder, "");
      artifact.getBranch();
      return artifact;
   }

   public static void addAttribute(Artifact artifact, AttributeTypeToken attributeType, String value) {
      Attribute<Object> attribute = mock(Attribute.class);
      AttributeData attributeData = mock(AttributeData.class);
      when(attribute.getOrcsData()).thenReturn(attributeData);
      when(attribute.getValue()).thenReturn(value);
      artifact.add(attributeType, attribute);
   }
}