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

package org.eclipse.osee.ats.util.widgets.commit;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ArtifactTreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.SelectionDialog;

/**
 * @author Donald G. Dunne
 */
public class ICommitConfigCheckListDialog extends SelectionDialog {
   private CheckboxTreeViewer treeViewer;
   private final ArrayList<ICommitConfigArtifact> selectedCommitConfigs;

   public ICommitConfigCheckListDialog(Collection<ICommitConfigArtifact> attrTypes) {
      super(Display.getCurrent().getActiveShell());
      setTitle("Select Configurations");
      setMessage("Select Configurations");
      this.selectedCommitConfigs = new ArrayList<ICommitConfigArtifact>();

      if (attrTypes != null && !attrTypes.isEmpty()) {
         selectedCommitConfigs.addAll(attrTypes);
      }
   }

   @Override
   protected Control createDialogArea(Composite container) {

      Composite comp = new Composite(container, SWT.NONE);
      comp.setLayout(new GridLayout(2, false));
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 700;
      gd.widthHint = 500;
      comp.setLayoutData(gd);

      treeViewer = new CheckboxTreeViewer(comp, SWT.MULTI | SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
      treeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      treeViewer.setContentProvider(new ArtifactTreeContentProvider());
      treeViewer.setSorter(new AttributeViewerSorter());
      ArrayList<Object> objs = new ArrayList<Object>();
      for (Object obj : selectedCommitConfigs)
         objs.add(obj);
      treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
         public void selectionChanged(SelectionChangedEvent event) {
            selectedCommitConfigs.clear();
            for (Object obj : treeViewer.getCheckedElements())
               selectedCommitConfigs.add((ICommitConfigArtifact) obj);
         };
      });
      treeViewer.setLabelProvider(new LabelProvider() {

         @Override
         public String getText(Object obj) {
            return obj.toString();
         }
      });
      treeViewer.setInput(selectedCommitConfigs);
      return container;
   }

   public class AttributeViewerSorter extends ViewerSorter {
      public AttributeViewerSorter() {
         super();
      }

      @Override
      @SuppressWarnings("unchecked")
      public int compare(Viewer viewer, Object o1, Object o2) {
         try {
            return getComparator().compare(((ICommitConfigArtifact) o1).getFullDisplayName(),
                  ((ICommitConfigArtifact) o2).getFullDisplayName());
         } catch (OseeCoreException ex) {
            return 0;
         }
      }
   }

   public boolean noneSelected() {
      return selectedCommitConfigs.isEmpty();
   }

   /**
    * @return the selectedCommitConfigs
    */
   public ArrayList<ICommitConfigArtifact> getSelectedCommitConfigs() {
      return selectedCommitConfigs;
   }
}
