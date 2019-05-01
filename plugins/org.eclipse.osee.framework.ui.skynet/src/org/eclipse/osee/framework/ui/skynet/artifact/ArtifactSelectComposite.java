/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.ui.skynet.artifact;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.ToStringViewerSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * @author Donald G. Dunne
 */
public class ArtifactSelectComposite extends Composite {
   protected static final int SIZING_TEXT_FIELD_WIDTH = 250;

   private Button artifactSelectButton;
   private Text artifactSelectTextWidget;
   private Artifact selectedArtifact;
   private final Set<Listener> listeners;
   private final String itemName;

   private final ArtifactProvider artifactProvider;

   public static interface ArtifactProvider {
      Collection<Artifact> getSelectableArtifacts();
   }

   public ArtifactSelectComposite(Composite parent, int style, ArtifactProvider getSelectableArtifacts, String itemName) {
      super(parent, style);
      this.artifactProvider = getSelectableArtifacts;
      this.itemName = itemName;
      this.listeners = Collections.synchronizedSet(new HashSet<Listener>());
      createControl(this);
   }

   private void createControl(Composite parent) {
      parent.setLayout(ALayout.getZeroMarginLayout(2, false));
      parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      createButton(parent);

      artifactSelectTextWidget = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
      GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
      data.widthHint = SIZING_TEXT_FIELD_WIDTH;
      artifactSelectTextWidget.setLayoutData(data);
      artifactSelectTextWidget.setBackground(Displays.getSystemColor(SWT.COLOR_WHITE));
      artifactSelectTextWidget.setText(String.format(" -- Select A %s -- ", itemName));
      artifactSelectTextWidget.setDoubleClickEnabled(false);
      artifactSelectTextWidget.addListener(SWT.MouseDoubleClick, new Listener() {
         @Override
         public void handleEvent(Event event) {
            handleSelectedArtifact(event);
            notifyListener(event);
         }
      });

   }

   private void createButton(Composite parent) {
      artifactSelectButton = new Button(parent, SWT.PUSH);
      artifactSelectButton.setText("Select " + itemName);
      artifactSelectButton.addListener(SWT.Selection, new Listener() {

         @Override
         public void handleEvent(Event event) {
            handleSelectedArtifact(event);
            notifyListener(event);
         }
      });
      artifactSelectButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
   }

   public Artifact getSelectedArtifact() {
      return selectedArtifact;
   }

   private void handleSelectedArtifact(Event event) {
      if (event.widget == artifactSelectButton || event.widget == artifactSelectTextWidget && artifactSelectTextWidget.getDoubleClickEnabled()) {
         FilteredTreeDialog dialog = new FilteredTreeDialog("Select " + itemName, "Select A " + itemName,
            new ArrayTreeContentProvider(), new StringLabelProvider(), new ToStringViewerSorter());
         dialog.setMultiSelect(false);
         dialog.setInput(artifactProvider.getSelectableArtifacts());
         if (dialog.open() == 0 && !dialog.getSelected().isEmpty()) {
            setSelected((Artifact) dialog.getSelected().iterator().next());
         }
      }
   }

   public void setSelected(Artifact artifact) {
      selectedArtifact = artifact;
      if (artifact == null) {
         artifactSelectTextWidget.setText(String.format(" -- Select A %s -- ", itemName));
      } else {
         artifactSelectTextWidget.setText(selectedArtifact.getName());
      }
   }

   private void notifyListener(Event event) {
      synchronized (listeners) {
         for (Listener listener : listeners) {
            listener.handleEvent(event);
         }
      }
   }

   public void addListener(Listener listener) {
      synchronized (listeners) {
         listeners.add(listener);
      }
   }

   public void removeListener(Listener listener) {
      synchronized (listeners) {
         listeners.remove(listener);
      }
   }

   public void setDefaultSelectedArtifact(Artifact artifact) {
      setSelected(artifact);
   }

   public Text getBranchSelectText() {
      return artifactSelectTextWidget;
   }

   public Button getBranchSelectButton() {
      return artifactSelectButton;
   }
}
