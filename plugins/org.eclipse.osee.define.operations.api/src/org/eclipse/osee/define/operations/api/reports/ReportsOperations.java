/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.define.operations.api.reports;

import java.util.List;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * Interface for server methods for producing Reports.
 * 
 * @author Loren K. Ashley
 */

public interface ReportsOperations {

   public StreamingOutput applicabilityImpact(BranchId branch, String publish, List<ArtifactTypeToken> artTypes, List<AttributeTypeToken> attrTypes);

}
