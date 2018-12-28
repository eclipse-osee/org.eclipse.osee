/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.artifact.editor;

import java.util.Collection;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;

/**
 * @author Megumi Telles
 */
public final class ArtifactEditorProviders {

   public static Collection<IArtifactEditorProvider> getXWidgetProviders() {
      ExtensionDefinedObjects<IArtifactEditorProvider> contributions =
         new ExtensionDefinedObjects<>(
            "org.eclipse.osee.framework.ui.skynet.ArtifactEditorProvider", "ArtifactEditorProvider", "classname", true);
      return contributions.getObjects();
   }

}
