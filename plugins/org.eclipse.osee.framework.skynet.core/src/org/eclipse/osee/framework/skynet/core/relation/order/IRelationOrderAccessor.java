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

package org.eclipse.osee.framework.skynet.core.relation.order;

import org.eclipse.osee.framework.core.model.event.DefaultBasicUuidRelationReorder;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public interface IRelationOrderAccessor {

   public void store(Artifact artifact, RelationOrderData orderData, DefaultBasicUuidRelationReorder reorderRecord);

   public void load(Artifact artifact, RelationOrderData orderData);
}
