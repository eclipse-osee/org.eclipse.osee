/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets.dialog;

import java.util.Collection;
import java.util.LinkedList;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class VersionListDialog extends FilteredTreeDialog {

   XCheckBox showReleased = new XCheckBox("Show Released Versions");
   private final Collection<IAtsVersion> verArts;
   private boolean removeAllAllowed = false;
   private boolean removeAllSelected = false;

   public VersionListDialog(String title, String message, Collection<IAtsVersion> verArts) {
      super(title, message, new VersionContentProvider(false), new VersionLabelProvider(),
         new AtsObjectNameReverseSorter());
      this.verArts = verArts;
      setInput(verArts);
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Composite composite = new Composite(container, SWT.None);
      composite.setLayout(new GridLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

      if (removeAllAllowed) {
         final Button button = new Button(composite, SWT.PUSH);
         button.setText("Un-Set and Close");
         button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               removeAllSelected = true;
               close();
            }
         });
      }

      Composite checkComp = new Composite(composite, SWT.NONE);
      checkComp.setLayout(new GridLayout(2, false));
      checkComp.setLayoutData(new GridData(GridData.FILL_BOTH));

      showReleased.createWidgets(checkComp, 2);
      showReleased.set(false);
      showReleased.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            VersionContentProvider versionContentProvider =
               (VersionContentProvider) getTreeViewer().getViewer().getContentProvider();
            versionContentProvider.setShowReleased(!versionContentProvider.getShowReleased());
            setInput(verArts);
            getTreeViewer().getViewer().refresh();
         };
      });

      Control control = super.createDialogArea(composite);

      return control;
   }

   public boolean isRemoveAllSelected() {
      if (!removeAllAllowed) {
         return false;
      } else {
         return removeAllSelected;
      }
   }

   public static class VersionContentProvider extends ArrayTreeContentProvider {

      boolean showReleased = false;

      public VersionContentProvider(boolean showReleased) {
         this.showReleased = showReleased;
      }

      public boolean getShowReleased() {
         return showReleased;
      }

      @SuppressWarnings({"rawtypes", "unchecked"})
      @Override
      public Object[] getElements(Object inputElement) {
         if (inputElement instanceof Collection) {
            Collection list = (Collection) inputElement;
            Collection<Object> verArts = new LinkedList<>();
            for (Object obj : list.toArray(new IAtsVersion[list.size()])) {
               IAtsVersion verArt = (IAtsVersion) obj;
               if (showReleased || !showReleased && !verArt.isReleased()) {
                  verArts.add(verArt);
               }
            }
            return verArts.toArray(new IAtsVersion[verArts.size()]);
         }
         return super.getElements(inputElement);
      }

      public void setShowReleased(boolean showReleased) {
         this.showReleased = showReleased;
      }
   }

   public boolean isRemoveAllAllowed() {
      return removeAllAllowed;
   }

   public void setRemoveAllAllowed(boolean removeAllAllowed) {
      this.removeAllAllowed = removeAllAllowed;
   }
}
