/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.ide.util.widgets.dialog.VersionListDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelCmdValueSelection;

/**
 * @author Donald G. Dunne
 */
public class XTargetedVersionHyperlinkWidget extends XHyperlinkLabelCmdValueSelection {

   IAtsVersion version;
   Collection<IAtsVersion> selectable = new HashSet<>();
   public static final String WIDGET_ID = XTargetedVersionHyperlinkWidget.class.getSimpleName();

   public XTargetedVersionHyperlinkWidget() {
      super("Targeted Version", true, 50);
   }

   @Override
   public String getCurrentValue() {
      return version == null ? "" : version.getName();
   }

   @Override
   public boolean handleSelection() {
      final VersionListDialog dialog = new VersionListDialog("Select Version", "Select Version", selectable);
      int result = dialog.open();
      if (result != 0) {
         return false;
      }
      Object obj = dialog.getSelectedFirst();
      version = (IAtsVersion) obj;
      return true;
   }

   @Override
   public boolean handleClear() {
      version = null;
      return true;
   }

   public IAtsVersion getSelected() {
      return version;
   }

   public Collection<IAtsVersion> getSelectable() {
      return selectable;
   }

   public void setSelectable(Collection<IAtsVersion> selectable) {
      this.selectable = selectable;
   }

   public void setVersion(IAtsVersion version) {
      this.version = version;
   }

}
