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

package org.eclipse.osee.orcs.core.internal.graph;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.HasBranch;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.data.HasTransaction;

/**
 * @author Roberto E. Escobar
 */
public interface GraphData extends HasBranch, HasTransaction {

   <T extends GraphNode> T getNode(ArtifactId data);

   void addNode(GraphNode node, boolean useBackingData);

   <T extends GraphNode> T removeNode(ArtifactId node);

   <T extends GraphAdjacencies> T getAdjacencies(ArtifactId node);

   void addAdjacencies(ArtifactId node, GraphAdjacencies adjacencies);

   <T extends GraphAdjacencies> T removeAdjacencies(ArtifactId node);

   OrcsSession getSession();
}