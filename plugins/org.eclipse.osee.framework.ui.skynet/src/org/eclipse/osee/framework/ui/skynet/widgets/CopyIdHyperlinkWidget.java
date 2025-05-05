/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class CopyIdHyperlinkWidget {

   public CopyIdHyperlinkWidget() {
      // utility class
   }

   public static void addCopyIdHyperlinkWidget(Artifact art, FormToolkit toolkit, Composite composite) {
      Hyperlink copyHl = toolkit.createHyperlink(composite, "ID", SWT.NONE);
      copyHl.setToolTipText("Click for [name]-[id]\nRight-Click for <id>");
      copyHl.addMouseListener(new MouseAdapter() {

         @Override
         public void mouseUp(MouseEvent e) {
            Clipboard clipboard = null;
            try {
               String idString = art.toStringWithId();
               if (e.button == 3) {
                  idString = art.getIdString();
               }
               clipboard = new Clipboard(null);
               clipboard.setContents(new Object[] {idString}, new Transfer[] {TextTransfer.getInstance()});
            } catch (Exception ex) {
               OseeLog.logf(Activator.class, Level.SEVERE, ex, "Error obtaining copy");
            } finally {
               if (clipboard != null && !clipboard.isDisposed()) {
                  clipboard.dispose();
                  clipboard = null;
               }
            }
         }
      });
   }

}
