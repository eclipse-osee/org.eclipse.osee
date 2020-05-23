/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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
