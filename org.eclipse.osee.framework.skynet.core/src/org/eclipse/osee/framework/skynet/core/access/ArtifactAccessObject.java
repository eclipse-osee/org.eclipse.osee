/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.access;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactAccessObject extends AccessObject {

   private Integer artId;
   private Integer branchId;

   public ArtifactAccessObject(Integer artId, Integer branchId) {
      super();
      this.artId = artId;
      this.branchId = branchId;
   }

   /**
    * @return Returns the artId.
    */
   public Integer getArtId() {
      return artId;
   }

   /**
    * @return Returns the branchId.
    */
   public Integer getBranchId() {
      return branchId;
   }
}
