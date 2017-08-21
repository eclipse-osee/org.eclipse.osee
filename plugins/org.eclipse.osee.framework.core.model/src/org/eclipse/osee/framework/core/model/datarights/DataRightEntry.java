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
package org.eclipse.osee.framework.core.model.datarights;

import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.core.util.PageOrientation;

/**
 * @author Angel Avila
 */

@XmlRootElement
public class DataRightEntry {
   private Long id;
   private String classification;
   private PageOrientation orientation;
   private int index;

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
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

   public int getIndex() {
      return index;
   }

   public void setIndex(int index) {
      this.index = index;
   }
}
