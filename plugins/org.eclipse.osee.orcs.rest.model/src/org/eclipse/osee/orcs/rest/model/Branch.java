/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.model;

import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class Branch {

   private String uuid;
   private String name;
   private BranchType branchType;
   private BranchState branchState;
   private BranchArchivedState branchArchivedState;
   private Transaction baseTransaction;
   private Transaction sourceTransaction;

   public String getId() {
      return uuid;
   }

   public String getName() {
      return name;
   }

   public BranchType getBranchType() {
      return branchType;
   }

   public BranchState getBranchState() {
      return branchState;
   }

   public BranchArchivedState getArchiveState() {
      return branchArchivedState;
   }

   public Transaction getBaseTransaction() {
      return baseTransaction;
   }

   public Transaction getSourceTransaction() {
      return sourceTransaction;
   }

}
