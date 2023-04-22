/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.framework.ui.skynet.render;

import static org.eclipse.osee.framework.core.enums.PresentationType.SPECIALIZED_EDIT;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FindInWorkspaceOperation;
import org.eclipse.osee.framework.ui.skynet.FindInWorkspaceOperation.FindInWorkspaceCollector;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * @author John R. Misinco
 */
public class JavaRenderer extends FileSystemRenderer {

   /**
    * The likely file system extension for files that hold the same type of data as is stored in main content
    * {@link Attribute} of the most common {@link Artifact} type processed by this renderer.
    */

   private static final String DEFAULT_ASSOCIATED_FILE_EXTENSION = "java";

   /**
    * A short description of the type of documents processed by the renderer.
    */

   private static final String RENDERER_DOCUMENT_TYPE_DESCRIPTION = "Java";

   /**
    * The renderer identifier used for publishing template selection.
    */

   private static final String RENDERER_IDENTIFIER = JavaRenderer.class.getCanonicalName();

   /**
    * The {@link IRenderer} implementation's name.
    */

   private static final String RENDERER_NAME = "Java Editor";

   public JavaRenderer(RendererMap rendererOptions) {
      super(rendererOptions);
   }

   public JavaRenderer() {
      super();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getDocumentTypeDescription() {
      return JavaRenderer.RENDERER_DOCUMENT_TYPE_DESCRIPTION;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getIdentifier() {
      return JavaRenderer.RENDERER_IDENTIFIER;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getName() {
      return JavaRenderer.RENDERER_NAME;
   }

   @Override
   public DefaultArtifactRenderer newInstance() {
      return new JavaRenderer();
   }

   @Override
   public DefaultArtifactRenderer newInstance(RendererMap rendererOptions) {
      return new JavaRenderer(rendererOptions);
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, RendererMap rendererOptions) {
      int toReturn = NO_MATCH;
      if (artifact.isOfType(CoreArtifactTypes.TestCase)) {
         if (presentationType.matches(PresentationType.SPECIALIZED_EDIT, PresentationType.DEFAULT_OPEN)) {
            toReturn = PRESENTATION_SUBTYPE_MATCH;
         }
      }
      return toReturn;
   }

   @Override
   public boolean supportsCompare() {
      return false;
   }

   @Override
   public void open(final List<Artifact> artifacts, PresentationType presentationType) {
      final List<Artifact> notMatched = new LinkedList<>();
      final StringBuffer findErrorMessage = new StringBuffer();

      FindInWorkspaceCollector collector = new FindInWorkspaceCollector() {

         @Override
         public void onResource(final IResource resource) {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  IFileSystem fs = EFS.getLocalFileSystem();

                  IPath fullPath = resource.getLocation();
                  final File fileToOpen = fullPath.toFile();
                  if (fileToOpen != null && fileToOpen.exists() && fileToOpen.isFile()) {
                     try {
                        IFileStore fileStore = fs.getStore(fileToOpen.toURI());
                        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                        IDE.openEditorOnFileStore(page, fileStore);
                     } catch (PartInitException e) {
                        findErrorMessage.append(e.toString());
                     }
                  }
               }
            });
         }

         @Override
         public void onNotFound(Artifact artifact) {
            notMatched.add(artifact);
         }
      };
      IOperation op = new FindInWorkspaceOperation(artifacts, collector);
      Operations.executeAsJob(op, true, Job.LONG, new JobChangeAdapter() {
         @Override
         public void done(IJobChangeEvent event) {
            if (event.getResult().isOK()) {
               Displays.ensureInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     StringBuilder builder = new StringBuilder();
                     builder.append(findErrorMessage);
                     if (!notMatched.isEmpty()) {
                        builder.append(String.format("Item(s) not found in the workspace: [%s]\n",
                           Collections.toString(",", notMatched)));
                     }

                     if (builder.length() > 0) {
                        Shell shell = AWorkbench.getActiveShell();
                        MessageDialog.openError(shell, getName(), builder.toString());
                     }
                  }
               });
            }
         }
      });
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getDefaultAssociatedExtension() {
      return JavaRenderer.DEFAULT_ASSOCIATED_FILE_EXTENSION;
   }

   @Override
   public Program getAssociatedProgram(Artifact artifact) {
      Program program = Program.findProgram("java");
      if (program == null) {
         throw new OseeArgumentException("No program associated with the extension [%s] found on your local machine.",
            "java");
      }
      return program;
   }

   @Override
   public InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts) {
      final List<IResource> matches = new LinkedList<>();
      final List<Artifact> notMatched = new LinkedList<>();

      FindInWorkspaceCollector collector = new FindInWorkspaceCollector() {

         @Override
         public void onResource(IResource resource) {
            matches.add(resource);
         }

         @Override
         public void onNotFound(Artifact artifact) {
            notMatched.add(artifact);
         }
      };
      IOperation op = new FindInWorkspaceOperation(artifacts.subList(0, 1), collector);
      Operations.executeWorkAndCheckStatus(op);
      for (IResource resource : matches) {
         IPath fullPath = resource.getLocation();
         File fileToOpen = fullPath.toFile();
         try {
            return new FileInputStream(fileToOpen);
         } catch (FileNotFoundException ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      }
      return null;
   }

   @Override
   protected IOperation getUpdateOperation(File file, List<Artifact> artifacts, BranchId branch, PresentationType presentationType) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact) {
      commands.add(new MenuCmdDef(CommandGroup.EDIT, SPECIALIZED_EDIT, JavaRenderer.RENDERER_NAME,
         FrameworkImage.JAVA_COMPILATION_UNIT));
   }
}
