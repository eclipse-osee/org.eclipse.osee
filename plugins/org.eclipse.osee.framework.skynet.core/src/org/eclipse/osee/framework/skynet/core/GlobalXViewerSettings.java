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

package org.eclipse.osee.framework.skynet.core;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
/**
 * This singleton artifact stores the default customizations for XViewers
 *
 * @author Donald G. Dunne
 */
public final class GlobalXViewerSettings {

   public static Artifact getCustomArtifact() throws OseeCoreException {
      return OseeSystemArtifacts.getCachedArtifact(CoreArtifactTypes.XViewerGlobalCustomization,
         CoreArtifactTypes.XViewerGlobalCustomization.getName(), COMMON);
   }

   public static Artifact createCustomArtifact() throws OseeCoreException {
      return ArtifactTypeManager.addArtifact(CoreArtifactTypes.XViewerGlobalCustomization,
         COMMON, CoreArtifactTypes.XViewerGlobalCustomization.getName());
   }
}