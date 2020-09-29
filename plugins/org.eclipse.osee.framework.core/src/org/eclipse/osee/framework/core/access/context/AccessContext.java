/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.access.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.AccessContextToken;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.token.QualificationMethodAttributeType;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.util.Collections;

public class AccessContext extends NamedIdBase {

   private String guid;
   private List<AccessType> typeAccess = new ArrayList<AccessType>();
   private final AccessContextToken accessToken;
   private static final Map<AccessContextToken, AccessContext> accessContexts = new HashMap<>();

   public AccessContext(AccessContextToken accessToken) {
      super(accessToken.getId(), accessToken.getName());
      this.accessToken = accessToken;
      accessContexts.put(accessToken, this);
   }

   public static AccessContext getAccessContext(AccessContextToken accessToken) {
      return accessContexts.get(accessToken);
   }

   public String getGuid() {
      return guid;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public List<AccessType> getTypeAccess() {
      return typeAccess;
   }

   public void setTypeAccess(List<AccessType> typeAccess) {
      this.typeAccess = typeAccess;
   }

   public void allowEditArtifactType(ArtifactTypeToken... artifactTypes) {
      for (ArtifactTypeToken artType : artifactTypes) {
         typeAccess.add(new ArtifactTypeAccessType(AllowDeny.Allow, artType));
      }
   }

   public void denyEditArtifactType(ArtifactTypeToken... artifactTypes) {
      for (ArtifactTypeToken artType : artifactTypes) {
         typeAccess.add(new ArtifactTypeAccessType(AllowDeny.Deny, artType));
      }
   }

   public void denyEditArtifactAndChildrenArtifactTypes(ArtifactToken artifact, ArtifactTypeToken... childArtTypes) {
      typeAccess.add(
         new ArtifactAndChildrenArtifactTypesAccessType(AllowDeny.Deny, artifact, Collections.asHashSet(childArtTypes)));
   }

   public void allowEditArtifactAndChildrenArtifactTypes(ArtifactToken artifact, ArtifactTypeToken... childArtTypes) {
      typeAccess.add(
         new ArtifactAndChildrenArtifactTypesAccessType(AllowDeny.Allow, artifact, Collections.asHashSet(childArtTypes)));
   }

   /**
    * Deny children of artifact where relation type equals and art type on both sides
    */
   public void denyEditArtifactAndChildrenRelationTypes(ArtifactToken artifact, RelationTypeToken relationType, ArtifactTypeToken artAandBType) {
      typeAccess.add(new ArtifactAndChildrenRelationTypesAccessType(AllowDeny.Deny, artifact, relationType, artAandBType));
   }

   /**
    * Allow children of artifact where relation type equals and art type on both sides
    */
   public void allowEditArtifactAndChildrenRelationTypes(ArtifactToken artifact, RelationTypeToken relationType, ArtifactTypeToken artAandBType) {
      typeAccess.add(new ArtifactAndChildrenRelationTypesAccessType(AllowDeny.Allow, artifact, relationType, artAandBType));
   }

   public void allowEditRelationType(RelationTypeToken relationType) {
      typeAccess.add(new RelationTypeAccessType(AllowDeny.Allow, relationType, CoreArtifactTypes.Artifact));
   }

   public void allowEditRelationTypeOfArtifactType(RelationTypeToken relationType, ArtifactTypeToken artifactType) {
      typeAccess.add(new RelationTypeAccessType(AllowDeny.Allow, relationType, artifactType));
   }

   public void allowEditAttributeTypeOfArtifactType(QualificationMethodAttributeType attributeType, ArtifactTypeToken artifactType) {
      typeAccess.add(new AttributeTypeAccessType(AllowDeny.Allow, attributeType, artifactType));
   }

   public void allowEditArtifactAndChildrenRelationTypes(ArtifactToken artifact, RelationTypeToken relationType) {
      typeAccess.add(
         new ArtifactAndChildrenRelationTypesAccessType(AllowDeny.Allow, artifact, relationType, CoreArtifactTypes.Artifact));
   }

   public AccessContextToken getAccessToken() {
      return accessToken;
   }

}
