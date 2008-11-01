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

package org.eclipse.osee.framework.ui.skynet.export;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.OseeData;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.dialogs.WizardDataTransferPage;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactExportPage extends WizardDataTransferPage {
   private final File exportPath;
   private final Collection<Artifact> selectedArtifacts;

   /**
    * @param pageName
    */
   public ArtifactExportPage(IStructuredSelection selection) {
      super("Main");

      this.exportPath = OseeData.getPath().toFile();
      selectedArtifacts = new ArrayList<Artifact>();
      if (selection != null) {
         Iterator<?> selectionIterator = selection.iterator();
         while (selectionIterator.hasNext()) {
            Object selectedObject = selectionIterator.next();
            if (selectedObject instanceof IAdaptable) {
               selectedObject = ((IAdaptable) selectedObject).getAdapter(Artifact.class);
               if (selectedObject instanceof Artifact) {
                  selectedArtifacts.add((Artifact) selectedObject);
               } else {
                  OSEELog.logSevere(SkynetGuiPlugin.class, "Expected selection to be of type Artifact", true);
               }
            } else {
               OSEELog.logSevere(SkynetGuiPlugin.class, "Expected selection to be of type IAdaptable", true);
            }
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.dialogs.WizardDataTransferPage#allowNewContainerName()
    */
   @Override
   protected boolean allowNewContainerName() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
    */
   @Override
   public void handleEvent(Event event) {

   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
    */
   @Override
   public void createControl(Composite parent) {
      initializeDialogUnits(parent);

      Composite composite = new Composite(parent, SWT.NULL);
      composite.setLayout(new GridLayout(1, false));
      composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
      composite.setFont(parent.getFont());

      //createSourceGroup(composite);

      createOptionsGroup(composite);

      setPageComplete(determinePageCompletion());
      setControl(composite);
   }

   /**
    * @return
    */
   public Collection<Artifact> getExportArtifacts() {
      return selectedArtifacts;
   }

   /**
    * @return
    */
   public File getExportPath() {
      return exportPath;
   }
}