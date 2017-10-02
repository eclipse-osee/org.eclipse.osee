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
package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public interface IArtifactDecoratorPreferences {

   public String getSelectedAttributeData(Artifact artifact) ;

   public boolean showArtIds();

   public boolean showArtType();

   public boolean showArtBranch();

   public boolean showArtVersion();

   boolean showRelations();

}
