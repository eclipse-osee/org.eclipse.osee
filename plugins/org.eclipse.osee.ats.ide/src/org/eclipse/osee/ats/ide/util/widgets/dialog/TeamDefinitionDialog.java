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
package org.eclipse.osee.ats.ide.util.widgets.dialog;

import java.util.Collection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactNameSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitionDialog extends FilteredTreeDialog {

   public TeamDefinitionDialog() {
      this("Select Team", "Select Team");
   }

   public TeamDefinitionDialog(String title, String message) {
      super(title, message, new ArrayTreeContentProvider(), new TeamDefinitionLabelProvider(),
         new ArtifactNameSorter());
   }

   public static class TeamDefinitionLabelProvider extends LabelProvider {
      @Override
      public String getText(Object element) {
         if (element instanceof IAtsTeamDefinition) {
            return ((IAtsTeamDefinition) element).getName();
         }
         return "Unknown element type";
      }

      @Override
      public Image getImage(Object element) {
         if (element instanceof IAtsTeamDefinition) {
            return ArtifactImageManager.getImage(AtsArtifactTypes.TeamDefinition);
         }
         return null;
      }

   };

   @Override
   public void setInput(Object input) {
      super.setInput(input);
      if (input instanceof Collection<?>) {
         Collection<?> coll = (Collection<?>) input;
         if (coll.size() == 1 && getTreeViewer() != null) {
            getTreeViewer().getViewer().expandToLevel(coll.iterator().next(), 1);
         }
      } else if (input instanceof IAtsTeamDefinition) {
         getTreeViewer().getViewer().expandToLevel(input, 1);
      }
   }

}
