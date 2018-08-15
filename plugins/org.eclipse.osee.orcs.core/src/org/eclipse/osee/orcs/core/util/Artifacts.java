/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.util;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * Utility methods for common tasks performed on Artifacts.
 *
 * @author Donald G. Dunne
 */
public class Artifacts {

   public static ArtifactToken getOrCreate(ArtifactToken artifactToken, ArtifactId parent, TransactionBuilder tx, OrcsApi orcsApi) {
      ArtifactToken art = getOrCreate(artifactToken, tx, orcsApi);
      tx.addChild(parent, art);
      return art;
   }

   public static ArtifactToken getOrCreate(ArtifactToken artifactToken, TransactionBuilder tx, OrcsApi orcsApi) {
      ArtifactToken art =
         orcsApi.getQueryFactory().fromBranch(tx.getBranch()).andId(artifactToken).getResults().getAtMostOneOrNull();
      if (art == null) {
         art = tx.createArtifact(artifactToken);
      }
      return art;
   }

   public static ArtifactToken get(ArtifactToken productsFolder, BranchId branch, OrcsApi orcsApi) {
      return orcsApi.getQueryFactory().fromBranch(branch).andId(productsFolder).getArtifactOrNull();
   }

}
