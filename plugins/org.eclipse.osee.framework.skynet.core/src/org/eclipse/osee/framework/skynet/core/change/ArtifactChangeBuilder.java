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
package org.eclipse.osee.framework.skynet.core.change;

import org.eclipse.osee.framework.core.data.TransactionDelta;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.Branch;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactChangeBuilder extends ChangeBuilder {

   /**
    * @param sourceGamma
    * @param artId
    * @param toTransactionId
    * @param fromTransactionId
    * @param artifact
    * @param modType
    * @param changeType
    * @param branch
    * @param artifactType
    * @param isHistorical
    */
   public ArtifactChangeBuilder(Branch branch, ArtifactType artifactType, int sourceGamma, int artId, TransactionDelta txDelta, ModificationType modType, boolean isHistorical) {
      super(branch, artifactType, sourceGamma, artId, txDelta, modType, isHistorical);
   }

   @Override
   public Change build(Branch branch) throws OseeDataStoreException, OseeTypeDoesNotExist, ArtifactDoesNotExist {
      return new ArtifactChange(branch, getArtifactType(), getSourceGamma(), getArtId(), getTxDelta(), getModType(),
            isHistorical(), new ArtifactDelta(loadArtifact(), null));
   }
}
