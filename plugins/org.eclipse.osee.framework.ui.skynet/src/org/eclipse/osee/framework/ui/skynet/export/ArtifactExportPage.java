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
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.search.ui.text.Match;
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

   public ArtifactExportPage(IStructuredSelection selection) {
      super("Main");

      this.exportPath = OseeData.getPath().toFile();
      selectedArtifacts = new ArrayList<>();
      if (selection != null) {
         Iterator<?> selectionIterator = selection.iterator();
         while (selectionIterator.hasNext()) {
            Object selectedObject = selectionIterator.next();

            if (selectedObject instanceof Match) {
               selectedObject = ((Match) selectedObject).getElement();
            } else if (selectedObject instanceof IAdaptable) {
               selectedObject = ((IAdaptable) selectedObject).getAdapter(Artifact.class);
            }

            if (selectedObject instanceof Artifact) {
               selectedArtifacts.add((Artifact) selectedObject);
            } else {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Expected selection to be of type Artifact");
            }
         }
      }
   }

   @Override
   protected boolean allowNewContainerName() {
      return false;
   }

   @Override
   public void handleEvent(Event event) {
      // do nothing
   }

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

   public Collection<Artifact> getExportArtifacts() {
      return selectedArtifacts;
   }

   public File getExportPath() {
      return exportPath;
   }
}