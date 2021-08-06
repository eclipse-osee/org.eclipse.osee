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

package org.eclipse.osee.framework.skynet.core.artifact.search;

import org.eclipse.osee.framework.core.client.QueryBuilder;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author John Misinco
 */
public interface QueryBuilderArtifact extends QueryBuilder {

   ResultSet<Artifact> getResults();

   ResultSet<ArtifactMatch> getMatches();

}