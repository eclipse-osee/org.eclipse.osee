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
   private final List<ArtifactWithRelationsAttribute> attributes = new ArrayList<>();
   private List<ArtifactRelation> relations;

   public ArtifactWithRelations(ArtifactReadable artReadable, OrcsTokenService tokenService, boolean includeRelations) {
      this.artReadable = artReadable;
      this.tokenService = tokenService;

      List<IAttribute<?>> attrs = artReadable.getAttributesHashCollection().getValues();
      List<ArtifactWithRelationsAttribute> pojoAttributes = attrs.stream().filter(attr -> attr != null).map(
         attr -> new ArtifactWithRelationsAttribute(attr, artReadable.getArtifactType(), tokenService)).collect(
            Collectors.toList());
      this.attributes.addAll(pojoAttributes);

      if (includeRelations) {
         this.relations = loadRelations();
      } else {
         this.relations = Collections.emptyList();
      }
   }

   public List<ArtifactWithRelationsAttribute> getAttributes() {
      return this.attributes;
   }

   public String getId() {
      return this.artReadable.getIdString();
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
      // get the list of all valid relation types for the overall artifact we are finding the direct relations for
      List<RelationTypeToken> validRelationTypes = tokenService.getValidRelationTypes(artReadable.getArtifactType());

      for (RelationTypeToken rel : validRelationTypes) {
         // relation object to place into relations list
         ArtifactRelation pojoRelation = new ArtifactRelation(rel);

         // iterate over the result sets and populate the relation object with artifacts (dividing into collections for side A and side B)
         this.artReadable.getRelated(new RelationTypeSide(rel, RelationSide.SIDE_A)).forEach(artReadA -> {
            pojoRelation.addArtifactSideA(new ArtifactWithRelations(artReadA, tokenService, false));
         });
         this.artReadable.getRelated(new RelationTypeSide(rel, RelationSide.SIDE_B)).forEach(artReadB -> {
            pojoRelation.addArtifactSideB(new ArtifactWithRelations(artReadB, tokenService, false));
         });

         //add pojoRelation to relations list
         relations.add(pojoRelation);
      }

      return relations;
   }

   public class ArtifactRelationSide {
      private String name = "";
      private final List<ArtifactWithRelations> artifacts = new ArrayList<>();
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

      public void addArtifact(ArtifactWithRelations art) {
         this.artifacts.add(art);
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
