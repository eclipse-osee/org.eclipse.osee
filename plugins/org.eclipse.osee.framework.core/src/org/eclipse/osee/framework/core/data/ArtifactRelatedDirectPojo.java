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

/**
 * @author Jaden W. Puckett
 */
public class ArtifactRelatedDirectPojo {
   private final ArtifactTypeToken token;
   private final ArtifactReadable artReadable;
   private final BranchId branchId;
   private final OrcsTokenService tokenService = new OrcsTokenServiceImpl();

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
      return tokenService.getArtifactType(this.token.getId()).toString();
   }

   public List<PojoRelation> getRelations() {
      List<PojoRelation> relations = new ArrayList<>();
      // get the list of all valid relation types for the overall artifact we are finding the direct relations for
      List<RelationTypeToken> validRelationTypes = tokenService.getValidRelationTypes(token);

      for (RelationTypeToken rel : validRelationTypes) {
         // relation object to place into relations list
         PojoRelation pojoRelation = new PojoRelation(rel);

         // iterate over the result sets and populate the relation object with artifacts (dividing into collections for side A and side B)
         this.artReadable.getRelated(new RelationTypeSide(rel, RelationSide.SIDE_A)).forEach(artReadA -> {
            pojoRelation.addArtifactSideA(new PojoArtifact(artReadA));
         });
         this.artReadable.getRelated(new RelationTypeSide(rel, RelationSide.SIDE_B)).forEach(artReadB -> {
            pojoRelation.addArtifactSideB(new PojoArtifact(artReadB));
         });

         //add pojoRelation to relations list
         relations.add(pojoRelation);
      }

      return relations;
   }

   public ArtifactRelatedDirectPojo(ArtifactTypeToken token, ArtifactReadable artReadable, BranchId branchId) {
      this.token = token;
      this.artReadable = artReadable;
      this.branchId = branchId;
   }

   public class PojoRelationSide {
      private String name = "";
      private final List<PojoArtifact> artifacts = new ArrayList<>();
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
      private final List<PojoRelationSide> relationSides = new ArrayList<>();

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
      private final ArtifactReadable art;
      private final List<PojoAttribute> attributes = new ArrayList<>();

      public PojoArtifact(ArtifactReadable art) {
         this.art = art;
         List<IAttribute<?>> attrs = art.getAttributesHashCollection().getValues();
         List<PojoAttribute> pojoAttributes =
            attrs.stream().filter(attr -> attr != null).map(attr -> new PojoAttribute(attr)).collect(
               Collectors.toList());
         this.attributes.addAll(pojoAttributes);
      }

      public List<PojoAttribute> getAttributes() {
         return this.attributes;
      }

      public String getId() {
         return this.art.getIdString();
      }

      public String getName() {
         return this.art.getName();
      }

      public String getTypeId() {
         return this.art.getArtifactType().getIdString();
      }

      public String getTypeName() {
         return this.art.getArtifactType().getName();
      }
   }

   public class PojoAttribute {
      private final IAttribute<?> attr;

      public PojoAttribute(IAttribute<?> attr) {
         this.attr = attr;
      }

      public String getBaseType() {
         if (attr.getAttributeType().isArtifactId()) {
            return "ART_ID";
         }
         if (attr.getAttributeType().isBoolean()) {
            return "BOOLEAN";
         }
         if (attr.getAttributeType().isBranchId()) {
            return "BRANCH_ID";
         }
         if (attr.getAttributeType().isDate()) {
            return "DATE";
         }
         if (attr.getAttributeType().isDouble()) {
            return "DOUBLE";
         }
         if (attr.getAttributeType().isEnumerated()) {
            return "ENUM";
         }
         if (attr.getAttributeType().isInputStream()) {
            return "STREAM";
         }
         if (attr.getAttributeType().isInteger()) {
            return "INTEGER";
         }
         if (attr.getAttributeType().isLong()) {
            return "LONG";
         }
         if (attr.getAttributeType().isString()) {
            return "STRING";
         }
         if (attr.getAttributeType().isUri()) {
            return "URI";
         }
         if (attr.getAttributeType().isJavaObject()) {
            return "JAVA_OBJECT";
         }
         return "COMPLEX";
      }

      public String getName() {
         return this.attr.getAttributeType().getName();
      }

      public Object getValue() {
         return this.attr.getValue();
      }

      public String getTypeId() {
         return this.attr.getAttributeType().getIdString();
      }

      public String getId() {
         return this.attr.getIdString();
      }
   }
}
