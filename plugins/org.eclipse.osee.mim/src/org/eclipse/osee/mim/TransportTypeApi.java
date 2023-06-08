/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.mim;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.types.TransportType;

/**
 * @author Luciano Vaglienti
 */
public interface TransportTypeApi extends QueryCapableMIMAPI<TransportType>, AffectedArtifactMIMAPI<TransportType> {

   Collection<TransportType> getAll(BranchId branch);

   Collection<TransportType> getAll(BranchId branch, AttributeTypeId orderByAttribute);

   Collection<TransportType> getAll(BranchId branch, long pageNum, long pageSize);

   Collection<TransportType> getAll(BranchId branch, long pageNum, long pageSize, AttributeTypeId orderByAttribute);

   TransportType get(BranchId branch, ArtifactId artId);

   TransportType getFromConnection(BranchId branch, ArtifactId connectionId);
}
