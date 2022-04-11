/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.framework.skynet.core.event.model;

import java.util.List;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.event.FrameworkEvent;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Torin Grenda
 */
public class EventTopicAttributeChangeTransfer implements FrameworkEvent {
   private AttributeTypeId attrTypeId;
   private Long modType;
   private AttributeId attrId;
   private GammaId gammaId;
   private String dataContent;
   private String dataLocator;
   private ApplicabilityId applicabilityId;

   public AttributeTypeId getAttrTypeId() {
      return attrTypeId;
   }

   public void setAttrTypeId(AttributeTypeId attrTypeId) {
      this.attrTypeId = attrTypeId;
   }

   public Long getModType() {
      return modType;
   }

   public void setModType(Long modType) {
      this.modType = modType;
   }

   public AttributeId getAttrId() {
      return attrId;
   }

   public void setAttrId(AttributeId attrId) {
      this.attrId = attrId;
   }

   public GammaId getGammaId() {
      return gammaId;
   }

   public void setGammaId(GammaId gammaId) {
      this.gammaId = gammaId;
   }

   public String getDataContent() {
      return dataContent;
   }

   public void setDataContent(String dataContent) {
      this.dataContent = dataContent;
   }

   public String getDataLocator() {
      return dataLocator;
   }

   public void setDataLocator(String dataLocator) {
      this.dataLocator = dataLocator;
   }

   public ApplicabilityId getApplicabilityId() {
      return applicabilityId;
   }

   public void setApplicabilityId(ApplicabilityId applicabilityId) {
      this.applicabilityId = applicabilityId;
   }

   public void setData(List<Object> objects) {
      if (objects == null) {
         return;
      }
      if (objects.size() == 2) {
         if (objects.get(0) == null) {
            setDataContent("");
         } else if (objects.get(0) instanceof String) {
            setDataContent((String) objects.get(0));
         }
         if (objects.get(1) == null) {
            setDataLocator("");
         } else if (objects.get(1) instanceof String) {
            setDataLocator((String) objects.get(1));
         }
      } else {
         throw new OseeArgumentException("Data for Attribute Change incorrect");
      }
   }

   @Override
   public String toString() {
      try {
         return String.format("[AttrTypeId%s - MT:%s - ID:%s - GI:%s - Data:%s - ApplicabilityId:%s]",
            getAttrTypeId().getIdString(), getModType().toString(), getAttrId().getIdString(),
            getGammaId().getIdString(), getDataContent().toString(), getApplicabilityId().getIdString());
      } catch (Exception ex) {
         return String.format("EventTopicAttributeChange exception: " + ex.getLocalizedMessage());
      }
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (getAttrTypeId() == null ? 0 : getAttrTypeId().hashCode());
      result = prime * result + (getModType() == null ? 0 : getModType().hashCode());
      result = prime * result + (getAttrId() == null ? 0 : getAttrId().hashCode());
      result = prime * result + (getGammaId() == null ? 0 : getGammaId().hashCode());
      result = prime * result + (getDataContent() == null ? 0 : getDataContent().hashCode());
      result = prime * result + (getDataLocator() == null ? 0 : getDataLocator().hashCode());
      result = prime * result + (getApplicabilityId() == null ? 0 : getApplicabilityId().hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (getClass() != obj.getClass()) {
         return false;
      }
      EventTopicAttributeChangeTransfer other = (EventTopicAttributeChangeTransfer) obj;
      if (getAttrTypeId() == null) {
         if (other.getAttrTypeId() != null) {
            return false;
         }
      } else if (!getAttrTypeId().equals(other.getAttrTypeId())) {
         return false;
      }

      if (getModType() == null) {
         if (other.getModType() != null) {
            return false;
         }
      } else if (!getModType().equals(other.getModType())) {
         return false;
      }

      if (getAttrId() == null) {
         if (other.getAttrId() != null) {
            return false;
         }
      } else if (!getAttrId().equals(other.getAttrId())) {
         return false;
      }

      if (getGammaId() == null) {
         if (other.getGammaId() != null) {
            return false;
         }
      } else if (!getGammaId().equals(other.getGammaId())) {
         return false;
      }

      if (getDataContent() == null) {
         if (other.getDataContent() != null) {
            return false;
         }
      } else if (!getDataContent().equals(other.getDataContent())) {
         return false;
      }

      if (getDataLocator() == null) {
         if (other.getDataLocator() != null) {
            return false;
         }
      } else if (!getDataLocator().equals(other.getDataLocator())) {
         return false;
      }
      if (getApplicabilityId() == null) {
         if (other.getApplicabilityId() != null) {
            return false;
         }
      } else if (!getApplicabilityId().equals(other.getApplicabilityId())) {
         return false;
      }
      return true;
   }
}
