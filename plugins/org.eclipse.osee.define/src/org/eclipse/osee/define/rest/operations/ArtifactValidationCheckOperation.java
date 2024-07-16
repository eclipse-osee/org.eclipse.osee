/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.define.rest.operations;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author David W. Miller
 */
public class ArtifactValidationCheckOperation {
   private final List<ArtifactReadable> itemsToCheck;
   private final boolean stopOnFirstError;
   private final XResultData results;

   public ArtifactValidationCheckOperation(OrcsApi orcsApi, XResultData results, ArtifactReadable parentArtifact, boolean stopOnFirstError) {
      this.stopOnFirstError = stopOnFirstError;
      this.itemsToCheck = new ArrayList<>();
      itemsToCheck.add(parentArtifact);// performance problem here> parentArtifact.getDescendants();
      this.results = results;
   }

   public boolean isStopOnFirstError() {
      return stopOnFirstError;
   }

   public XResultData validate() {
      for (ArtifactReadable artifact : itemsToCheck) {
         boolean hasError = validateArt(artifact, results);
         if (isStopOnFirstError() && hasError) {
            break;
         }
      }
      return results;
   }

   private boolean validateArt(ArtifactReadable artifact, XResultData results) {
      return false; //TODO put the correct validation here
   }
}
