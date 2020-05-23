/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.define.api;

import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.datarights.DataRightResult;

/**
 * @author Ryan D. Brooks
 */
public interface DataRightsOperations {

   DataRightResult getDataRights(List<ArtifactId> artifacts, BranchId branch);

   DataRightResult getDataRights(List<ArtifactId> artifacts, BranchId branch, String overrideClassification);

}
