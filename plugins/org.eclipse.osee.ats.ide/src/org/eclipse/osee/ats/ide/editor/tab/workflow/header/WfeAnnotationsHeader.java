/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.annotation.ArtifactAnnotationComposite;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class WfeAnnotationsHeader extends ArtifactAnnotationComposite {

   public WfeAnnotationsHeader(Composite parent, int style, IAtsWorkItem workItem, WorkflowEditor editor) {
      super(editor.getToolkit(), parent, style, (Artifact) workItem.getStoreObject());
   }

}
