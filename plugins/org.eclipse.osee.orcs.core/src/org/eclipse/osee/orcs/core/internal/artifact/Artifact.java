/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.core.internal.artifact;

import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.HasTransaction;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.HasOrcsData;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeManager;
import org.eclipse.osee.orcs.core.internal.graph.GraphNode;
import org.eclipse.osee.orcs.core.internal.relation.order.OrderStore;
import org.eclipse.osee.orcs.core.internal.util.OrcsWriteable;

/**
 * @author Megumi Telles
 */
public interface Artifact extends AttributeManager, HasTransaction, ArtifactVisitable, HasOrcsData<ArtifactTypeToken, ArtifactData>, OrcsWriteable, GraphNode, OrderStore {

   TransactionId getLastModifiedTransaction();

   void setArtifactType(ArtifactTypeToken artifactType);

   void setName(String name);

   void setNotDirty();

   boolean isDeleteAllowed();
}