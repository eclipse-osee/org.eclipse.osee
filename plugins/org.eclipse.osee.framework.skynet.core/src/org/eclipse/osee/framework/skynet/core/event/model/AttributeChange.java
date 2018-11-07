/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.event.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.messaging.event.res.AttributeEventModificationType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;

/**
 * <p>
 * Java class for AttributeChange complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="AttributeChange">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="attrTypeGuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="modTypeGuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="attributeId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="gammaId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="data" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Jeff C. Phillips
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AttributeChange", propOrder = {"attrTypeGuid", "modTypeGuid", "attributeId", "gammaId", "data"})
public class AttributeChange implements FrameworkEvent {
   private static final Object[] emptyArray = new Object[0];

   @XmlElement(required = true)
   protected Long attrTypeGuid;
   @XmlElement(required = true)
   protected String modTypeGuid;
   protected int attributeId;
   protected GammaId gammaId;
   @XmlElement(required = true)
   protected List<Object> data;
   @XmlElement(required = true)
   protected ApplicabilityId applicabilityId;

   public Long getAttrTypeGuid() {
      return attrTypeGuid;
   }

   public void setAttrTypeGuid(Long value) {
      this.attrTypeGuid = value;
   }

   public String getModTypeGuid() {
      return modTypeGuid;
   }

   public void setModTypeGuid(String value) {
      this.modTypeGuid = value;
   }

   public int getAttributeId() {
      return attributeId;
   }

   public void setAttributeId(int value) {
      this.attributeId = value;
   }

   public GammaId getGammaId() {
      return gammaId;
   }

   public void setApplicabilityId(ApplicabilityId applicabilityId) {
      this.applicabilityId = applicabilityId;
   }

   public ApplicabilityId getApplicabilityId() {
      return applicabilityId;
   }

   public void setGammaId(GammaId gammaId) {
      this.gammaId = gammaId;
   }

   /**
    * Gets the value of the data property.
    * <p>
    * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
    * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
    * the data property.
    * <p>
    * For example, to add a new item, do as follows:
    *
    * <pre>
    * getData().add(newItem);
    * </pre>
    * <p>
    * Objects of the following type(s) are allowed in the list {@link String }
    */
   public List<Object> getData() {
      if (data == null) {
         data = new ArrayList<>();
      }
      return this.data;
   }

   public Object[] getDataArray() {
      if (data == null) {
         return emptyArray;
      }
      return data.toArray();
   }

   @Override
   public String toString() {
      try {
         return String.format("[AttrChg: %s - %s - %s]", AttributeEventModificationType.getType(modTypeGuid),
            AttributeTypeManager.getTypeById(attrTypeGuid), data);
      } catch (OseeCoreException ex) {
         return "Exception: " + ex.getLocalizedMessage();
      }
   }
}
