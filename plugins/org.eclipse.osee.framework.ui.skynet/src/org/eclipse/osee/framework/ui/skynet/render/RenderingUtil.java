/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.core.model.TransactionDeltaSupplier;
import org.eclipse.osee.framework.core.publishing.FilenameFactory;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.publishing.RendererUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

/**
 * Class of utilities methods for use by {@link FileSystemRenderer} implementations for creating filenames and file
 * handles.
 *
 * @implNote This class is for OSEE client only.
 * @author Loren K. Ashley
 */

public final class RenderingUtil {

   /**
    * Flag to disable pop-up dialogs. Generally used for testing.
    */

   private static AtomicBoolean arePopupsAllowed = new AtomicBoolean(true);

   /**
    * Warning message statement for lengthy filename paths.
    */

   //@formatter:off
   private static final String FILENAME_WARNING_MESSAGE =
        "The filename is approaching a large size which may cause external applications to fail.\n"
      + "It is suggested to move your workspace to avoid potential errors.";
   //@formatter:on

   /**
    * Default class to used for the error document when the render class can't be determined.
    */

   private static final Class<?> UNKNOWN_CLASS = Object.class;

   /**
    * Default document type to used for the error document when render class can't be determined.
    */

   private static final String UNKNOWN_DOCUMENT_TYPE = "UNKNOWN DOCUMENT TYPE";

   /**
    * Default renderer name to used for the error document when the render class can't be determined.
    */

   private static final String UNKNOWN_RENDERER = "UNKNOWN RENDERER";

   /**
    * One and done flag to warn a user about a large file name.
    */

   private static AtomicBoolean showAgain = new AtomicBoolean(true);

   /**
    * Predicate used to determine if pop-up dialogs are allowed.
    *
    * @return <code>true</code> when pop-up dialogs are allowed; otherwise, <code>false</code> when pop-up dialogs are
    * disallowed.
    */

   public static boolean arePopupsAllowed() {
      return RenderingUtil.arePopupsAllowed.get();
   }

   /**
    * Displays the document contained in the file <code>contentFile</code> according to the following rules:
    * <dl>
    * <dt>Pop Ups Allowed</dt>
    * <dd>The provided <code>program</code> is used to open the <code>contentFile</code>.</dd>
    * <dt>Pop Ups Not Allowed</dt>
    * <dd>An {@link Level#INFO} message with the <code>contentFile</code> path is logged.</dd>
    * <dt>An Exception Occurred With One Of The Above Methods</dt>
    * <dd>If a default workbench editor can be found for the <code>contentFile</code> type, it will be used to display
    * the document.</dd>
    * </dl>
    *
    * @param presentationType the {@link PresentationType} determines the type of file monitor.
    * @param program the {@link Program} to open the <code>contentFile</code> with.
    * @param contentFile the rendered file to be displayed.
    */

   public static void displayDocument(PresentationType presentationType, Program program, IFile contentFile) {

      var contentFilePath = contentFile.getLocation().toFile().getAbsolutePath();

      try {

         if (RenderingUtil.arePopupsAllowed()) {

            RenderingUtil.ensureFilenameLimit(contentFile);

            program.execute(contentFile.getLocation().toFile().getAbsolutePath());

         } else {

            OseeLog.logf(Activator.class, Level.INFO, "Test - Opening File - [%s]" + contentFilePath);

         }
      } catch (Exception ex) {

         var workbench = PlatformUI.getWorkbench();
         var editorDescriptor = workbench.getEditorRegistry().getDefaultEditor(contentFile.getName());

         if (editorDescriptor != null) {
            try {

               var page = workbench.getActiveWorkbenchWindow().getActivePage();
               page.openEditor(new FileEditorInput(contentFile), editorDescriptor.getId());

            } catch (PartInitException | NullPointerException e) {

               //@formatter:off
               throw
                  new OseeArgumentException
                        (
                          "No program associated with the extension [%s] found on your local machine.",
                          contentFile.getFileExtension(),
                          e
                        );
               //@formatter:on
            }
         }
      }
   }

   /**
    * Generates a message stating the renderer failed to publish a document. The message is logged at the
    * {@link Level#WARNING}. If pop-ups are allowed, the message is displayed in a dialog box.
    *
    * @param renderer the render that failed to generate a document.
    * @param presentationType the {@link PresentationType} that was being generated.
    * @param branchToken the branch the artifacts that failed to render were from.
    * @param artifacts the artifacts that failed to render.
    * @param errorMessage a failure specific message from the renderer.
    * @return the generated message.
    */

   public static String displayErrorDocument(IRenderer renderer, PresentationType presentationType,
      BranchToken branchToken, List<Artifact> artifacts, String errorMessage) {

      var rendererClass = Objects.nonNull(renderer) ? renderer.getClass() : RenderingUtil.UNKNOWN_CLASS;
      var rendererName = Objects.nonNull(renderer) ? renderer.getName() : RenderingUtil.UNKNOWN_RENDERER;
      var rendererDocumentType =
         Objects.nonNull(renderer) ? renderer.getDocumentTypeDescription() : RenderingUtil.UNKNOWN_DOCUMENT_TYPE;

      //@formatter:off
      var message =
         new Message()
                .title( rendererName )
                .append( ", Failed to publish " )
                .append( rendererDocumentType )
                .append( " document." )
                .indentInc()
                .segment( "Artifacts", artifacts,   Artifact::getIdString    )
                .segment( "Branch",    branchToken, BranchToken::getIdString )
                .blank()
                .indentDec()
                .title( "Reason Follows:" )
                .blank()
                .block( errorMessage )
                .toString();
      //@formatter:on

      OseeLog.log(rendererClass, Level.WARNING, message);

      if (RenderingUtil.arePopupsAllowed()) {

         Displays.pendInDisplayThread(new Runnable() {
            @Override
            public void run() {
               MessageDialog.openError(Displays.getActiveShell(), "Publishing Error", message);
            }
         });
      }

      return message;
   }

   /**
    * Checks if the absolute path in the {@link CharSequence} maybe to long for the platform. On the first occurrence of
    * a lengthy filepath when pop-ups are enabled, the user will be warned with a pop-up dialog and the warning will be
    * logged at the {@link Level#Warning} level. After the first pop-up warning, no further pop-up dialog will occur.
    * However, all occurrences will be logged.
    *
    * @param absPath the {@link CharSequence} containing the path to check.
    * @return <code>true</code>, when the path length is considered to be safe or the parameter <code>absPath</code> is
    * <code>null</code>; otherwise, <code>false</code>.
    */

   public static boolean ensureFilenameLimit(CharSequence absPath) {

      if (Objects.isNull(absPath)) {
         return true;
      }

      var withinLimit = FilenameFactory.isInLimit(absPath);

      if (withinLimit) {
         return true;
      }

      /*
       * Warn the user that the filename length is large and may cause external applications (Word, Excel, PPT) to fail.
       * The warning is only displayed once per session; however, it is logged every time.
       */

      //@formatter:off
      var message =
         new Message()
                .title( "File path may be to long." )
                .indentInc()
                .segment( "Filename", absPath          )
                .segment( "Length",   absPath.length() )
                .block( RenderingUtil.FILENAME_WARNING_MESSAGE )
                .toString();


      if(    RenderingUtil.showAgain.get()
          && RenderingUtil.arePopupsAllowed() ) {

         Displays.pendInDisplayThread
            (
               new Runnable()
               {
                  @Override
                  public void run()
                  {
                      MessageDialog.openWarning
                         (
                            Displays.getActiveShell(),
                            "Filename Length Warning",
                            message
                         );
                  }
               }
            );

         /*
          * Disable any further pop-up waring messages.
          */

         RenderingUtil.showAgain.set(false);
      }
      //@formatter:on

      OseeLog.log(Activator.class, Level.WARNING, message);

      return false;
   }

   /**
    * Checks if the absolute path for the {@link IFile} resource maybe to long for the platform. On the first occurrence
    * of a lengthy filepath when pop-ups are enabled, the user will be warned with a pop-up dialog and the warning will
    * be logged at the {@link Level#Warning} level. After the first pop-up warning, no further pop-up dialog will occur.
    * However, all occurrences will be logged.
    *
    * @param file the {@link IFile} to check the path length for.
    * @return <code>true</code>, when the path length is considered to be safe or unable to determine the path length;
    * otherwise, <code>false</code>.
    */

   public static boolean ensureFilenameLimit(IFile file) {

      try {
         var absPath = file.getLocation().toFile().getAbsolutePath();

         return RenderingUtil.ensureFilenameLimit(absPath);

      } catch (Exception e) {

         return true;
      }

   }

   /**
    * Gets the {@link BranchToken} from each {@link Artifact} on the list, consolidates duplicates, and saves the unique
    * {@link BranchToken}s into a {@link Set}. The {@link BranchToken#SENTINEL} will be used for
    * {@link Artifact#SENTINEL} artifacts instead of the {@link CoreBranches#Common} {@link BranchToken} that is the
    * default for {@link Artifact#SENTINEL} artifacts. When the parameter <code>outputArtifactsNoNulls</code> is
    * non-<code>null</code> the non-<code>null</code> {@link Artifact} elements from the <code>artifacts</code>
    * {@link List} are added to the <code>outputArtifactsNoNulls</code> list. {@link Artifact#SENTINEL} objects will
    * also be copied into the <code>outputArtifactsNoNulls</code> {@link List}. When the <code>artifact</code>
    * {@link List} is <code>null</code> or empty, an empty {@link Set} is returned.
    *
    * @param artifacts the list of {@link Artifact}s.
    * @param outputArtifactsNoNulls when non-<code>null</code>, non-<code>null</code> {@link Artifact}s from the
    * <code>artifacts</code> {@link List} are copied to the <code>outputArtitfactsNoNulls</code> {@link List}.
    * @return a {@link Set} of the unique {@link BranchToken}s from the {@link Artifacts} on the {@link List}
    * <code>artifacts</code>.
    */

   public static Set<BranchToken> getBranchTokens(List<Artifact> artifacts, List<Artifact> outputArtifactsNoNulls) {

      if ((Objects.isNull(artifacts) || artifacts.isEmpty())) {
         return Set.of();
      }

      Stream<Artifact> stream = artifacts.stream().filter(Objects::nonNull);

      if (Objects.nonNull(outputArtifactsNoNulls)) {
         stream = stream.peek(outputArtifactsNoNulls::add);
      }

      //@formatter:off
      return
         stream
            .map
               (
                  ( artifact ) -> artifact.isValid()
                                     ? artifact.getBranchToken()
                                     : BranchToken.SENTINEL
               )
            .collect( Collectors.toSet() );
      //@formatter:on
   }

   /**
    * Get the first 15 characters of the {@link TransactionDelta}'s associated artifact's name from the first
    * {@link TransactionDeltaSupplier} in the {@link Collection}.
    *
    * @param <T> any class implementing the interface {@link TransactionDeltaSupplier}.
    * @param transactionDeltaSuppliers a {@link Collection} of {@link TransactionDeltaSupplier}s.
    * @return when an associated artifact can be obtained and that artifact has a suitable name, an {@link Optional}
    * with the first 15 characters of the associated artifact's URL safe name; otherwise, and empty {@link Optional}.
    */

   //@formatter:off
   public static <T extends TransactionDeltaSupplier> Optional<String>
      getFileNameSegmentFromFirstTransactionDeltaSupplierAssociatedArtifactName
         (
            Collection<T> transactionDeltaSuppliers
         )
   {

      try {

         if (Objects.isNull(transactionDeltaSuppliers) || transactionDeltaSuppliers.isEmpty()) {
            return Optional.empty();
         }

         var firstTransactionDeltaSupplier = transactionDeltaSuppliers.iterator().next();

         if (Objects.isNull(firstTransactionDeltaSupplier)) {
            return Optional.empty();
         }

         var transactionDelta = firstTransactionDeltaSupplier.getTxDelta();

         if (Objects.isNull(transactionDelta)) {
            return Optional.empty();
         }

         return
            RenderingUtil
               .getFileNameSegmentFromTransactionDeltaAssociatedArtifactName
                   (
                     transactionDelta
                   )
                .filter( Strings::isValidAndNonBlank )
                ;

      } catch (Exception e) {

         return Optional.empty();
      }
   }
   //@formatter:on

   /**
    * Get the first 15 characters of the {@link TransactionDelta}'s associated artifact's name.
    *
    * @param txDelta the {@link TransactionDelta} to create a filename segment from.
    * @return when an associated artifact can be obtained and that artifact has a suitable name, an {@link Optional}
    * with the first 15 characters of the associated artifact's URL safe name; otherwise, and empty {@link Optional}.
    */

   private static Optional<String> getFileNameSegmentFromTransactionDeltaAssociatedArtifactName(
      TransactionDelta txDelta) {

      if (Objects.isNull(txDelta)) {
         return Optional.empty();
      }

      try {
         var associatedArtifact = BranchManager.getAssociatedArtifact(txDelta);

         if (Objects.isNull(associatedArtifact)) {
            return Optional.empty();
         }

         var associatedArtifactName = associatedArtifact.getName();

         if (Strings.isInvalidOrBlank(associatedArtifactName)) {
            return Optional.empty();
         }

         var safeAssociatedArtifactName = FilenameFactory.makeNameSafer(associatedArtifactName);

         if (Strings.isInvalidOrBlank(safeAssociatedArtifactName)) {
            return Optional.empty();
         }

         //@formatter:off
         var shortSafeAssociatedArtifactName =
            ( safeAssociatedArtifactName.length() <= 15 )
               ? safeAssociatedArtifactName
               : safeAssociatedArtifactName.substring( 0, 15 );
         //@formatter:on

         return Optional.ofNullable(shortSafeAssociatedArtifactName);

      } catch (Exception e) {
         return Optional.empty();
      }
   }

   /**
    * When <code>artifacts</code> contains more than one element, creates an array with the following filename segments:
    * <dl>
    * <dt>&lt;branch-name&gt;</dt>
    * <dd>When, the safe <code>branchName</code> is non-<code>null</code> and non-blank.</dd>
    * <dt>"artifacts"</dt>
    * <dt>&lt;artifacts-length&gt;</dt>
    * </dl>
    * When <code>artifacts</code> contains one non-<code>null</code> artifact, creates an array with the following
    * filename segments:
    * <dl>
    * <dt>&lt;branch-name&gt;</dt>
    * <dd>When, the safe <code>branchName</code> is non-<code>null</code> and non-blank.</dd>
    * <dt>&lt;artifact-name&gt;
    * <dd>When the artifact safe name is non-<code>null</code> and non-blank.</dd>
    * <dt>&lt;artifact-id&gt;</dt>
    * <dt>&lt;transaction-id&gt;</dt>
    * <dd>When the artifact is valid; the artifact is historical or the presentation type is
    * {@link PresentationType#DIFF}; and the artifact has a valid transaction.</dd>
    * </dl>
    * When <code>artifacts</code> is empty or contains one <code>null</code> artifact, creates an array with the
    * following filename segments:
    * <dl>
    * <dt>"artifacts"</dt>
    * <dt>"0"</dt>
    * </dl>
    *
    * @param presentationType the {@link PresentationType}.
    * @param branchName the name of the branch the artifacts are on.
    * @param artifacts a {@link List} of {@link Artifact}s.
    * @return a {@link String} array of filename segments.
    */

   public static String[] getFileNameSegmentsFromArtifacts(PresentationType presentationType, String branchName,
      List<Artifact> artifacts) {

      var segments = new LinkedList<String>();

      //@formatter:off
      var safeBranchName =
         Strings.isValidAndNonBlank( branchName )
            ? FilenameFactory.makeNameSafer( branchName )
            : null;
      //@formatter:on

      if (Strings.isValidAndNonBlank(safeBranchName)) {
         segments.add(safeBranchName);
      }

      if (Objects.nonNull(artifacts) && artifacts.size() > 1) {
         segments.add("artifacts");
         segments.add(Integer.toString(artifacts.size()));
         return segments.toArray(String[]::new);
      }

      if (Objects.nonNull(artifacts) && artifacts.size() == 1) {

         var artifact = artifacts.get(0);

         if (Objects.nonNull(artifact)) {

            var safeArtifactName = FilenameFactory.makeNameSafer(artifact.getName());

            if (Strings.isValidAndNonBlank(safeArtifactName)) {
               segments.add(safeArtifactName);
            }

            var artifactIdString = artifact.getIdString();

            segments.add(artifactIdString);

            //@formatter:off
            var transaction =
                  artifact.isValid()
               && ( artifact.isHistorical() || ( presentationType == PresentationType.DIFF ) )
                     ? artifact.getTransaction()
                     : null;
            //@formatter:on

            if (Objects.nonNull(transaction) && transaction.isValid()) {
               var transactionIdString = transaction.getIdString();
               segments.add(transactionIdString);
            }

            return segments.toArray(String[]::new);
         }
      }

      segments.add("artifacts");
      segments.add("0");
      return segments.toArray(String[]::new);

   }

   /**
    * Gets the OS path string from a {@link IFile} implementation.
    *
    * @param iFile the {@link IFile} implementation.
    * @return when extraction is successful, an {@link Optional} with the OS path string; otherwise, an empty
    * {@link Optional}.
    */

   public static Optional<String> getOsString(IFile iFile) {
      try {
         return Optional.ofNullable(iFile.getLocation().toOSString());
      } catch (Exception e) {
         return Optional.empty();
      }
   }

   /**
    * Gets the OS path string from a {@link IPath} implementation.
    *
    * @param iFile the {@link IFile} implementation.
    * @return when extraction is successful, an {@link Optional} with the OS path string; otherwise, an empty
    * {@link Optional}.
    */

   public static Optional<String> getOsString(IPath iPath) {
      try {
         return Optional.ofNullable(iPath.toOSString());
      } catch (Exception e) {
         return Optional.empty();
      }
   }

   /**
    * Gets an {@link IFile} handle for a file in a sub-directory of the workspace folder for the
    * {@link PresentationType}.
    *
    * <pre>
    *    &lt;workspace&gt; "/" &lt;presentation-workfolder&gt; { "/" &lt;sub-directory&gt; } "/" &lt;filename&gt;
    * </pre>
    *
    * Where:
    * <dl>
    * <dt>workspace</dt>
    * <dd>The workspace folder is located with {@link OseeData#getFolder}.</dd>
    * <dt>presentation-workfolder</dt>
    * <dd>The presentation-workfolder name is looked up according to the specified <code>presentationType</code>.</dd>
    * <dt>sub-directory</dt>
    * <dd>The parameter <code>subFolder</code> is a relative {@link IPath} which may specify one or more levels of
    * sub-directories under the presentation-workfolder.</dd>
    * <dt>filename</dt>
    * <dd>The filename is the concatenation of <code>subFolderPrimary</code>, <code>segment2</code>, a date and time
    * segment, and a random number segment, followed by <code>segment3</code>. The filename segments are separated with
    * the "-" character and the segments are URL encoded. If any segments are invalid, that segment and it's separator
    * character are omitted from the filename.</dd>
    * </dl>
    * The presentation-workfolder is created in the workspace-folder if it does not exist. The sub-directorys, if
    * specified, are created if they do not exist. The file is not checked for existence and is not created.
    *
    * @param render the {@link FileSystemRenderer} implementation requesting the {@link IFile} handle.
    * @param presentationType the presentation-workfolder is found from the <code>presentationType</code>.
    * @param subFolder an {@link IPath} implementation containing a relative path. This parameter may be
    * <code>null</code>.
    * @param extension if specified, the file extension to use. If the {@link String} starts with '.' it will be used as
    * is; otherwise, a '.' will be append to the filename before the <code>extension</code>.
    * @param segments an array of {@link CharSequence}s to be used as filename segments. The parameter may be
    * <code>null</code> or an empty array.
    * @return an {@link IFile} handle for the specified filename.
    */

   public static Optional<IFile> getRenderFile(IRenderer renderer, PresentationType presentationType, IPath subFolder,
      CharSequence extension, CharSequence... segments) {

      //@formatter:off
      var safeFileName = FilenameFactory.create( extension, segments );

      var renderFile = RendererUtil.getRenderFile( presentationType, subFolder, safeFileName );

      RenderingUtil.setRendererResultPathReturn( renderer, renderFile );

      return Optional.ofNullable(renderFile);
      //@formatter:on
   }

   /**
    * Enables or disables pop-up dialogs for testing.
    *
    * @implNote The test rule {@link NoPopUpsRule} can be used to disable pop-up dialogs for a test or test suit that is
    * being run independently.
    * @param popupsAllowed when <code>true</code> pop-ups are allowed and when <code>false</code> pop-ups are
    * disallowed.
    */

   public static void setPopupsAllowed(boolean popupsAllowed) {
      RenderingUtil.arePopupsAllowed.set(popupsAllowed);
   }

   /**
    * Sets the {@link IRenderer} implementation's {@link RendererOption#RESULT_PATH_RETURN} option to the OS path string
    * of the <code>file</code>.
    *
    * @param renderer the {@link IRenderer} implementation.
    * @param file the {@link IFile} to get the OS path string from.
    * @return the parameter <code>file</code>.
    */

   private static IFile setRendererResultPathReturn(IRenderer renderer, IFile file) {
      try {
         renderer.setRendererOption(RendererOption.RESULT_PATH_RETURN, RenderingUtil.getOsString(file).orElseThrow());
      } catch (Exception e) {
         //@formatter:off
         throw
            new OseeCoreException
                   (
                      new Message()
                             .title( "RenderingUtil::setRendererResultPathReturn, failed to set the Renderer's \"RESULT_PATH_RETURN\" option." )
                             .indentInc()
                             .segment( "Renderer",  Objects.nonNull(renderer)
                                                       ? renderer.getName()
                                                       : "(null)"             )
                             .segment( "File Path", RenderingUtil.getOsString( file ).orElse( "(empty)" ) )
                             .reasonFollows(e)
                             .toString(),
                      e
                   );
         //@formatter:on
      }
      return file;
   }

   /**
    * Constructor is private to prevent instantiation of the class.
    */

   private RenderingUtil() {
   }

}

/* EOF */
