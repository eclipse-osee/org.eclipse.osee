/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.define.rest.importing.resolvers;

import org.eclipse.osee.define.rest.api.importing.RoughArtifact;
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
