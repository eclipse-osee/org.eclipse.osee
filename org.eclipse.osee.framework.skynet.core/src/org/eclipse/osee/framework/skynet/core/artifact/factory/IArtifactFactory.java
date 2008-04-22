/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.artifact.factory;

import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;

/**
 * Defines the necessary methods for being an artifact factory.
 * 
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public interface IArtifactFactory {

   public abstract Artifact makeNewArtifact(Branch branch, ArtifactSubtypeDescriptor descriptor) throws SQLException;

   public abstract Artifact makeNewArtifact(Branch branch, ArtifactSubtypeDescriptor descriptor, String guid, String humandReadableId) throws SQLException;

   public abstract int getFactoryId();

   public abstract Artifact getNewArtifact(String guid, String humandReadableId, String factoryKey, Branch branch) throws SQLException;
}
