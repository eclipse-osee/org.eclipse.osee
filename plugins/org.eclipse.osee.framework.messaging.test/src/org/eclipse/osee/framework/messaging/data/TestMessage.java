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
package org.eclipse.osee.framework.messaging.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for TestMessage complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TestMessage">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="anotherMessage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * @author Roberto E. Escobar
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TestMessage", propOrder = {"message", "anotherMessage"})
public class TestMessage {

   @XmlElement(required = true)
   protected String message;
   @XmlElement(required = true)
   protected String anotherMessage;

   /**
    * Gets the value of the message property.
    * 
    * @return possible object is {@link String }
    */
   public String getMessage() {
      return message;
   }

   /**
    * Sets the value of the message property.
    * 
    * @param value allowed object is {@link String }
    */
   public void setMessage(String value) {
      this.message = value;
   }

   /**
    * Gets the value of the anotherMessage property.
    * 
    * @return possible object is {@link String }
    */
   public String getAnotherMessage() {
      return anotherMessage;
   }

   /**
    * Sets the value of the anotherMessage property.
    * 
    * @param value allowed object is {@link String }
    */
   public void setAnotherMessage(String value) {
      this.anotherMessage = value;
   }

}
