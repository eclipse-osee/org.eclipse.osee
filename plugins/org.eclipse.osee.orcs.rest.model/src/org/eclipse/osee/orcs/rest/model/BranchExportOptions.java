/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.model;

import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class BranchExportOptions {

   private List<Long> branchUuids;
   private long minTx = -1L;
   private long maxTx = -1L;
   private String filename;
   private boolean compress;

   public List<Long> getBranchUuids() {
      return branchUuids != null ? branchUuids : Collections.<Long> emptyList();
   }

   public long getMinTx() {
      return minTx;
   }

   public long getMaxTx() {
      return maxTx;
   }

   public String getFileName() {
      return filename;
   }

   public boolean isCompress() {
      return compress;
   }

   public void setBranchUuids(List<Long> branchUuids) {
      this.branchUuids = branchUuids;
   }

   public void setMinTx(long minTx) {
      this.minTx = minTx;
   }

   public void setMaxTx(long maxTx) {
      this.maxTx = maxTx;
   }

   public void setFileName(String filename) {
      this.filename = filename;
   }

   public void setCompress(boolean compress) {
      this.compress = compress;
   }

}
