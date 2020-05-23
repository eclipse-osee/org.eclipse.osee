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

package org.eclipse.osee.framework.skynet.core.change;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.TransactionDelta;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactChangeBuilder extends ChangeBuilder {

   public ArtifactChangeBuilder(BranchId branch, ArtifactTypeId artifactType, GammaId sourceGamma, ArtifactId artId, TransactionDelta txDelta, ModificationType modType, boolean isHistorical) {
      super(branch, artifactType, sourceGamma, artId, txDelta, modType, isHistorical);
   }
}