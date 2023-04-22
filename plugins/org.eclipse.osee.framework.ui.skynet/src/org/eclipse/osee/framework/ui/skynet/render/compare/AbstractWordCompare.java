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

package org.eclipse.osee.framework.ui.skynet.render.compare;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.exception.OperationTimedoutException;
import org.eclipse.osee.framework.core.model.change.CompareData;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.publishing.RendererUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.UnmodifiedArtifactDelta;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.preferences.MsWordPreferencePage;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.framework.ui.skynet.render.WordImageChecker;
import org.eclipse.osee.framework.ui.skynet.util.IVbaDiffGenerator;
import org.eclipse.osee.framework.ui.skynet.util.WordUiUtil;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractWordCompare implements IComparator {
   protected final FileSystemRenderer renderer;
   protected final ArtifactDeltaToFileConverter converter;
   private final List<AttributeTypeId> wordAttributeType = new ArrayList<>();
   protected boolean skipDialogs;

   public AbstractWordCompare(FileSystemRenderer renderer, AttributeTypeId... wordAttributeType) {
      this.renderer = renderer;
      this.wordAttributeType.addAll(Arrays.asList(wordAttributeType));
      converter = new ArtifactDeltaToFileConverter(renderer);
   }

   protected FileSystemRenderer getRenderer() {
      return renderer;
   }

   protected IVbaDiffGenerator createGenerator(List<Artifact> artifacts, BranchId branch, PresentationType presentationType) {
      boolean show = !((boolean) renderer.getRendererOptionValue(RendererOption.NO_DISPLAY));
      boolean executeVbScript = System.getProperty("os.name").contains("Windows");
      boolean skipErrors = !((boolean) renderer.getRendererOptionValue(RendererOption.SKIP_ERRORS));
      skipDialogs = (boolean) renderer.getRendererOptionValue(RendererOption.SKIP_DIALOGS);

      boolean diffFieldCodes = !UserManager.getBooleanSetting(MsWordPreferencePage.IGNORE_FIELD_CODE_CHANGES);

      IVbaDiffGenerator diffGenerator = WordUiUtil.createScriptGenerator(presentationType == PresentationType.MERGE,
         show, presentationType == PresentationType.MERGE, executeVbScript, skipErrors, diffFieldCodes);
      return diffGenerator;
   }

   @Override
   public void compare(IProgressMonitor monitor, CompareDataCollector collector, PresentationType presentationType, ArtifactDelta artifactDelta, String pathPrefix) {
      boolean invalid = false;
      Artifact startArt = artifactDelta.getStartArtifact();
      Artifact endArt = artifactDelta.getEndArtifact();
      String paraNumStart = null;
      String paraNumEnd = null;
      if (startArt != null) {
         paraNumStart = startArt.getSoleAttributeValueAsString(CoreAttributeTypes.ParagraphNumber, null);
      }
      if (endArt != null) {
         paraNumEnd =
            artifactDelta.getEndArtifact().getSoleAttributeValueAsString(CoreAttributeTypes.ParagraphNumber, null);
      }

      if (paraNumStart != null && paraNumStart.matches(".*[a-zA-Z].*")) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP,
            String.format("Invalid Paragraph Number on Artifact [%s], cannot contain letters",
               artifactDelta.getEndArtifact().toStringWithId()));
         invalid = true;
      }
      if (paraNumEnd != null && paraNumEnd.matches(".*[a-zA-Z].*")) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP,
            String.format("Invalid Paragraph Number on Artifact [%s], cannot contain letters",
               artifactDelta.getEndArtifact().toStringWithId()));
         invalid = true;
      }

      if (!invalid) {
         Artifact artifact = artifactDelta.getStartArtifact();
         if (artifact == null) {
            artifact = artifactDelta.getEndArtifact();
         }
         BranchToken branch = artifact.getBranchToken();

         IVbaDiffGenerator diffGenerator =
            createGenerator(Collections.singletonList(artifact), branch, presentationType);

         //@formatter:off
         var resultPath =
            getDiffPath
               (
                  artifactDelta.getStartArtifact(),
                  artifactDelta.getEndArtifact(),
                  presentationType,
                  pathPrefix
               );

         var vbsPath =
            RenderingUtil
               .getRenderFile
                  (
                     renderer,
                     presentationType,
                     RendererUtil.makeRenderPath(pathPrefix),
                     "vbs",
                     branch.getShortName(),
                     "compareDocs"
                  )
               .flatMap( RenderingUtil::getOsString )
               .orElseThrow
                  (
                     () -> new OseeCoreException
                                  (
                                     new Message()
                                            .title( "AbstractWordCompare::compare, Failed to locate renderer file." )
                                            .indentInc()
                                            .segment( "Renderer",          renderer.getName()      )
                                            .segment( "Presentation Type", presentationType.name() )
                                            .segment( "Branch Identifier", branch.getIdString()    )
                                            .segment( "Path Prefix",       pathPrefix              )
                                            .toString()
                                  )
                  );

         var data = new CompareData( presentationType, pathPrefix, resultPath, vbsPath );
         //@formatter:on

         addToCompare(monitor, data, artifactDelta);

         try {
            diffGenerator.generate(monitor, data);
         } catch (OperationTimedoutException ex) {
            if (!skipDialogs) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, String.format(
                  "The View Word Change Report Timed-out for Artifact(s) [%s] on Branch [%s]", artifact, branch));
            }
         }

         collector.onCompare(data);
      }

   }

   @Override
   public void compare(CompareDataCollector collector, Artifact baseVersion, Artifact newerVersion, IFile baseFile, IFile newerFile, PresentationType presentationType, String pathPrefix) {
      //@formatter:off
      var branch =
         ( Objects.nonNull( baseVersion )
            ? baseVersion
            : newerVersion).getBranchToken();


      var resultPath =
         getDiffPath
            (
               baseVersion,
               newerVersion,
               presentationType,
               pathPrefix
            );

      var vbsPath =
         RenderingUtil
            .getRenderFile
               (
                  renderer,
                  presentationType,
                  RendererUtil.makeRenderPath(pathPrefix),
                  "vbs",
                  branch.getShortName(),
                  "compareDocs"
               )
            .flatMap( RenderingUtil::getOsString )
            .orElseThrow
               (
                  () -> new OseeCoreException
                               (
                                  new Message()
                                         .title( "AbstractWordCompare::compare, Failed to locate renderer file." )
                                         .indentInc()
                                         .segment( "Renderer",          renderer.getName()      )
                                         .segment( "Presentation Type", presentationType.name() )
                                         .segment( "Branch Identifier", branch.getIdString()    )
                                         .segment( "Path Prefix",       pathPrefix              )
                                         .toString()
                               )
               );

      var data = new CompareData( presentationType, pathPrefix, resultPath, vbsPath);
      //@formatter:on

      data.add(baseFile.getLocation().toOSString(), newerFile.getLocation().toOSString());

      IVbaDiffGenerator diffGenerator =
         createGenerator(Collections.singletonList(newerVersion), branch, presentationType);

      IProgressMonitor monitor = new NullProgressMonitor();
      try {
         diffGenerator.generate(monitor, data);
      } catch (OperationTimedoutException ex) {
         if (!skipDialogs) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, String.format(
               "The View Word Change Report Timed-out for Artifact(s) [%s] on Branch [%s]", newerVersion, branch));
         }
      }
      collector.onCompare(data);
   }

   protected String getDiffPath(Artifact baseVersion, Artifact newerVersion, PresentationType presentationType, String prefix) {
      //@formatter:off
      var artifact =
         Objects.nonNull( baseVersion )
            ? baseVersion
            : newerVersion;

      var branchToken = artifact.getBranchToken();

      var artifacts = Collections.singletonList( artifact );

      var diffPath =
         RenderingUtil
            .getRenderFile
               (
                  renderer,
                  presentationType,
                  RendererUtil.makeRenderPath(prefix),
                  "xml",
                  RenderingUtil.getFileNameSegmentsFromArtifacts( presentationType, branchToken.getShortName(), artifacts )
               )
            .flatMap( RenderingUtil::getOsString )
            .orElseThrow
               (
                  () -> new OseeCoreException
                               (
                                  new Message()
                                         .title( "AbstractWordCompare::getDiffPath, Failed to locate renderer file." )
                                         .indentInc()
                                         .segment( "Renderer",          renderer.getName()        )
                                         .segment( "Presentation Type", presentationType.name()   )
                                         .segment( "Branch Identifier", branchToken.getIdString() )
                                         .segment( "Prefix",            prefix                    )
                                         .toString()
                               )
               );

      return diffPath;
   }

   protected void addToCompare(IProgressMonitor monitor, CompareData data, ArtifactDelta artifactDelta) {
      Pair<IFile, IFile> compareFiles;
      if (artifactDelta instanceof UnmodifiedArtifactDelta) {
         compareFiles = converter.convertToFileAndCopy(data.getPresentationType(), artifactDelta);
      } else {
         Pair<String, Boolean> originalValue = null;

         Artifact baseArtifact = artifactDelta.getStartArtifact();
         Artifact newerArtifact = artifactDelta.getEndArtifact();

         Attribute<String> baseContent = getWordContent(baseArtifact);
         Attribute<String> newerContent = getWordContent(newerArtifact);

         if (!UserManager.getBooleanSetting(MsWordPreferencePage.IDENTFY_IMAGE_CHANGES)) {
            originalValue = WordImageChecker.checkForImageDiffs(baseContent, newerContent);
         }
         monitor.setTaskName(
            "Preparing comparison for: " + (newerArtifact == null ? baseArtifact.getName() : newerArtifact.getName()));

         if (artifactDelta.getStartArtifact() == artifactDelta.getBaseArtifact()) {
            compareFiles = converter.convertToFile(data.getPresentationType(), data.getPathPrefix(), artifactDelta);
         } else {
            // The artifactDelta is a 3 Way Merge
            compareFiles = handle3WayMerge(data, artifactDelta);
         }
         WordImageChecker.restoreOriginalValue(baseContent, originalValue);
      }
      //@formatter:off
      data.add
         (
            RenderingUtil.getOsString( compareFiles.getFirst()  ).orElse(null),
            RenderingUtil.getOsString( compareFiles.getSecond() ).orElse(null)
         );
      //@formatter:on
   }

   private Pair<IFile, IFile> handle3WayMerge(CompareData data, ArtifactDelta artifactDelta) {
      Pair<IFile, IFile> toReturnFiles;
      List<IFile> outputFiles = new ArrayList<>();
      converter.convertToFileForMerge(outputFiles, artifactDelta.getTxDelta(), artifactDelta.getBaseArtifact(),
         artifactDelta.getStartArtifact());
      converter.convertToFileForMerge(outputFiles, artifactDelta.getTxDelta(), artifactDelta.getBaseArtifact(),
         artifactDelta.getEndArtifact());
      // this is where we are getting the exception that the length of outputFiles is 1
      // This happens because the artifact did not exist on the previous
      // branch or was removed on the current branch
      if (outputFiles.size() == 1) {
         String outputFileName = outputFiles.get(0).getRawLocation().toOSString();
         String tempFileName = Lib.removeExtension(outputFileName);
         IFile tempFile = getEmptyFileFromName(tempFileName);
         outputFiles.add(tempFile);
      }
      toReturnFiles = new Pair<>(outputFiles.get(0), outputFiles.get(1));
      data.addMerge(outputFiles.get(0).getLocation().toOSString());

      return toReturnFiles;
   }

   private IFile getEmptyFileFromName(String fileName) {
      try {
         File tempFile = new File(fileName + ".temp.xml");
         Lib.writeStringToFile("", tempFile);
         return AIFile.constructIFile(tempFile.getPath());
      } catch (IOException ex) {
         OseeCoreException.wrap(ex);
      }
      return null;
   }

   private Attribute<String> getWordContent(Artifact artifact) {
      Attribute<String> toReturn = null;
      if (artifact != null && !artifact.isDeleted()) {
         for (AttributeTypeId wordAttr : wordAttributeType) {
            toReturn = artifact.getSoleAttribute(wordAttr);
            if (toReturn != null) {
               break;
            }
         }
      }
      return toReturn;
   }
}
