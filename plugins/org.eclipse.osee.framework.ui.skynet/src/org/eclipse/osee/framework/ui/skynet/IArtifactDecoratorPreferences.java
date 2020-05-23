/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public interface IArtifactDecoratorPreferences {

   public String getSelectedAttributeData(Artifact artifact);

   public boolean showArtIds();

   public boolean showArtType();

   public boolean showArtBranch();

   public boolean showArtVersion();

   boolean showRelations();

}
