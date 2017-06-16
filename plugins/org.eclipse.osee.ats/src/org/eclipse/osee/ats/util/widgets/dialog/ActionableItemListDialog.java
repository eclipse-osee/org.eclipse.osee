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
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsObjectLabelProvider;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactNameSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemListDialog extends FilteredCheckboxTreeDialog {

   public ActionableItemListDialog(Active active, String message) {
      super("Select Actionable Item(s)", "Select Actionable Item(s)", new AITreeContentProvider(active),
         new AtsObjectLabelProvider(), new ArtifactNameSorter());
      try {
         setInput(ActionableItems.getTopLevelActionableItems(active, AtsClientService.get()));
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public Set<IAtsActionableItem> getSelected() {
      Set<IAtsActionableItem> selectedactionItems = new HashSet<>();
      for (Object obj : getResult()) {
         selectedactionItems.add((IAtsActionableItem) obj);
      }
      return selectedactionItems;
   }

}
