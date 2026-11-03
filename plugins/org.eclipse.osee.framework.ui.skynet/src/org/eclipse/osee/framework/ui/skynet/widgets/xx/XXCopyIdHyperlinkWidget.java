/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets.xx;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.osgi.service.component.annotations.Component;

/**
 * Hyperlink widget that copies the artifact ID to the clipboard. Left-click copies [name]-[id], right-click copies just
 * the id.
 * <p>
 * Can be used as an XX widget via XWidgetBuilder or standalone via the static
 * {@link #addTo(Artifact, FormToolkit, Composite)} method.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXCopyIdHyperlinkWidget extends XXButtonHyperWidget {

   public static final WidgetId ID = WidgetId.XXCopyIdHyperlinkWidget;
   private static final String TOOLTIP = "Copy to Clipboard\n\nClick for [name]-[id]\nRight-Click for <id>";

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXCopyIdHyperlinkWidget() {
      super(ID, "ID");
      setToolTip(TOOLTIP);
      set(XOption.NO_LABEL_BOLD, XOption.COLON);
   }

   //////////////////////////////////////////
   // UI Construction
   //////////////////////////////////////////

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);
      if (labelLinkWidget != null) {
         labelLinkWidget.addMouseListener(new CopyIdMouseAdapter(this));
      }
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   protected boolean handleSelection() {
      // Copy handled by mouseUp listener; no dialog needed
      return false;
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   @Override
   protected String getSelectToModifyMsg() {
      return "";
   }

   /**
    * Standalone usage: creates a "ID" hyperlink followed by the artifact's ID label on the given composite. Click
    * copies [name]-[id], right-click copies just the id.
    */
   public static void addTo(Artifact art, FormToolkit toolkit, Composite composite) {
      Hyperlink copyHl = toolkit.createHyperlink(composite, "ID", SWT.NONE);
      copyHl.setToolTipText(TOOLTIP);
      copyHl.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseUp(MouseEvent e) {
            copyToClipboard(art, e.button);
         }
      });
      Label idLabel = toolkit.createLabel(composite, art.getIdString());
      idLabel.setForeground(Displays.getSystemColor(SWT.COLOR_DARK_GRAY));
   }

   private static void copyToClipboard(Artifact art, int button) {
      if (art == null) {
         return;
      }
      Clipboard clipboard = null;
      try {
         String idString = art.toStringWithId();
         if (button == 3) {
            idString = art.getIdString();
         }
         clipboard = new Clipboard(null);
         clipboard.setContents(new Object[] {idString}, new Transfer[] {TextTransfer.getInstance()});
      } catch (Exception ex) {
         OseeLog.logf(Activator.class, Level.SEVERE, ex, "Error obtaining copy");
      } finally {
         if (clipboard != null && !clipboard.isDisposed()) {
            clipboard.dispose();
         }
      }
   }

   private static class CopyIdMouseAdapter extends MouseAdapter {
      private final XXCopyIdHyperlinkWidget widget;

      CopyIdMouseAdapter(XXCopyIdHyperlinkWidget widget) {
         this.widget = widget;
      }

      @Override
      public void mouseUp(MouseEvent e) {
         copyToClipboard(widget.getArtifact(), e.button);
      }
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   protected void initializeSelected() {
      if (getArtifact() != null) {
         setSelected(getArtifact().getIdString(), false);
      }
   }

}
