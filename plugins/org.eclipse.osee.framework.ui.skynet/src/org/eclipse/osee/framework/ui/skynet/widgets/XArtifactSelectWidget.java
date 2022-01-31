/*********************************************************************
 * Copyright (c) 2016 Boeing
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactSelectComposite;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactSelectComposite.ArtifactProvider;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * Widget providing attribute label, select button with filterable list and readonly name of selected artifact
 *
 * @author Donald G. Dunne
 */
public class XArtifactSelectWidget extends GenericXWidget implements Listener, ArtifactProvider {
   public static final String WIDGET_ID = XArtifactSelectWidget.class.getSimpleName();

   protected ArtifactSelectComposite selectComposite;
   private Composite composite;
   private Artifact defaultArtifact;
   private final List<Listener> listeners = new ArrayList<>();

   public XArtifactSelectWidget() {
      this("");
   }

   public XArtifactSelectWidget(String label) {
      super(label);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      composite = null;

      if (!verticalLabel && horizontalSpan < 2) {
         horizontalSpan = 2;
      } else if (verticalLabel) {
         horizontalSpan = 1;
      }

      if (isDisplayLabel() && verticalLabel) {
         composite = new Composite(parent, SWT.NONE);
         GridLayout gL = new GridLayout();
         gL.marginWidth = 0;
         gL.marginHeight = 0;
         composite.setLayout(gL);
         composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      } else {
         composite = parent;
      }

      // Create List Widgets
      if (isDisplayLabel()) {
         labelWidget = new Label(composite, SWT.NONE);
         labelWidget.setText(getLabel() + ":");
      }
      selectComposite = new ArtifactSelectComposite(composite, SWT.NONE, this, getLabel());
      if (defaultArtifact != null) {
         selectComposite.setDefaultSelectedArtifact(defaultArtifact);
      }
      selectComposite.addListener(this);
   }

   @Override
   public Collection<Artifact> getSelectableArtifacts() {
      return Collections.emptyList();
   }

   @Override
   public void dispose() {
      if (selectComposite != null) {
         selectComposite.removeListener(this);
         selectComposite.dispose();
      }
   }

   @Override
   public Control getControl() {
      return selectComposite.getBranchSelectText();
   }

   public Control getButtonControl() {
      return selectComposite.getBranchSelectButton();
   }

   @Override
   public void setEditable(boolean editable) {
      super.setEditable(editable);
      if (selectComposite != null) {
         if (getControl() != null && !getControl().isDisposed()) {
            getControl().setEnabled(editable);
         }
         if (getButtonControl() != null && !getButtonControl().isDisposed()) {
            getButtonControl().setEnabled(editable);
         }
      }
   }

   @Override
   public Artifact getData() {
      return getSelection();
   }

   public Artifact getSelection() {
      return selectComposite.getSelectedArtifact();
   }

   @Override
   public String getReportData() {
      Artifact artifact = selectComposite.getSelectedArtifact();
      return artifact != null ? artifact.getName() : "";
   }

   @Override
   public IStatus isValid() {
      if (isEmpty()) {
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Must select a Artifact");
      }
      return Status.OK_STATUS;
   }

   @Override
   public boolean isEmpty() {
      return selectComposite.getSelectedArtifact() == null;
   }

   @Override
   public void setFocus() {
      selectComposite.setFocus();
   }

   @Override
   public void setDisplayLabel(final String displayLabel) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            XArtifactSelectWidget.super.setDisplayLabel(displayLabel);
            getLabelWidget().setText(displayLabel);
         }
      });
   }

   @Override
   public void setToolTip(final String toolTip) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (Strings.isValid(toolTip)) {
               XArtifactSelectWidget.super.setToolTip(toolTip);
               if (selectComposite != null && selectComposite.isDisposed() != true) {
                  selectComposite.setToolTipText(toolTip);
                  for (Control control : selectComposite.getChildren()) {
                     control.setToolTipText(toolTip);
                  }
               }
            }
         }
      });
   }

   @Override
   public void handleEvent(Event event) {
      super.validate();
      notifyListeners(event);
      notifyXModifiedListeners();
   }

   public void addListener(Listener listener) {
      listeners.add(listener);
   }

   public void removeListener(Listener listener) {
      listeners.remove(listener);
   }

   private void notifyListeners(Event event) {
      for (Listener listener : listeners) {
         listener.handleEvent(event);
      }
   }

   public void setSelection(Artifact artifact) {
      defaultArtifact = artifact;
      if (selectComposite != null) {
         selectComposite.setSelected(artifact);
      }
   }

   public ArtifactSelectComposite getSelectComposite() {
      return selectComposite;
   }
}