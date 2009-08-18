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
package org.eclipse.osee.ats.util.widgets.dialog;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactDescriptiveLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactNameSorter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemListDialog extends CheckedTreeSelectionDialog {

   public ActionableItemListDialog(Active active) {
      super(Display.getCurrent().getActiveShell(), new ArtifactDescriptiveLabelProvider(), new AITreeContentProvider(
            active));
      setTitle("Select Actionable Item(s)");
      setMessage("Select Actionable Item(s)");
      setComparator(new ArtifactNameSorter());
      try {
         setInput(ActionableItemArtifact.getTopLevelActionableItems(active));
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public Set<ActionableItemArtifact> getSelected() {
      Set<ActionableItemArtifact> selectedactionItems = new HashSet<ActionableItemArtifact>();
      for (Object obj : getResult())
         selectedactionItems.add((ActionableItemArtifact) obj);
      return selectedactionItems;
   }

}
