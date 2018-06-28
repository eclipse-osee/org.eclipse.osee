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

/**
 * @author Angel Avila
 */
public class DataRightAnchor {

   private Long id;
   private DataRightId dataRightId;
   private boolean isSetDataRightFooter = false;
   private boolean isContinuous = false;

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public DataRightId getDataRightId() {
      return dataRightId;
   }

   public void setDataRightId(DataRightId rightId) {
      this.dataRightId = rightId;
   }

   public boolean isSetDataRightFooter() {
      return isSetDataRightFooter;
   }

   public void setSetDataRightFooter(boolean isSetDataRightFooter) {
      this.isSetDataRightFooter = isSetDataRightFooter;
   }

   public boolean isContinuous() {
      return isContinuous;
   }

   public void setContinuous(boolean isContinuous) {
      this.isContinuous = isContinuous;
   }

}
