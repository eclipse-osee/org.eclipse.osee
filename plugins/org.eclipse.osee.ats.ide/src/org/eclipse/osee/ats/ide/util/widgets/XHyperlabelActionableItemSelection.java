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
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.util.widgets.dialog.ActionableItemTreeWithChildrenDialog;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelCmdValueSelection;

/**
 * @author Donald G. Dunne
 */
public class XHyperlabelActionableItemSelection extends XHyperlinkLabelCmdValueSelection {

   public static final String WIDGET_ID = XHyperlabelActionableItemSelection.class.getSimpleName();
   Collection<IAtsActionableItem> selectedAis = new HashSet<>();
   Collection<IAtsActionableItem> teamDefs;
   ActionableItemTreeWithChildrenDialog dialog = null;

   public XHyperlabelActionableItemSelection(String label) {
      super(label, true, WorldEditor.TITLE_MAX_LENGTH);
   }

   public Collection<IAtsActionableItem> getSelectedActionableItems() {
      return selectedAis;
   }

   @Override
   public Object getData() {
      List<Artifact> arts = org.eclipse.osee.framework.jdk.core.util.Collections.castAll(getSelectedActionableItems());
      return arts;
   }

   @Override
   public String getCurrentValue() {
      return Collections.toString(",", selectedAis);
   }

   public void setSelectedAIs(Collection<IAtsActionableItem> selectedAIs) {
      this.selectedAis = selectedAIs;
      refresh();
      notifyXModifiedListeners();
   }

   @Override
   public boolean handleClear() {
      selectedAis.clear();
      notifyXModifiedListeners();
      return true;
   }

   @Override
   public boolean handleSelection() {
      try {
         if (teamDefs == null) {
            dialog = new ActionableItemTreeWithChildrenDialog(Active.Both);
         } else {
            dialog = new ActionableItemTreeWithChildrenDialog(Active.Both, teamDefs);
         }
         int result = dialog.open();
         if (result == 0) {
            selectedAis.clear();
            for (Object obj : dialog.getResultAndRecursedAIs()) {
               selectedAis.add((IAtsActionableItem) obj);
            }
            notifyXModifiedListeners();
         }
         return true;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   @Override
   public boolean isEmpty() {
      return selectedAis.isEmpty();
   }

}
