/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.relation.crossbranch;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.IRelationEnumeration;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;

public class CrossBranchLink {

   public Artifact artifact;
   public DefaultBasicGuidArtifact guidArt;
   public IRelationEnumeration relationEnum;
   public boolean aSide;
   public Attribute<?> matchingAttribute;

   public CrossBranchLink(Attribute<?> attr) {
      this.matchingAttribute = attr;
      try {
         this.artifact = attr.getArtifact();
         unPack();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public CrossBranchLink(IRelationEnumeration relationEnum, Artifact artifact) {
      this.relationEnum = relationEnum;
      this.artifact = artifact;
      this.guidArt = artifact.getBasicGuidArtifact();
      this.aSide = relationEnum.getSide().isSideA();
   }

   public Attribute<?> getMatchingAttribute() {
      return matchingAttribute;
   }

   public void setMatchingAttribute(Attribute<?> matchingAttribute) {
      this.matchingAttribute = matchingAttribute;
   }

   public String toXml() throws OseeCoreException {
      RelationType relationType = RelationTypeManager.getType(relationEnum);
      StringBuffer sb = new StringBuffer(AXml.addTagData("relTypeGuid", relationType.getGuid()));
      sb.append(AXml.addTagData("artGuid", artifact.getGuid()));
      sb.append(AXml.addTagData("artTypeGuid", artifact.getArtTypeGuid()));
      sb.append(AXml.addTagData("branchGuid", artifact.getBranchGuid()));
      sb.append(AXml.addTagData("aSide", String.valueOf(relationEnum.getSide().isSideA())));
      return sb.toString();
   }

   public void store(Artifact onArtifact) throws OseeCoreException {
      onArtifact.addAttribute(CoreAttributeTypes.CrossBranchLink, toXml());
   }

   public void fromXml(String xmlStr) throws OseeCoreException {
      String artGuid = AXml.getTagData(xmlStr, "artGuid");
      String branchGuid = AXml.getTagData(xmlStr, "branchGuid");
      String artTypeGuid = AXml.getTagData(xmlStr, "artTypeGuid");
      String relTypeGuid = AXml.getTagData(xmlStr, "relTypeGuid");
      aSide = AXml.getTagBooleanData(xmlStr, "aSide");
      final RelationType relationType = RelationTypeManager.getTypeByGuid(relTypeGuid);
      relationEnum = new LoadedRelationTypes(relationType, aSide);
      guidArt = new DefaultBasicGuidArtifact(branchGuid, artTypeGuid, artGuid);
   }

   private void unPack() throws OseeCoreException {
      if (relationEnum == null) {
         String xmlStr = (String) matchingAttribute.getValue();
         fromXml(xmlStr);
      }
   }

   public RelationType getRelationType() throws OseeCoreException {
      unPack();
      return RelationTypeManager.getType(relationEnum);
   }

   public Artifact getArtifact() throws OseeCoreException {
      if (artifact != null) {
         unPack();
         artifact = ArtifactQuery.getArtifactFromToken(guidArt);
      }
      return artifact;
   }

   public void setArtifact(Artifact artifact) {
      this.artifact = artifact;
   }

   public IRelationEnumeration getRelationEnum() throws OseeCoreException {
      if (relationEnum == null) {
         unPack();
      }
      return relationEnum;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (aSide ? 1231 : 1237);
      result = prime * result + ((guidArt == null) ? 0 : guidArt.hashCode());
      result = prime * result + ((relationEnum == null) ? 0 : relationEnum.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      CrossBranchLink other = (CrossBranchLink) obj;
      if (aSide != other.aSide) {
         return false;
      }
      if (guidArt == null) {
         if (other.guidArt != null) {
            return false;
         }
      } else if (!guidArt.equals(other.guidArt)) {
         return false;
      }
      if (relationEnum == null) {
         if (other.relationEnum != null) {
            return false;
         }
      } else if (!relationEnum.equals(other.relationEnum)) {
         return false;
      }
      return true;
   }

   @Override
   public String toString() {
      return String.format("[%s - %s - %s]", relationEnum, aSide, guidArt);
   }
}
