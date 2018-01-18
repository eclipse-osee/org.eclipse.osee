/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Angel Avila
 */
@XmlRootElement
public class Discrepancy {

   private String id;
   private String text;
   private String location;

   public Discrepancy() {

   }

   public String getId() {
      return id;
   }

   public String getText() {
      return text;
   }

   public String getLocation() {
      return location;
   }

   // Setters
   public void setId(String id) {
      this.id = id;
   }

   public void setText(String text) {
      this.text = text;
   }

   public void setLocation(String location) {
      this.location = location;
   }

}
