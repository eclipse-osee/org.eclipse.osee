/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.importing.resolvers;

import org.eclipse.define.api.importing.RoughArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * Used during imports that ask for artifact re-use to resolve the Artifact to be used for a particular RoughArtifact
 *
 * @author Robert A. Fisher
 * @author David W. Miller
 */
public interface IArtifactImportResolver {

   public ArtifactId resolve(RoughArtifact roughArtifact, BranchId branch, ArtifactId realParent, ArtifactId root);
}
