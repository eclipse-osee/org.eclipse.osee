/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.testscript.ats;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Stephen J. Molaro
 */
public interface AtsScriptTaskTrackingApi {

   void createFailureTasks(BranchId branch, ArtifactId ciSetId, ArtifactToken scriptToken, String scriptResultString);

   void setScriptTaskCompleted(BranchId branch, ArtifactId ciSetId, ArtifactToken scriptToken);

}
