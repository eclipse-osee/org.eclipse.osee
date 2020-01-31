/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Single section for artifact type
 *
 * @author Donald G. Dunne
 */
public class XArtifactTypeComboViewer extends XComboViewer {
   public static final String WIDGET_ID = XArtifactTypeComboViewer.class.getSimpleName();
   private ArtifactTypeToken selectedArtifactType = null;

   public XArtifactTypeComboViewer() {
      super("Artifact Type", SWT.READ_ONLY);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);

      try {
         Collection<? extends ArtifactTypeToken> artifactTypes = ArtifactTypeManager.getAllTypes();
         List<ArtifactTypeToken> sortedArtifatTypes = new ArrayList<>();
         sortedArtifatTypes.addAll(artifactTypes);
         Collections.sort(sortedArtifatTypes);
         getComboViewer().setInput(sortedArtifatTypes);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      ArrayList<Object> defaultSelection = new ArrayList<>();
      defaultSelection.add("--select--");
      setSelected(defaultSelection);
      addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            selectedArtifactType = (ArtifactTypeToken) getSelected();
         }
      });
   }

   public ArtifactTypeToken getSelectedTeamDef() {
      return selectedArtifactType;
   }

   @Override
   public Object getData() {
      return Arrays.asList(selectedArtifactType);
   }

}
