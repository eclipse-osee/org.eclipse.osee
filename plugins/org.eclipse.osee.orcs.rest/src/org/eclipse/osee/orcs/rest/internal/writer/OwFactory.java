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
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
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

   public static OwArtifactType createArtifactType(IArtifactType artType) {
      String typeStr = String.format("[%s]-[%d]", artType.getName(), artType.getGuid());
      OwArtifactType type = new OwArtifactType();
      type.setUuid(artType.getGuid());
      type.setName(artType.getName());
      type.setData(typeStr);
      return type;
   }

   public static OwAttributeType createAttributeType(IAttributeType attrType) {
      String typeStr = String.format("[%s]-[%d]", attrType.getName(), attrType.getGuid());
      OwAttributeType type = new OwAttributeType();
      type.setUuid(attrType.getGuid());
      type.setName(attrType.getName());
      type.setData(typeStr);
      return type;
   }

   public static OwArtifactToken createArtifactToken(ArtifactToken token) {
      return createArtifactToken(token.getName(), token.getUuid());
   }

   public static OwArtifactToken createArtifactToken(String name, long uuid) {
      String tokenStr = String.format("[%s]-[%d]", name, uuid);
      OwArtifactToken owToken = new OwArtifactToken();
      owToken.setUuid(uuid);
      owToken.setName(name);
      owToken.setData(tokenStr);
      return owToken;
   }

   public static OwRelationType createRelationType(IRelationType relType, String sideName, boolean sideA) {
      String sideData =
         String.format("[%s]-[%s]-[Side %s]-[%s]", relType.getName(), sideName, sideA ? "A" : "B", relType.getId());
      OwRelationType owType = new OwRelationType();
      owType.setUuid(relType.getId());
      owType.setName(relType.getName());
      owType.setData(sideData);
      owType.setSideA(sideA);
      return owType;
   }

   public static OwRelationType createRelationType(OrcsApi orcsApi, IRelationTypeSide type) {
      String sideAName = orcsApi.getOrcsTypes().getRelationTypes().getSideAName(type);
      OwRelationType owType = OwFactory.createRelationType(type, sideAName, true);
      return owType;
   }

   public static OwArtifact createArtifact(IArtifactType artifactType, String name) {
      return createArtifact(artifactType, name, null);
   }

   public static OwArtifact createArtifact(IArtifactType artifactType, String name, Long uuid) {
      OwArtifact artifact = new OwArtifact();
      OwArtifactType owArtType = OwFactory.createArtifactType(artifactType);
      artifact.setType(owArtType);
      if (uuid == null) {
         uuid = Lib.generateArtifactIdAsInt();
      }
      artifact.setUuid(uuid);
      artifact.setName(name);
      return artifact;
   }

   public static OwAttribute createAttribute(OwArtifact artifact, IAttributeType attrType, Object... values) {
      OwAttribute attribute = new OwAttribute();
      attribute.setType(OwFactory.createAttributeType(attrType));
      for (Object obj : values) {
         attribute.getValues().add(obj);
      }
      artifact.getAttributes().add(attribute);
      return attribute;
   }

   public static OwBranch createBranchToken(IOseeBranch branch) {
      String tokenStr = String.format("[%s]-[%d]", branch.getName(), branch.getUuid());
      OwBranch owBranch = new OwBranch();
      owBranch.setData(tokenStr);
      owBranch.setName(branch.getName());
      owBranch.setUuid(branch.getUuid());
      return owBranch;
   }
}
