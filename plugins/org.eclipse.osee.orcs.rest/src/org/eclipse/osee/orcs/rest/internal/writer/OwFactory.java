/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.writer;

/**
 * Donald G. Dunne
 */
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwArtifact;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwArtifactToken;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwArtifactType;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwAttribute;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwAttributeType;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwBranch;
import org.eclipse.osee.orcs.rest.model.writer.reader.OwRelationType;

public class OwFactory {

   public static OwArtifactType createArtifactType(ArtifactTypeToken artType) {
      String typeStr = String.format("[%s]-[%d]", artType.getName(), artType.getId());
      OwArtifactType type = new OwArtifactType(artType.getId(), artType.getName());
      type.setData(typeStr);
      return type;
   }

   public static OwAttributeType createAttributeType(AttributeTypeToken attrType) {
      String typeStr = String.format("[%s]-[%d]", attrType.getName(), attrType.getId());
      OwAttributeType type = new OwAttributeType(attrType.getId(), attrType.getName());
      type.setData(typeStr);
      return type;
   }

   public static OwArtifactToken createArtifactToken(ArtifactToken token) {
      return createArtifactToken(token.getName(), token.getId());
   }

   public static OwArtifactToken createArtifactToken(String name, long id) {
      String tokenStr = String.format("[%s]-[%d]", name, id);
      OwArtifactToken owToken = new OwArtifactToken(id, name);
      owToken.setData(tokenStr);
      return owToken;
   }

   public static OwRelationType createRelationType(RelationTypeToken relType, String sideName, boolean sideA) {
      String sideData =
         String.format("[%s]-[%s]-[Side %s]-[%s]", relType.getName(), sideName, sideA ? "A" : "B", relType.getId());
      OwRelationType owType = new OwRelationType(relType.getId(), relType.getName());
      owType.setData(sideData);
      owType.setSideA(sideA);
      return owType;
   }

   public static OwRelationType createRelationType(OrcsApi orcsApi, RelationTypeSide type) {
      String sideAName = orcsApi.getOrcsTypes().getRelationTypes().getSideAName(type);
      OwRelationType owType = OwFactory.createRelationType(type, sideAName, true);
      return owType;
   }

   public static OwArtifact createArtifact(ArtifactTypeToken artifactType, String name) {
      return createArtifact(artifactType, name, null);
   }

   public static OwArtifact createArtifact(ArtifactTypeToken artifactType, String name, Long id) {
      OwArtifact artifact = new OwArtifact(id, name);
      OwArtifactType owArtType = OwFactory.createArtifactType(artifactType);
      artifact.setType(owArtType);
      if (id == null) {
         id = Lib.generateArtifactIdAsInt();
         artifact.setId(id);
      }
      return artifact;
   }

   public static OwAttribute createAttribute(OwArtifact artifact, AttributeTypeToken attrType, Object... values) {
      OwAttribute attribute = new OwAttribute();
      attribute.setType(OwFactory.createAttributeType(attrType));
      for (Object obj : values) {
         attribute.getValues().add(obj);
      }
      artifact.getAttributes().add(attribute);
      return attribute;
   }

   public static OwBranch createBranchToken(IOseeBranch branch) {
      String tokenStr = String.format("[%s]-[%d]", branch.getName(), branch.getId());
      OwBranch owBranch = new OwBranch(branch.getId(), branch.getName());
      owBranch.setData(tokenStr);
      return owBranch;
   }
}
