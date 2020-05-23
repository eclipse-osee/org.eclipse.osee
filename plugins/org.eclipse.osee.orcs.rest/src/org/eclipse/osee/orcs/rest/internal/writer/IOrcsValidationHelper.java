/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.orcs.rest.internal.writer;

import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Donald G. Dunne
 */
public interface IOrcsValidationHelper {

   boolean isBranchExists(BranchId branch);

   boolean isUserExists(String userId);

   boolean isArtifactTypeExist(long artifactTypeUuid);

   boolean isRelationTypeExist(long relationTypeUuid);

   boolean isAttributeTypeExists(long attributeTypeUuid);

   public boolean isArtifactExists(BranchId branch, long artifactUuid);

   boolean isApplicabilityExist(BranchId branch, String value);

}