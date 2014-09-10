/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.report.api;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Angel Avila
 */

@XmlRootElement
public class DataRightEntry {
   private String guid;
   private String classification;
   private PageOrientation orientation;

   public String getGuid() {
      return guid;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public String getClassification() {
      return classification;
   }

   public void setClassification(String classification) {
      this.classification = classification;
   }

   public PageOrientation getOrientation() {
      return orientation;
   }

   public void setOrientation(PageOrientation orientation) {
      this.orientation = orientation;
   }
}
