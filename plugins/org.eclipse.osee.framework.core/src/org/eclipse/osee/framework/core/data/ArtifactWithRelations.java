/*********************************************************************
 * Copyright (c) 2023 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.framework.core.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.enums.RelationSide;

/**
 * @author Jaden W. Puckett
 */
public class ArtifactWithRelations {
   private final ArtifactReadable artReadable;
   private final OrcsTokenService tokenService;
   private final List<AttributePojo<String>> attributes = new ArrayList<>();
   private List<ArtifactRelation> relations = Collections.emptyList();

   public ArtifactWithRelations(ArtifactReadable artReadable, OrcsTokenService tokenService, boolean includeRelations) {
      this(artReadable, tokenService, includeRelations, true);
   }

   public ArtifactWithRelations(ArtifactReadable artReadable, OrcsTokenService tokenService, boolean includeRelations, boolean includeAttributes) {
      this.artReadable = artReadable;
      this.tokenService = tokenService;

      if (includeAttributes) {
         ArtifactTypeToken artType = artReadable.getArtifactType();
         List<IAttribute<?>> attrs = artReadable.getAttributesHashCollection().getValues();
         List<AttributePojo<String>> pojoAttributes = attrs.stream().filter(attr -> attr != null).map(
            attr -> toStringPojo(attr, artType)).collect(Collectors.toList());
         this.attributes.addAll(pojoAttributes);
      }

      if (includeRelations) {
         this.relations = loadRelations();
      }
   }

   private static AttributePojo<String> toStringPojo(IAttribute<?> attr, ArtifactTypeToken artType) {
      Object value = attr.getValue();
      String stringValue;
      if (value == null) {
         stringValue = "";
      } else if (value instanceof String) {
         stringValue = (String) value;
      } else {
         try {
            Object nameValue = value.getClass().getMethod("getName").invoke(value);
            stringValue = nameValue != null ? nameValue.toString() : value.toString();
         } catch (Exception e) {
            stringValue = value.toString();
         }
      }
      Multiplicity multiplicity = artType.getMultiplicity(attr.getAttributeType());
      AttributePojo<String> pojo = new AttributePojo<>(attr.getId(), attr.getAttributeType(), attr.getGammaId(),
         stringValue, attr.getDisplayableString(), multiplicity);
      return pojo;
   }

   public List<AttributePojo<String>> getAttributes() {
      return this.attributes;
   }

   public ApplicabilityToken getApplicability() {
      return this.artReadable.getApplicabilityToken();
   }

   public String getId() {
      return this.artReadable.getIdString();
   }

   public String getGammaId() {
      return this.artReadable.getGamma().getIdString();
   }

   public String getName() {
      return this.artReadable.getName();
   }

   public String getTypeId() {
      return this.artReadable.getArtifactType().getIdString();
   }

   public String getTypeName() {
      return this.artReadable.getArtifactType().getName();
   }

   public MaterialIcon getIcon() {
      return this.artReadable.getArtifactType().getIcon();
   }

   public Set<OperationTypeToken> getOperationTypes() {
      return this.artReadable.getArtifactType().getOperationTypes();
   }

   @JsonIgnore
   public String getBranchId() {
      return this.artReadable.getBranchIdString();
   }

   public List<ArtifactRelation> getRelations() {
      return this.relations;
   }

   private List<ArtifactRelation> loadRelations() {
      List<ArtifactRelation> relations = new ArrayList<>();
      List<RelationTypeToken> validRelationTypes = tokenService.getValidRelationTypes(artReadable.getArtifactType());

      for (RelationTypeToken rel : validRelationTypes) {
         ArtifactRelation pojoRelation = new ArtifactRelation(rel);

         this.artReadable.getRelated(new RelationTypeSide(rel, RelationSide.SIDE_A)).forEach(artReadA -> {
            String gamma = artReadable.getRelationGamma(rel, RelationSide.SIDE_A, artReadA).getIdString();
            pojoRelation.addArtifactSideA(new ArtifactWithRelations(artReadA, tokenService, false), gamma);
         });
         this.artReadable.getRelated(new RelationTypeSide(rel, RelationSide.SIDE_B)).forEach(artReadB -> {
            String gamma = artReadable.getRelationGamma(rel, RelationSide.SIDE_B, artReadB).getIdString();
            pojoRelation.addArtifactSideB(new ArtifactWithRelations(artReadB, tokenService, false), gamma);
         });

         relations.add(pojoRelation);
      }

      return relations;
   }

   public class ArtifactRelationSide {
      private String name = "";
      private final List<ArtifactWithRelations> artifacts = new ArrayList<>();
      private final List<String> gammaIds = new ArrayList<>();
      private Boolean isSideA = false;
      private Boolean isSideB = false;

      public ArtifactRelationSide(String name, Boolean isSideA, Boolean isSideB) {
         this.name = name;
         this.isSideA = isSideA;
         this.isSideB = isSideB;
      }

      public Boolean getIsSideA() {
         return isSideA;
      }

      public Boolean getIsSideB() {
         return isSideB;
      }

      public String getName() {
         return this.name;
      }

      public List<ArtifactWithRelations> getArtifacts() {
         return this.artifacts;
      }

      public List<String> getGammaIds() {
         return this.gammaIds;
      }

      public void addArtifact(ArtifactWithRelations art, String gammaId) {
         this.artifacts.add(art);
         this.gammaIds.add(gammaId);
      }

      public void addArtifact(ArtifactWithRelations art) {
         this.artifacts.add(art);
         this.gammaIds.add("-1");
      }
   }

   public class ArtifactRelation {
      private RelationTypeToken relationTypeToken = null;
      private ArtifactRelationSide sideA = null;
      private ArtifactRelationSide sideB = null;
      private final List<ArtifactRelationSide> relationSides = new ArrayList<>();

      public ArtifactRelation(RelationTypeToken rel) {
         this.relationTypeToken = rel;
         this.sideA = new ArtifactRelationSide(this.relationTypeToken.getSideName(RelationSide.SIDE_A), true, false);
         this.sideB = new ArtifactRelationSide(this.relationTypeToken.getSideName(RelationSide.SIDE_B), false, true);
      }

      public List<ArtifactRelationSide> getRelationSides() {
         this.relationSides.add(this.sideA);
         this.relationSides.add(this.sideB);
         return this.relationSides;
      }

      public void addArtifactSideA(ArtifactWithRelations art, String gammaId) {
         this.sideA.addArtifact(art, gammaId);
      }

      public void addArtifactSideB(ArtifactWithRelations art, String gammaId) {
         this.sideB.addArtifact(art, gammaId);
      }

      public void addArtifactSideA(ArtifactWithRelations art) {
         this.sideA.addArtifact(art);
      }

      public void addArtifactSideB(ArtifactWithRelations art) {
         this.sideB.addArtifact(art);
      }

      public RelationTypeToken getRelationTypeToken() {
         return this.relationTypeToken;
      }
   }

}
