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
package org.eclipse.osee.ats.util.widgets;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.IArtifactEditorProvider;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Megumi Telles
 */
public class AtsArtifactEditorProvider implements IArtifactEditorProvider {

   @Override
   public void contributeToHeader(Artifact artifact, Composite composite) {
      if (artifact.isTypeEqual(AtsArtifactTypes.Version)) {
         new XBranchViewSelect(artifact, "Branch View").createControls(composite, 2);
      }
   }
}
