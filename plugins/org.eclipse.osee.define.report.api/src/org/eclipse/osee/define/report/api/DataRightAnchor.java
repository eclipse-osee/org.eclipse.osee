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
public class DataRightAnchor {

   private String id;
   private DataRightId dataRightId;
   private boolean needsPageBreak;
   private boolean isNextDifferent;

   public boolean isNextDifferent() {
      return isNextDifferent;
   }

   public void setNextDifferent(boolean isNextDifferent) {
      this.isNextDifferent = isNextDifferent;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public DataRightId getDataRightId() {
      return dataRightId;
   }

   public void setDataRightId(DataRightId rightId) {
      this.dataRightId = rightId;
   }

   public boolean isNeedsPageBreak() {
      return needsPageBreak;
   }

   public void setNeedsPageBreak(boolean needsPageBreak) {
      this.needsPageBreak = needsPageBreak;
   }

}
