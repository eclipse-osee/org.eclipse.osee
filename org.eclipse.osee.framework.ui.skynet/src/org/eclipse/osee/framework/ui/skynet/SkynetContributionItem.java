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
package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchViewPage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.StatusLineContributionItem;

/**
 * @author Jeff C. Phillips
 */
public abstract class SkynetContributionItem extends StatusLineContributionItem {
   private final Image enabled;
   private final Image disabled;
   private String enabledToolTip;
   private String disabledToolTip;

   public SkynetContributionItem(String id, Image enabled, Image disabled, String enabledToolTip, String disabledToolTip) {
      this(id, enabled, disabled, enabledToolTip, disabledToolTip, 4);
   }

   public SkynetContributionItem(String id, Image enabled, Image disabled, String enabledToolTip, String disabledToolTip, int width) {
      super(id, true, width);
      this.enabled = enabled;
      this.disabled = disabled;
      this.enabledToolTip = enabledToolTip;
      this.disabledToolTip = disabledToolTip;

      if (enabled == null || enabledToolTip == null || disabled == null || disabledToolTip == null) throw new IllegalStateException(
            "Enabled and disabled images must be set.");

   }

   public void setDisabledToolTip(String disabledToolTip) {
      this.disabledToolTip = disabledToolTip;
   }

   public void setEnabledToolTip(String enabledToolTip) {
      this.enabledToolTip = enabledToolTip;
   }

   protected String getEnabledToolTip() {
      return enabledToolTip;
   }

   protected String getDisabledToolTip() {
      return disabledToolTip;
   }

   protected Image getEnabledImage() {
      return enabled;
   }

   protected Image getDisabledImage() {
      return disabled;
   }

   protected void updateStatus(boolean active) {
      if (active) {
         setImage(getEnabledImage());
         setToolTipText(getEnabledToolTip());
      } else {
         setImage(getDisabledImage());
         setToolTipText(getDisabledToolTip());
      }
   }

   public static void addTo(IStatusLineManager manager) {
      SkynetDefaultBranchContributionItem.addTo(manager);
      if (OseeAts.isAtsAdmin()) AdminContributionItem.addTo(manager);
      SkynetServiceContributionItem.addTo(manager);
      SkynetConnectionContributionItem.addTo(manager);
      ApplicationServerStatusContributionItem.addTo(manager);
      SkynetAuthenticationContributionItem.addTo(manager);
   }

   public static void addTo(AbstractArtifactSearchViewPage view, boolean update) {
      addTo(view.getSite().getActionBars().getStatusLineManager());

      if (update) view.getSite().getActionBars().updateActionBars();
   }

   public static void addTo(ViewPart view, boolean update) {
      addTo(view.getViewSite().getActionBars().getStatusLineManager());

      if (update) view.getViewSite().getActionBars().updateActionBars();
   }

   public static void addTo(MultiPageEditorPart editorPart, boolean update) {
      addTo(editorPart.getEditorSite().getActionBars().getStatusLineManager());
      if (update) editorPart.getEditorSite().getActionBars().updateActionBars();
   }

}
