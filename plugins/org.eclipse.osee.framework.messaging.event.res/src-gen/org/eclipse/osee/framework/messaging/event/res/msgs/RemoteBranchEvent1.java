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
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2012.09.14 at 05:06:42 PM MST
//

package org.eclipse.osee.framework.messaging.event.res.msgs;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.messaging.event.res.RemoteEvent;

/**
 * <p>
 * Java class for RemoteBranchEvent1 complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="RemoteBranchEvent1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eventTypeGuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="branchGuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="destinationBranchGuid" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="networkSender" type="{}RemoteNetworkSender1"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RemoteBranchEvent1", propOrder = {
   "eventTypeGuid",
   "branchGuid",
   "destinationBranchGuid",
   "networkSender"})
public class RemoteBranchEvent1 extends RemoteEvent {

   @XmlElement(required = true)
   protected String eventTypeGuid;
   @XmlElement(required = true)
   protected String branchGuid;
   protected String destinationBranchGuid;
   @XmlElement(required = true)
   protected RemoteNetworkSender1 networkSender;

   /**
    * Gets the value of the eventTypeGuid property.
    *
    * @return possible object is {@link String }
    */
   public String getEventTypeGuid() {
      return eventTypeGuid;
   }

   /**
    * Sets the value of the eventTypeGuid property.
    *
    * @param value allowed object is {@link String }
    */
   public void setEventTypeGuid(String value) {
      this.eventTypeGuid = value;
   }

   public String getBranchGuid() {
      return branchGuid;
   }

   public BranchId getBranch() {
      return BranchId.valueOf(branchGuid);
   }

   public void setBranch(BranchId banch) {
      this.branchGuid = banch.getIdString();
   }

   public String getDestinationBranchGuid() {
      return destinationBranchGuid;
   }

   public BranchId getDestinationBranch() {
      return Strings.isNumeric(destinationBranchGuid) ? BranchId.valueOf(destinationBranchGuid) : null;
   }

   public void setDestinationBranch(BranchId branch) {
      this.destinationBranchGuid = branch == null ? null : branch.getIdString();
   }

   /**
    * Gets the value of the networkSender property.
    *
    * @return possible object is {@link RemoteNetworkSender1 }
    */
   @Override
   public RemoteNetworkSender1 getNetworkSender() {
      return networkSender;
   }

   /**
    * Sets the value of the networkSender property.
    *
    * @param value allowed object is {@link RemoteNetworkSender1 }
    */
   public void setNetworkSender(RemoteNetworkSender1 value) {
      this.networkSender = value;
   }

}
