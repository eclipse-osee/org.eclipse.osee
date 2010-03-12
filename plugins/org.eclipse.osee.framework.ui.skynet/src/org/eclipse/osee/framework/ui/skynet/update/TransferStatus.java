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
package org.eclipse.osee.framework.ui.skynet.update;

/**
 * @author Jeff C. Phillips
 */
public enum TransferStatus {

   REBASELINE("Replace artifact in baseline with new version"),
   REBASELINE_SOMEWHERE_ON_BRANCH("Artifact exist on branch - Revert artifact and update"),
   ADD_TO_BASELINE("Add artifact to baseline transaction"),
   INTRODUCE("Introduce this artifact to this branch in a new transaction"),
   UPDATE("Update this artifact in a new transaction"),
   ERROR("This artifact will not be updated");

   private String message;

   private TransferStatus(String message) {
      this.message = message;
   }

   public String getMessage() {
      return message;
   }

}
