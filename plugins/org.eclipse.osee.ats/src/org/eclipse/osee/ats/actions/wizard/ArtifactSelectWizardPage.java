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
package org.eclipse.osee.ats.actions.wizard;

import java.util.logging.Level;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.util.AtsObjectLabelProvider;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Donald G. Dunne
 */
public class ArtifactSelectWizardPage extends WizardPage {

   private ListViewer artList;
   private Artifact selectedArtifact;
   private boolean showArtData = false;

   public ArtifactSelectWizardPage() {
      super("Select an Artifact");
   }

   @Override
   public void createControl(Composite parent) {
      setTitle("Select an Artifact");

      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 2;
      composite.setLayout(gridLayout);
      GridData gridData1 = new GridData(GridData.FILL, GridData.FILL, true, true);
      composite.setLayoutData(gridData1);

      Composite leftComp = new Composite(composite, SWT.NONE);

      leftComp.setLayout(new GridLayout());
      gridData1 = new GridData(GridData.FILL, GridData.FILL, true, true);
      leftComp.setLayoutData(gridData1);

      new Label(leftComp, SWT.NONE).setText("Artifact Type");

      try {
         ListViewer artTypeList = new ListViewer(leftComp, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
         artTypeList.setContentProvider(new ArrayContentProvider());
         artTypeList.setLabelProvider(new ArtTypeLabelProvider());

         gridData1 = new GridData(GridData.FILL, GridData.FILL, true, true);
         gridData1.heightHint = 300;
         gridData1.widthHint = 200;
         artTypeList.getControl().setLayoutData(gridData1);
         artTypeList.setInput(ArtifactTypeManager.getValidArtifactTypes(AtsClientService.get().getAtsBranch()));
         artTypeList.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
               IStructuredSelection selection = (IStructuredSelection) event.getSelection();
               IArtifactType desc = (IArtifactType) selection.getFirstElement();
               try {
                  artList.setInput(ArtifactQuery.getArtifactListFromType(desc, AtsClientService.get().getAtsBranch()));
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         });
         artTypeList.setComparator(new ViewerComparator() {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
               return getComparator().compare(((IArtifactType) e1).getName(), ((IArtifactType) e2).getName());
            }
         });

         Composite rightComp = new Composite(composite, SWT.NONE);

         rightComp.setLayout(new GridLayout());
         gridData1 = new GridData(GridData.FILL, GridData.FILL, true, true);
         rightComp.setLayoutData(gridData1);

         Label lab = new Label(rightComp, SWT.NONE);
         lab.setText("Artifact (click here for artifact data)");
         lab.setForeground(Displays.getSystemColor(SWT.COLOR_BLUE));
         lab.addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
               showArtData ^= true;
               artList.refresh();
            }
         });

         artList = new ListViewer(rightComp, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
         artList.setContentProvider(new ArrayContentProvider());
         artList.setLabelProvider(new AtsObjectLabelProvider());

         artList.setComparator(new ViewerComparator() {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
               return getComparator().compare(((Artifact) e1).getName(), ((Artifact) e2).getName());
            }
         });
         gridData1 = new GridData(GridData.FILL, GridData.FILL, true, true);
         gridData1.heightHint = 300;
         gridData1.widthHint = 200;
         artList.getControl().setLayoutData(gridData1);
         artList.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
               IStructuredSelection selection = (IStructuredSelection) event.getSelection();
               selectedArtifact = (Artifact) selection.getFirstElement();
            }
         });
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      setControl(composite);
   }

   public Artifact getSelectedArtifact() {
      return selectedArtifact;
   }

   public class ArtifactDescriptiveLabelProvider implements ILabelProvider {

      @Override
      public Image getImage(Object arg0) {
         return null;
      }

      @Override
      public String getText(Object arg0) {
         if (arg0 instanceof Artifact) {
            Artifact art = (Artifact) arg0;
            if (showArtData) {
               return String.format("%s - (%s  %s)", art.getName(), art.getArtId(), art.getGuid());
            } else {
               return art.getName();
            }
         } else if (arg0 instanceof IAtsWorkItem) {
            IAtsWorkItem workItem = (IAtsWorkItem) arg0;
            if (showArtData) {
               return String.format("%s - (%s  %d)", workItem.getName(), workItem.getAtsId(), workItem.getId());
            } else {
               return workItem.getName();
            }
         } else if (arg0 instanceof IAtsObject) {
            IAtsObject art = (IAtsObject) arg0;
            if (showArtData) {
               return String.format("%s - (%d)", art.getName(), art.getId());
            } else {
               return art.getName();
            }
         }
         return arg0.toString();
      }

      @Override
      public boolean isLabelProperty(Object arg0, String arg1) {
         return false;
      }

      @Override
      public void removeListener(ILabelProviderListener arg0) {
         // do nothing
      }

      @Override
      public void addListener(ILabelProviderListener listener) {
         // do nothing
      }

      @Override
      public void dispose() {
         // do nothing
      }
   }

   public static class ArtTypeLabelProvider implements ILabelProvider {

      @Override
      public Image getImage(Object arg0) {
         return null;
      }

      @Override
      public String getText(Object arg0) {
         return ((ArtifactType) arg0).getName();
      }

      @Override
      public void addListener(ILabelProviderListener arg0) {
         // do nothing
      }

      @Override
      public void dispose() {
         // do nothing
      }

      @Override
      public boolean isLabelProperty(Object arg0, String arg1) {
         return false;
      }

      @Override
      public void removeListener(ILabelProviderListener arg0) {
         // do nothing
      }
   }

}
