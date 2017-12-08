/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.artifact;

import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.HasOrcsData;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeManager;
import org.eclipse.osee.orcs.core.internal.graph.GraphNode;
import org.eclipse.osee.orcs.core.internal.relation.order.OrderStore;
import org.eclipse.osee.orcs.core.internal.util.OrcsWriteable;
import org.eclipse.osee.orcs.data.HasTransaction;

/**
 * @author Megumi Telles
 */
public interface Artifact extends AttributeManager, HasTransaction, ArtifactVisitable, HasOrcsData<ArtifactData>, OrcsWriteable, GraphNode, OrderStore {

   TransactionId getLastModifiedTransaction();

   void setArtifactType(IArtifactType artifactType);

   void setName(String name);

   boolean isOfType(ArtifactTypeId... otherTypes);

   void setNotDirty();

}
