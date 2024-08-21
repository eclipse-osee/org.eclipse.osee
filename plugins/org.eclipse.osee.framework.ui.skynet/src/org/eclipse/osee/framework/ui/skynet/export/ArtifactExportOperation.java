/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.framework.ui.skynet.export;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.publishing.FilenameFactory;
import org.eclipse.osee.framework.core.publishing.FilenameFormat;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;

/**
 * Operation for exporting artifacts to the local file system.
 *
 * @author Loren K. Ashley
 */

public class ArtifactExportOperation implements IRunnableWithProgress {

   /**
    * Extension of the {@Link IRunnableWithProgress} interface with a method to get the action's {@link IStatus}.
    */

   private interface Action extends IRunnableWithProgress {

      /**
       * Get's the action's {@link IStatus}.
       *
       * @return the action's status.
       */

      IStatus getStatus();
   }

   /**
    * Encapsulates the parameters and export method to export an artifact.
    */

   private class ExportAction implements Action {

      /**
       * The artifact to be exported.
       */

      private final @NonNull Artifact artifact;

      /**
       * Local file system path to write the exported artifact file to.
       */

      private final @NonNull Path path;

      /**
       * Saves the action's status.
       */

      private @Nullable IStatus status;

      /**
       * Creates a new {@link ExportAction} and saves the export parameters.
       *
       * @param path the local file system {@link Path} to write the exported artifact to.
       * @param artifact the {@link Artifact} to be exported.
       * @throws NullPointerException when <code>path</code> or <code>artifact</code> are <code>null</code>.
       */

      ExportAction(@NonNull Path path, @NonNull Artifact artifact) {

         this.path = Conditions.requireNonNull(path, "path");
         this.artifact = artifact;
         this.status = null;
      }

      /**
       * Gets the status of the operation.
       *
       * @return when the operation has been run the operation {@link IStatus}; otherwise, an {@link IStatus#ERROR}
       * indicating the operation has not been run.
       */

      @Override
      public IStatus getStatus() {

         //@formatter:off
         return
            ( this.status != null )
               ? this.status
               : new Status
                        (
                           IStatus.ERROR,
                           ExportAction.class,
                           ArtifactExportConstants.ARTIFACT_EXPORT_OPERATION_NOT_RUN_ERROR_TITLE
                        );
         //@formatter:on
      }

      /**
       * Performs an export of the artifact to the local file system.
       *
       * @param monitor the progress monitor for the operation.
       * @throws NullPointerException when <code>monitor</code> is <code>null</code>.
       * @throws IllegalStateException when the operation has already been run.
       */

      @Override
      public void run(@NonNull IProgressMonitor monitor) {

         Conditions.requireNonNull(monitor);
         Conditions.requireNull(this.status, "status");

         try {

            //@formatter:off
            monitor.subTask
               (
                 ArtifactExportConstants.ARTIFACT_EXPORT_OPERATION_EXPORT_ARTIFACT_PREFIX + this.artifact.getName()
               );

            this.status =
               RendererManager.openExecuteWork
                  (
                     List.of( this.artifact ),
                     PresentationType.PREVIEW,
                     monitor,
                     RendererMap.of
                        (
                           RendererOption.FILENAME_FORMAT,  FilenameFormat.EXPORT,
                           RendererOption.OUTPUT_PATH,      this.path,
                           RendererOption.PROGRESS_MONITOR, monitor
                        )
                  );
            //@formatter:on

         } catch (Exception e) {

            //@formatter:off
            final var message =
               new Message()
                      .title( ArtifactExportConstants.ARTIFACT_EXPORT_OPERATION_RENDER_ERROR_TITLE )
                      .indentInc()
                      .segment( ArtifactExportConstants.ARTIFACT_EXPORT_OPERATION_RENDER_ERROR_ARTIFACT_SEGMENT_TITLE, artifact.getName() )
                      .toString();
            //@formatter:on
            this.status = new Status(IStatus.ERROR, ExportAction.class, message, e);
         }
      }
   }

   /**
    * Encapsulates the parameters and make path method to create an export path.
    */

   private class MakeDirectoryAction implements Action {

      /**
       * Local file system path to be created.
       */

      private final @NonNull Path path;

      /**
       * Saves the action's status.
       */

      private @Nullable IStatus status;

      /**
       * Creates a new {@link MakeDirectoryAction} and saves the parameters.
       *
       * @param path the local file system {@link Path} to create.
       * @throws NullPointerException when <code>path</code> is <code>null</code>.
       */

      MakeDirectoryAction(@NonNull Path path) {

         this.path = Conditions.requireNonNull(path, "path");
         this.status = null;
      }

      /**
       * Gets the status of the operation.
       *
       * @return when the operation has been run the operation {@link IStatus}; otherwise, an {@link IStatus#ERROR}
       * indicating the operation has not been run.
       */

      @Override
      public IStatus getStatus() {

         //@formatter:off
         return
            ( this.status != null )
               ? this.status
               : new Status
                        (
                           IStatus.ERROR,
                           ExportAction.class,
                           ArtifactExportConstants.ARTIFACT_EXPORT_OPERATION_NOT_RUN_ERROR_TITLE
                        );
         //@formatter:on
      }

      /**
       * Performs the local file system path creation.
       *
       * @param monitor the progress monitor for the operation.
       * @throws NullPointerException when <code>monitor</code> is <code>null</code>.
       * @throws IllegalStateException when the operation has already been run.
       */

      @Override
      public void run(@NonNull IProgressMonitor monitor) {

         Conditions.requireNonNull(monitor);
         Conditions.requireNull(this.status, "status");

         try {

            monitor.subTask(ArtifactExportConstants.ARTIFACT_EXPORT_OPERATION_MAKE_PATH_PREFIX + this.path);

            final var folderFile = this.path.toFile();
            folderFile.mkdir();

            this.status = Status.OK_STATUS;

         } catch (Exception e) {

            //@formatter:off
            final var message =
               new Message()
                      .title( ArtifactExportConstants.ARTIFACT_EXPORT_OPERATION_MAKE_PATH_ERROR_TITLE )
                      .indentInc()
                      .segment( ArtifactExportConstants.ARTIFACT_EXPORT_OPERATION_MAKE_PATH_ERROR_ARTIFACT_SEGMENT_TITLE, this.path )
                      .toString();
            //@formatter:on
            this.status = new Status(IStatus.ERROR, MakeDirectoryAction.class, message, e);
         }
      }

   }

   /**
    * When <code>true</code> the operation will be canceled upon the first error; otherwise, the operation will attempt
    * to run until completion.
    */

   private final boolean cancelOnError;

   /**
    * Saves a list of the artifacts to be exported.
    */

   private final List<Artifact> exportArtifacts;

   /**
    * Saves the root path in the local file system selected by the user to save the exported artifacts.
    */

   private final Path rootExportPath;

   /**
    * Accumulates the statuses of the directory creation operations and the artifact export operations.
    */

   private MultiStatus status;

   /**
    * Creates a new {@link ArtifactExportAction} and saves the export parameters.
    *
    * @param exportPath the local file system {@link Path} to write the exported artifact to.
    * @param cancelOnError when <code>true</code> the operation will cancel upon the first error.
    * @param exportArtifacts a list of the root {@link Artifact}s to be exported.
    * @throws NullPointerException when any of the parameters are <code>null</code>.
    */

   public ArtifactExportOperation(@NonNull Path exportPath, @NonNull boolean cancelOnError, @NonNull List<Artifact> exportArtifacts) {

      this.rootExportPath = Conditions.requireNonNull(exportPath, "exportPath");
      this.cancelOnError = Conditions.requireNonNull(cancelOnError, "cancelOnError");
      this.exportArtifacts = Conditions.requireNonNull(exportArtifacts, "exportArtifacts");
      this.status = null;

   }

   /**
    * Export actions are created for non-folder artifacts. Make path actions are created for folder artifacts. The
    * children of the selected artifacts are recursively processed. The children of non-folder artifacts will be
    * exported to the same directory as their parent artifact.
    *
    * @param levelExportPath the path where artifacts will be exported for the level.
    * @param levelArtifacts the artifacts at the recursion level to be processed.
    * @param actions the actions to be performed are appended to this list.
    * @param artifactIdSet a set used to track which artifacts have already been processed.
    * @return a {@link MultiStatus} indicating whether the action list was successfully built.
    */

   private MultiStatus getActions(@NonNull Path levelExportPath, @NonNull List<Artifact> levelArtifacts,
      @NonNull List<Action> actions, @NonNull Set<ArtifactId> artifactIdSet) {

      var levelStatus =
         new MultiStatus(ArtifactExportOperation.class, 0, ArtifactExportConstants.ARTIFACT_EXPORT_OPERATION_NAME);

      Path exportPath = levelExportPath;

      for (final var artifact : levelArtifacts) {

         if (artifact == null) {
            continue;
         }

         final var artifactId = ArtifactId.create(artifact);

         if (artifactIdSet.contains(artifactId)) {
            continue;
         }

         if (!artifact.isOfType(CoreArtifactTypes.Folder)) {

            final var action = new ExportAction(levelExportPath, artifact);

            actions.add(action);

         } else {

            final var artifactName = artifact.getName();
            final var cleanArtifactName = FilenameFactory.makeNameCleaner(artifactName);
            exportPath = levelExportPath.resolve(cleanArtifactName);
            final var action = new MakeDirectoryAction(exportPath);

            actions.add(action);

         }

         List<Artifact> children = null;

         try {

            children = artifact.getChildren();

         } catch (Exception e) {

            final var getChildrenStatus = new Status(IStatus.ERROR, ArtifactExportOperation.class,
               ArtifactExportConstants.ARTIFACT_EXPORT_OPERATION_GET_CHILDREN_ERROR_TITLE);

            levelStatus.add(getChildrenStatus);

            continue;
         }

         final var getActionsStatus = this.getActions(exportPath, children, actions, artifactIdSet);

         if (!getActionsStatus.isOK()) {
            levelStatus.merge(getActionsStatus);
         }
      }

      return levelStatus;
   }

   /**
    * Gets the status of the operation.
    *
    * @return when the operation has been run the operation {@link IStatus}; otherwise, an {@link IStatus#ERROR}
    * indicating the operation has not been run.
    */

   public IStatus getStatus() {
      //@formatter:off
      return
         ( this.status != null )
            ? this.status
            : new Status
                     (
                        IStatus.ERROR,
                        ArtifactExportOperation.class,
                        ArtifactExportConstants.ARTIFACT_EXPORT_OPERATION_NOT_RUN_ERROR_TITLE
                     );
      //@formatter:on
   }

   /**
    * Performs the export of the artifacts to the local file system.
    *
    * @param monitor the progress monitor for the operation.
    * @throws NullPointerException when <code>monitor</code> is <code>null</code>.
    * @throws IllegalStateException when the operation has already been run.
    * @throws InterruptedException when the <code>monitor</code> has been set to canceled.
    */

   @Override
   public void run(@NonNull IProgressMonitor monitor) throws InterruptedException {

      Conditions.requireNonNull(monitor);
      Conditions.requireNull(this.status, "status");

      this.status = new MultiStatus(ArtifactExportOperation.class, 0,
         ArtifactExportConstants.ARTIFACT_EXPORT_OPERATION_DEFAULT_STATUS_MESSAGE);

      try {

         final var actions = new LinkedList<Action>();
         final var artifactIdSet = new HashSet<ArtifactId>();
         final var getActionsStatus =
            this.getActions(this.rootExportPath, this.exportArtifacts, actions, artifactIdSet);

         if (!getActionsStatus.isOK()) {
            this.status.merge(getActionsStatus);
         }

         if (!this.status.isOK() && this.cancelOnError) {
            return;
         }

         final var actionCount = actions.size();

         monitor.beginTask(ArtifactExportConstants.ARTIFACT_EXPORT_OPERATION_MONITOR_NAME, actionCount);

         for (final var action : actions) {

            action.run(monitor);

            monitor.worked(1);

            final var actionStatus = action.getStatus();

            if (!actionStatus.isOK()) {
               this.status.merge(actionStatus);
            }

            if (!this.status.isOK() && this.cancelOnError) {

               monitor.setCanceled(true);

               return;
            }

            if (monitor.isCanceled()) {

               return;
            }

         }

      } catch (Exception e) {

         final var runFailureStatus =
            new Status(IStatus.ERROR, ArtifactExportOperation.class, -1, e.getLocalizedMessage(), e);

         this.status.add(runFailureStatus);

      } finally {

         if (monitor.isCanceled()) {

            throw new InterruptedException();
         }

         monitor.done();
      }

   }

}

/* EOF */