/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.ide.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.ide.util.AtsEditor;
import org.eclipse.osee.ats.ide.world.search.MultipleIdSearchData;
import org.eclipse.osee.ats.ide.world.search.MultipleIdSearchOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenArtifactEditorById extends Action {

   public OpenArtifactEditorById() {
      this("Open Artifact Editor by ID(s)");
   }

   public OpenArtifactEditorById(String name) {
      super(name);
      setToolTipText(getText());
   }

   @Override
   public void run() {
      MultipleIdSearchData data = new MultipleIdSearchData(getText(), AtsEditor.ArtifactEditor);
      MultipleIdSearchOperation operation = new MultipleIdSearchOperation(data);
      Operations.executeAsJob(operation, true);
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.ARTIFACT_EDITOR);
   }

}
