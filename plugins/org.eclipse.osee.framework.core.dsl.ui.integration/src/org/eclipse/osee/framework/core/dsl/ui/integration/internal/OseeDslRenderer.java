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
package org.eclipse.osee.framework.core.dsl.ui.integration.internal;

import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.GENERALIZED_EDIT;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.BaseArtifactEditorInput;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public final class OseeDslRenderer extends DefaultArtifactRenderer {

   private static final String OSEE_DSL_EDITOR_ID = "org.eclipse.osee.framework.core.dsl.OseeDsl";
   private static final String COMMAND_ID = "org.eclipse.osee.framework.core.dsl.OseeDsl.editor.command";

   @Override
   public String getName() {
      return "OseeDsl Editor";
   }

   @Override
   public DefaultArtifactRenderer newInstance() {
      return new OseeDslRenderer();
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact) {
      if (presentationType != GENERALIZED_EDIT && !artifact.isHistorical()) {
         if (artifact.isOfType(CoreArtifactTypes.ACCESS_CONTROL_MODEL)) {
            return SUBTYPE_TYPE_MATCH;
         }
      }
      return NO_MATCH;
   }

   @Override
   public Image getImage(Artifact artifact) {
      return super.getImage(artifact);
   }

   @Override
   public List<String> getCommandId(PresentationType presentationType) {
      ArrayList<String> commandIds = new ArrayList<String>(1);
      if (presentationType == PresentationType.SPECIALIZED_EDIT) {
         commandIds.add(COMMAND_ID);
      }
      return commandIds;
   }

   @Override
   public void open(final List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               for (Artifact artifact : artifacts) {
                  //                  String value = artifact.getSoleAttributeValueAsString(CoreAttributeTypes.GeneralStringData, "");
                  AWorkbench.getActivePage().openEditor(new XtextArtifactEditorInput(artifact), OSEE_DSL_EDITOR_ID);
               }
            } catch (CoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });

   }

   @Override
   public boolean supportsCompare() {
      return true;
   }

   private final static class XtextArtifactEditorInput extends BaseArtifactEditorInput {

      public XtextArtifactEditorInput(Artifact artifact) {
         super(artifact);
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof XtextArtifactEditorInput) {
            return super.equals(obj);
         }
         return false;
      }

      @Override
      public Object getAdapter(Class adapter) {
         System.out.println(adapter);
         return super.getAdapter(adapter);
      }

   }

}
