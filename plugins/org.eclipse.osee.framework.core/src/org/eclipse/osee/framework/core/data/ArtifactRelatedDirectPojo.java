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
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

public class ArtifactRelatedDirectPojo {
   private ArtifactTypeToken token;
   private List<ArtifactReadable> artReadables;
   private final OrcsTokenService tokenService = new OrcsTokenServiceImpl();

   public String getArtId() {
      return artReadables.stream().map(a -> a.getIdString()).collect(Collectors.joining());
   }

   public String getArtName() {
      return artReadables.stream().map(a -> a.getName()).collect(Collectors.joining());
   }

   public String getArtType() {
      return tokenService.getArtifactType(this.token.getId()).toString();
   }

   public List<PojoRelation> getRelations() {
      List<PojoRelation> relations = new ArrayList<>();
      // get the list of all valid relation types for the overall artifact we are finding the direct relations for
      List<RelationTypeToken> validRelationTypes = tokenService.getValidRelationTypes(token);
      // artReadables.length should = 1
      for (ArtifactReadable artRead : artReadables) {

         for (RelationTypeToken rel : validRelationTypes) {
            // relation object to place into relations list
            PojoRelation pojoRelation = new PojoRelation(rel);

            // get relation resultsets for side A and side B for the current overall artifact readable
            RelationTypeSide rtsA = new RelationTypeSide(rel, RelationSide.SIDE_A);
            RelationTypeSide rtsB = new RelationTypeSide(rel, RelationSide.SIDE_B);
            ResultSet<ArtifactReadable> resultSetA = artRead.getRelated(rtsA);
            ResultSet<ArtifactReadable> resultSetB = artRead.getRelated(rtsB);

            // iterate over the result sets and populate the relation object with artifacts (dividing into collections for side A and side B)
            resultSetA.forEach(artReadA -> {
               PojoArtifact newArt = new PojoArtifact(artReadA.getIdString(), artReadA.getName(),
                  artReadA.getArtifactType().getIdString());
               pojoRelation.addArtifactSideA(newArt);
            });
            resultSetB.forEach(artReadB -> {
               PojoArtifact newArt = new PojoArtifact(artReadB.getIdString(), artReadB.getName(),
                  artReadB.getArtifactType().getIdString());
               pojoRelation.addArtifactSideB(newArt);
            });

            //add pojoRelation to relations list
            relations.add(pojoRelation);
         }
      }
      return relations;
   }

   public ArtifactRelatedDirectPojo(ArtifactTypeToken token, List<ArtifactReadable> artReadables) {
      this.token = token;
      this.artReadables = artReadables;
   }

   public class PojoRelationSide {
      private String name = "";
      private List<PojoArtifact> artifacts = new ArrayList<>();
      private Boolean isSideA = false;
      private Boolean isSideB = false;

      public PojoRelationSide(String name, Boolean isSideA, Boolean isSideB) {
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

      public List<PojoArtifact> getArtifacts() {
         return this.artifacts;
      }

      public void addArtifact(PojoArtifact art) {
         this.artifacts.add(art);
      }
   }

   public class PojoRelation {
      private RelationTypeToken relationTypeToken = null;
      private PojoRelationSide sideA = null;
      private PojoRelationSide sideB = null;
      private List<PojoRelationSide> relationSides = new ArrayList<>();

      public PojoRelation(RelationTypeToken rel) {
         this.relationTypeToken = rel;
         this.sideA = new PojoRelationSide(this.relationTypeToken.getSideName(RelationSide.SIDE_A), true, false);
         this.sideB = new PojoRelationSide(this.relationTypeToken.getSideName(RelationSide.SIDE_B), false, true);
      }

      public List<PojoRelationSide> getRelationSides() {
         this.relationSides.add(this.sideA);
         this.relationSides.add(this.sideB);
         return this.relationSides;
      }

      public void addArtifactSideA(PojoArtifact art) {
         this.sideA.addArtifact(art);
      }

      public void addArtifactSideB(PojoArtifact art) {
         this.sideB.addArtifact(art);
      }

      public RelationTypeToken getRelationTypeToken() {
         return this.relationTypeToken;
      }
   }

   public class PojoArtifact {
      private String id = "";
      private String name = "";
      private String type = "";

      public PojoArtifact(String id, String name, String type) {
         this.id = id;
         this.name = name;
         this.type = type;
      }

      public String getId() {
         return this.id;
      }

      public void setId(String id) {
         this.id = id;
      }

      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public String getType() {
         return type;
      }

      public void setType(String type) {
         this.type = type;
      }
   }
}
