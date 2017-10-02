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
package org.eclipse.osee.ats.workdef.editor;

import static org.eclipse.osee.framework.core.enums.PresentationType.DEFAULT_OPEN;
import static org.eclipse.osee.framework.core.enums.PresentationType.GENERALIZED_EDIT;
import static org.eclipse.osee.framework.core.enums.PresentationType.GENERAL_REQUESTED;
import static org.eclipse.osee.framework.core.enums.PresentationType.SPECIALIZED_EDIT;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * @author Donald G. Dunne
 */
public final class AtsWorkDefinitionDslRenderer extends FileSystemRenderer {

   public AtsWorkDefinitionDslRenderer(Map<RendererOption, Object> rendererOptions) {
      super(rendererOptions);
   }

   public AtsWorkDefinitionDslRenderer() {
      super(new HashMap<RendererOption, Object>());
   }

   @Override
   public String getName() {
      return "ATS Work Definition DSL Editor";
   }

   @Override
   public DefaultArtifactRenderer newInstance() {
      return new AtsWorkDefinitionDslRenderer(new HashMap<RendererOption, Object>());
   }

   @Override
   public DefaultArtifactRenderer newInstance(Map<RendererOption, Object> rendererOptions) {
      return new AtsWorkDefinitionDslRenderer(rendererOptions);
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, Map<RendererOption, Object> rendererOptions) {
      if (artifact.isOfType(
         AtsArtifactTypes.WorkDefinition) && !presentationType.matches(GENERALIZED_EDIT, GENERAL_REQUESTED)) {
         return SUBTYPE_TYPE_MATCH;
      }
      return NO_MATCH;
   }

   @Override
   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact) {
      commands.add(new MenuCmdDef(CommandGroup.EDIT, SPECIALIZED_EDIT, "ATS Work Definition DSL Editor",
         AtsImage.WORK_DEFINITION));
   }

   @Override
   public boolean supportsCompare() {
      return true;
   }

   @Override
   public void open(final List<Artifact> artifacts, PresentationType presentationType)  {
      final PresentationType resultantpresentationType =
         presentationType == DEFAULT_OPEN ? SPECIALIZED_EDIT : presentationType;

      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (!artifacts.isEmpty()) {
               try {
                  IFile file = renderToFile(artifacts, resultantpresentationType);
                  if (file != null) {
                     IWorkbench workbench = PlatformUI.getWorkbench();
                     IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
                     IDE.openEditor(page, file);
                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      });
   }

   @Override
   public String getAssociatedExtension(Artifact artifact)  {
      return "ats";
   }

   @Override
   public InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts)  {
      Artifact artifact = artifacts.iterator().next();

      String data;
      if (artifact.isOfType(AtsArtifactTypes.WorkDefinition)) {
         data = artifact.getSoleAttributeValueAsString(AtsAttributeTypes.DslSheet, "");
      } else {
         throw new OseeArgumentException("Invalidate artifact type [%s] for ATS Work Definition",
            artifact.getArtifactTypeName());
      }

      InputStream inputStream = null;
      try {
         inputStream = new ByteArrayInputStream(data.getBytes("UTF-8"));
      } catch (UnsupportedEncodingException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return inputStream;
   }

   @Override
   public Program getAssociatedProgram(Artifact artifact)  {
      throw new OseeCoreException("should not be called");
   }

   @Override
   protected IOperation getUpdateOperation(File file, List<Artifact> artifacts, BranchId branch, PresentationType presentationType) {
      Artifact artifact = artifacts.iterator().next();
      return new SaveAtsDslModelOperation(file, artifact);
   }

   @Override
   public int minimumRanking() {
      return GENERAL_MATCH;
   }

   public class SaveAtsDslModelOperation extends AbstractOperation {

      private final Artifact artifact;
      private final File file;

      public SaveAtsDslModelOperation(File fromFile, Artifact toArtifact) {
         super("Save ATS DSL Work Definition", Activator.PLUGIN_ID);
         file = fromFile;
         artifact = toArtifact;
      }

      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         if (!AtsClientService.get().getUserService().isAtsAdmin()) {
            AWorkbench.popup("Must be ATS Admin to make Work Definition changes.");
            return;
         }
         if (artifact.isOfType(AtsArtifactTypes.WorkDefinition)) {
            Artifact saveArt = ArtifactQuery.getArtifactFromId(artifact.getGuid(), artifact.getBranch());
            String dslStr = Lib.fileToString(file);
            saveArt.setSoleAttributeValue(AtsAttributeTypes.DslSheet, dslStr);
            saveArt.persist(getName());
            AtsClientService.get().getWorkDefinitionService().clearCaches();
         }

      }

   }
}
