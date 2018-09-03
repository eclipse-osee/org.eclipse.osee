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