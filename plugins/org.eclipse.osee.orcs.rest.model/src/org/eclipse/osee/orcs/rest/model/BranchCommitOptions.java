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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Roberto E. Escobar
 */
@XmlRootElement
public class BranchCommitOptions {

   private static final int DEFAULT_COMMITTER_ID = -1;

   private int committerId = DEFAULT_COMMITTER_ID;
   private boolean archive;

   public int getCommitterId() {
      return committerId;
   }

   public void setCommitterId(int committerId) {
      this.committerId = committerId;
   }

   public boolean isArchive() {
      return archive;
   }

   public void setArchive(boolean archive) {
      this.archive = archive;
   }

}
