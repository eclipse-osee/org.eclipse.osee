/*********************************************************************
 * Copyright (c) 2010 Boeing
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Single section for artifact type
 *
 * @author Donald G. Dunne
 */
public class XArtifactTypeComboViewer extends XComboViewer {
   public static final String SELECT_STR = "--select--";
   public static final String WIDGET_ID = XArtifactTypeComboViewer.class.getSimpleName();
   private ArtifactTypeToken selectedArtifactType = null;

   public XArtifactTypeComboViewer() {
      super("Artifact Type", SWT.READ_ONLY);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);

      try {
         Collection<ArtifactTypeToken> artifactTypes = ServiceUtil.getTokenService().getArtifactTypes();
         List<ArtifactTypeToken> sortedArtifatTypes = new ArrayList<>();
         sortedArtifatTypes.addAll(artifactTypes);
         Collections.sort(sortedArtifatTypes);
         getComboViewer().setInput(sortedArtifatTypes);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      ArrayList<Object> defaultSelection = new ArrayList<>();
      defaultSelection.add(SELECT_STR);
      setSelected(defaultSelection);
      addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            selectedArtifactType = (ArtifactTypeToken) getComboViewer().getStructuredSelection().getFirstElement();
         }
      });
   }

   @Override
   public ArtifactTypeToken getSelected() {
      return selectedArtifactType;
   }

   @Override
   public Object getData() {
      return Arrays.asList(selectedArtifactType);
   }

}
