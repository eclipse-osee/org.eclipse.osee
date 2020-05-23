/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.workflow.goal;

import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeArtifactDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class GoalCheckTreeDialog extends FilteredCheckboxTreeArtifactDialog {

   public GoalCheckTreeDialog(Collection<? extends Artifact> artifacts) {
      super("Select Goals", "Select Goals", artifacts, new ArrayTreeContentProvider(), new MembersLabelProvider());
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control control = super.createDialogArea(container);
      getTreeViewer().getViewer().setComparator(new MembersViewerSorter());
      return control;
   }

}
