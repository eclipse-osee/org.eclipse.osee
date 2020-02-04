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
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.util.widgets.dialog.VersionTreeDialog;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelCmdValueSelection;

/**
 * @author Megumi Telles
 */
public class XHyperlabelVersionSelection extends XHyperlinkLabelCmdValueSelection {

   public static final String WIDGET_ID = XHyperlabelVersionSelection.class.getSimpleName();
   Collection<Version> selectedVersions = new HashSet<>();
   Collection<IAtsVersion> versions;
   VersionTreeDialog dialog = null;
   IAtsTeamDefinition teamDef;

   public XHyperlabelVersionSelection(String label) {
      super(label, true, WorldEditor.TITLE_MAX_LENGTH);
   }

   public XHyperlabelVersionSelection(String label, IAtsTeamDefinition teamDef) {
      super(label, true, WorldEditor.TITLE_MAX_LENGTH);
      this.teamDef = teamDef;
   }

   public Collection<Version> getSelectedVersions() {
      return selectedVersions;
   }

   @Override
   public Object getData() {
      return getSelectedVersions();
   }

   @Override
   public String getCurrentValue() {
      return Collections.toString(",", selectedVersions);
   }

   public void setSelectedVersions(Collection<Version> selectedVersions) {
      this.selectedVersions = selectedVersions;
      refresh();
      notifyXModifiedListeners();
   }

   @Override
   public boolean handleClear() {
      selectedVersions.clear();
      notifyXModifiedListeners();
      return true;
   }

   @Override
   public boolean handleSelection() {
      try {
         if (versions == null) {
            dialog = new VersionTreeDialog(Active.Both);
         } else {
            dialog = new VersionTreeDialog(Active.Both, versions);
         }
         int result = dialog.open();
         if (result == 0) {
            selectedVersions.clear();
            for (Object obj : dialog.getResultVersions()) {
               selectedVersions.add((Version) obj);
            }
            notifyXModifiedListeners();
         }
         return true;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   public void setVersions(Collection<IAtsVersion> versions) {
      this.versions = versions;
      if (dialog != null) {
         dialog.setInput(versions);
      }
   }

   @Override
   public boolean isEmpty() {
      return selectedVersions.isEmpty();
   }

}
