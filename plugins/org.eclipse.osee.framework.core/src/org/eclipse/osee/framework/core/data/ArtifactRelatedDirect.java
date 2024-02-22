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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.enums.RelationSide;

/**
 * @author Jaden W. Puckett
 */
public class ArtifactRelatedDirect {
   private final ArtifactTypeToken artTypeToken;
   private final ArtifactReadable artReadable;
   private final BranchId branchId;
   private final OrcsTokenService tokenService;

   public String getBranchId() {
      return this.branchId.getIdString();
   }

   public String getArtId() {
      return artReadable.getIdString();
   }

   public String getArtName() {
      return artReadable.getName();
   }

   public String getArtType() {
      return tokenService.getArtifactType(this.artTypeToken.getId()).toString();
   }

   public List<ArtifactRelatedDirectRelation> getRelations() {
      List<ArtifactRelatedDirectRelation> relations = new ArrayList<>();
      // get the list of all valid relation types for the overall artifact we are finding the direct relations for
      List<RelationTypeToken> validRelationTypes = tokenService.getValidRelationTypes(artTypeToken);

      for (RelationTypeToken rel : validRelationTypes) {
         // relation object to place into relations list
         ArtifactRelatedDirectRelation pojoRelation = new ArtifactRelatedDirectRelation(rel);

         // iterate over the result sets and populate the relation object with artifacts (dividing into collections for side A and side B)
         this.artReadable.getRelated(new RelationTypeSide(rel, RelationSide.SIDE_A)).forEach(artReadA -> {
            pojoRelation.addArtifactSideA(new ArtifactRelatedDirectArtifact(artReadA, tokenService));
         });
         this.artReadable.getRelated(new RelationTypeSide(rel, RelationSide.SIDE_B)).forEach(artReadB -> {
            pojoRelation.addArtifactSideB(new ArtifactRelatedDirectArtifact(artReadB, tokenService));
         });

         //add pojoRelation to relations list
         relations.add(pojoRelation);
      }

      return relations;
   }

   public ArtifactRelatedDirect(ArtifactTypeToken artTypeToken, ArtifactReadable artReadable, BranchId branchId, OrcsTokenService tokenService) {
      this.artTypeToken = artTypeToken;
      this.artReadable = artReadable;
      this.branchId = branchId;
      this.tokenService = tokenService;
   }

   public class ArtifactRelatedDirectRelationSide {
      private String name = "";
      private final List<ArtifactRelatedDirectArtifact> artifacts = new ArrayList<>();
      private Boolean isSideA = false;
      private Boolean isSideB = false;

      public ArtifactRelatedDirectRelationSide(String name, Boolean isSideA, Boolean isSideB) {
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

      public List<ArtifactRelatedDirectArtifact> getArtifacts() {
         return this.artifacts;
      }

      public void addArtifact(ArtifactRelatedDirectArtifact art) {
         this.artifacts.add(art);
      }
   }

   public class ArtifactRelatedDirectRelation {
      private RelationTypeToken relationTypeToken = null;
      private ArtifactRelatedDirectRelationSide sideA = null;
      private ArtifactRelatedDirectRelationSide sideB = null;
      private final List<ArtifactRelatedDirectRelationSide> relationSides = new ArrayList<>();

      public ArtifactRelatedDirectRelation(RelationTypeToken rel) {
         this.relationTypeToken = rel;
         this.sideA =
            new ArtifactRelatedDirectRelationSide(this.relationTypeToken.getSideName(RelationSide.SIDE_A), true, false);
         this.sideB =
            new ArtifactRelatedDirectRelationSide(this.relationTypeToken.getSideName(RelationSide.SIDE_B), false, true);
      }

      public List<ArtifactRelatedDirectRelationSide> getRelationSides() {
         this.relationSides.add(this.sideA);
         this.relationSides.add(this.sideB);
         return this.relationSides;
      }

      public void addArtifactSideA(ArtifactRelatedDirectArtifact art) {
         this.sideA.addArtifact(art);
      }

      public void addArtifactSideB(ArtifactRelatedDirectArtifact art) {
         this.sideB.addArtifact(art);
      }

      public RelationTypeToken getRelationTypeToken() {
         return this.relationTypeToken;
      }
   }

}
