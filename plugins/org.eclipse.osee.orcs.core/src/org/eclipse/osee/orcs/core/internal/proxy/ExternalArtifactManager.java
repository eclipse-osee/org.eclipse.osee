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
package org.eclipse.osee.orcs.core.internal.proxy;

import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.Attribute;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.relation.RelationNode;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;

/**
 * @author Megumi Telles
 */
public interface ExternalArtifactManager {

   ResultSet<ArtifactReadable> asExternalArtifacts(OrcsSession session, Iterable<? extends Artifact> artifacts);

   ResultSet<? extends RelationNode> asInternalArtifacts(Iterable<? extends ArtifactReadable> externals);

   Artifact asInternalArtifact(ArtifactReadable external);

   ArtifactReadable asExternalArtifact(OrcsSession session, Artifact artifact);

   <T> AttributeReadable<T> asExternalAttribute(OrcsSession session, Attribute<T> attribute);

   <T> ResultSet<AttributeReadable<T>> asExternalAttributes(OrcsSession session, Iterable<? extends Attribute<T>> attributes);

}