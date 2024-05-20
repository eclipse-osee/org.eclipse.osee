/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.framework.core.publishing;

import java.io.File;
import java.util.EnumMap;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collectors;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * A static utility class for obtaining {@link IFile} and {@link IFolder} handles for rendering files.
 *
 * @implSpec The methods in this class should only work with the {@link IFile} family of classes, {@link String}, and
 * {@link CharSequence} classes to remain compatible with both OSEE Client and OSEE Server code.
 * @author Morgan E. Cook
 * @author Loren K. Ashley
 */

public class RendererUtil {

   /**
    * Array of path segments that are not allowed in a path requested by a renderer. Disallowing the ".." path segment
    * prevents a renderer from writing a file outside of the workspace folder assigned to the {@link PresentationType}.
    */

   private static String[] BAD_PATH_SEGMENTS = {".", ".."};

   /**
    * {@link Pattern} used to split a path string into segments. This pattern will split the path string with both UNIX
    * '/' and Windows '\' path delimiters.
    */

   private static Pattern PATH_SPLITTER_PATTERN = Pattern.compile("/|\\\\");

   /**
    * Cache for {@link IFolder} handles to the workspace folders associated with each member of the
    * {@link PresentationType} enumeration.
    */

   private static EnumMap<PresentationType, IFolder> workingFolderCache = new EnumMap<>(PresentationType.class);

   /**
    * Get an {@link IFolder} handle to the OSEE Client workspace directory for the specified
    * <code>presentationType</code>. If the workspace directory does not exist it will be created.
    *
    * @param presentationType the {@link PresentationType} to get the workspace directory for.
    * @return an {@link IFolder} handle to the existing or created workspace directory.
    * @throws OseeArgumentException when the <code>pesentationType</code> is <code>null</code> or does not have a
    * defined workspace directory.
    */

   public static Optional<IFolder> ensureRenderFolderExists(PresentationType presentationType) {
      //@formatter:off
      return
         ( Objects.nonNull(presentationType) && presentationType.isSubFolderDefined() )
            ? Optional.ofNullable(RendererUtil.getOrCreateFolder(presentationType))
            : Optional.empty();
      //@formatter:on
   }

   /**
    * Gets an {@link IFolder} handle to a sub-directory of <code>iFolder</code> specified by the
    * <code>subFolderName</code>. If the sub-directory does not exist, it will be created.
    *
    * @param iFolder an {@link IFolder} handle to an existing directory.
    * @param subFolderName the name of the sub-directory to <code>iFolder</code> to get an {@link IFolder} handle to.
    * @return an {@link IFolder} handle.
    * @throws OseeCoreException when looking for or creating the sub-directory fails.
    */

   private static IFolder getFolderWithCreate(IFolder iFolder, String subFolderName) {
      try {
         var subFolder = iFolder.getFolder(subFolderName);

         if (!subFolder.exists()) {
            subFolder.create(true, true, null);
         }

         return subFolder;
      } catch (Exception e) {
         //@formatter:off
         throw
            new OseeCoreException
                   (
                      new Message()
                             .title( "RendererUtil::getFolderWithCreate, Failed to create a sub-folder." )
                             .indentInc()
                             .segment( "Root Folder", iFolder )
                             .segment( "Subfolder Name", subFolderName )
                             .reasonFollows(e)
                             .toString(),
                      e
                   );
         //@formatter:on
      }
   }

   /**
    * When the {@link IFolder} handle cached for the {@link PresentationType} is invalid a new {@link IFolder} handle is
    * created for the workspace sub-directory and the workspace sub-directory is created if it does not exist. When the
    * {@link IFolder} handle that is cached is valid, the workspace sub-directory is checked for existence and created
    * if not present.
    *
    * @implNote The method is synchronized on the {@link RendererUtil#workingFolderCache} so that multiple publishing
    * threads do not collide and attempt manipulate the directory at the same time.
    * @param presentationType the {@link PresentationType} to get an {@link IFolder} handle for.
    * @return the cachedFolder.
    * @throws OseeCoreException when looking for or creating the sub-directory fails.
    */

   private static IFolder getOrCreateFolder(PresentationType presentationType) {

      try {
         synchronized (RendererUtil.workingFolderCache) {
            var cachedFolder = RendererUtil.workingFolderCache.get(presentationType);
            //@formatter:off
            var folder =
               ( Objects.nonNull( cachedFolder) && cachedFolder.exists() )
                  ? cachedFolder
                  : OseeData.getFolder( presentationType.getSubFolder() );
            //@formatter:on
            if (Objects.isNull(cachedFolder)) {
               RendererUtil.workingFolderCache.put(presentationType, folder);
            }

            return folder;
         }
      } catch (Exception e) {
         //@formatter:off
         throw
            new OseeCoreException
                   (
                      new Message()
                             .title( "RendererUtil::getOrCreateFolder, Failed to test or create a workspace presentation sub-folder." )
                             .indentInc()
                             .segment( "Presentation Type", presentationType )
                             .reasonFollows(e)
                             .toString(),
                      e
                   );
         //@formatter:on
      }
   }

   /**
    * Gets an {@link IFile} handle to the file with the name of <code>fileName</code> in the sub-directories specified
    * by <code>folderSubPath</code> of the workspace presentation folder specified by <code>presentationType</code>. If
    * the workspace presentation folder or any of the sub-directories do not exist, they will be created. The file
    * itself will not be checked for or created.
    *
    * @param presentationType the {@link PresentationType} specifies the workspace presentation folder.
    * @param presentationFolderSubPath an {@link IPath} implementation containing the path of folders under the
    * workspace presentation folder for the file. This parameter may be <code>null</code>.
    * @param fileName the name of the file.
    * @return an {@link IFile} handle to the named file.
    * @throws NullPointerException when either of the parameters <code>presentationType</code> or <code>fileName</code>
    * are <code>null</code>.
    * @throws OseeCoreException when checking for or creating a directory fails.
    * @throws IllegalArgumentException if the parameter <code>folderSubPath</code> contains an absolute path.
    */

   public static IFile getRenderFile(PresentationType presentationType, IPath folderSubPath, String fileName) {
      //@formatter:off
      Objects.requireNonNull
         (
            presentationType,
            "RendererUtil::getRenderFile, The parameter \"presentationType\" cannot be null."
         );

      Objects.requireNonNull
         (
            fileName,
            "RendererUtil::getRenderFile, The parameter \"fileName\" cannot be null."
         );

      var cursorFolder =
         RendererUtil
            .ensureRenderFolderExists( presentationType )
            .orElseThrow
               (
                  () -> new OseeCoreException
                               (
                                  new Message()
                                         .title( "RendererUtil::getRenderFile, Cannot locate presentation folder." )
                                         .indentInc()
                                         .segment( "Presentation Type",           presentationType )
                                         .segment( "Presentation Folder SubPath", folderSubPath    )
                                         .segment( "Filename",                    fileName         )
                                        .toString()
                               )
               );

      if( Objects.nonNull( folderSubPath ) ) {

         if( folderSubPath.isAbsolute() ) {
            throw
               new IllegalArgumentException
                      (
                        "RendererUtil::getRenderFile, The parameter \"presentationFolderSubPath\" cannot be absolute."
                      );
         }

         for( int i = 0, l = folderSubPath.segmentCount(); i < l; i++ ) {

            var segment = folderSubPath.segment( i );

            cursorFolder = RendererUtil.getFolderWithCreate( cursorFolder, segment );
         }
      }

      return cursorFolder.getFile( fileName );
      //@formatter:on
   }

   /**
    * Gets an {@link IFile} handle for the <code>fileName</code> file in the <code>subFolder</code> of the workspace
    * directory for the <code>presentationType</code>. If the workspace directory for the <code>presentationType</code>
    * does not exist, it is created. If the <code>subFolder</code> of the presentation type workspace directory does not
    * exist, it is created. The file specified by <code>fileName</code> is not checked for existence and is not created.
    *
    * @param presentationType the workspace directory for the presentation type is created if necessary.
    * @param subFolder the sub-directory of the presentation type workspace directory to create if necessary.
    * @param fileName the name of the file to create a {@link IFile} handle for.
    * @return when a workspace folder is defined for the {@link PresentationType}, an {@link Optional} with the
    * {@link IFile} handler for the specified file; otherwise, an empty {@link Optional}.
    * @throws OseeCoreException when a file operations exception occurs.
    */

   public static Optional<IFile> getRenderFile(PresentationType presentationType, String subFolder, String fileName) {
      //@formatter:off
      Objects.requireNonNull
         (
            presentationType,
            "RendererUtil::getRenderFile, The parameter \"presentationType\" cannot be null."
         );

      if( Strings.isInvalidOrBlank( fileName) ) {
         return Optional.empty();
      }

      if( Strings.isInvalidOrBlank( subFolder ) ) {
         return
            RendererUtil
               .ensureRenderFolderExists( presentationType )
               .map( ( iFolderWorkspace ) -> iFolderWorkspace.getFile( fileName ) )
               ;
      }

      return
         RendererUtil
            .ensureRenderFolderExists( presentationType )
            .map( ( iFolderWorkspace ) -> iFolderWorkspace.getFolder( subFolder ) )
            .map( ( iFolderSubFolder ) ->
                  {
                     if( !iFolderSubFolder.exists() ) {
                        try {
                           iFolderSubFolder.create
                              (
                                 true,   /* force      */
                                 true,   /* local      */
                                 null    /* no monitor */
                              );
                        } catch( Exception e ) {
                           throw
                              new OseeCoreException
                                     (
                                        new Message()
                                               .title( "RendererUtil::getRenderFile, Failed to create a sub-directory." )
                                               .indentInc()
                                               .segment( "IFolder", iFolderSubFolder )
                                               .reasonFollows( e )
                                               .toString(),
                                        e
                                     );
                        }
                     }

                     return iFolderSubFolder.getFile( fileName );
                  }
                );
      //@formatter:on
   }

   /**
    * Creates a {@link File} object representing the file <code>filenameSpecification</code> under the path
    * <code>pathRoot</code>. This method does not verify or create the path specified by <code>pathRoot</code>.
    *
    * @param pathRoot the path the file is file is to be under.
    * @param filenameSpecification specifies the filename.
    * @return on success an {@link Optional} containing a {@link File} representation of the path and filename;
    * otherwise, an empty {@link Optional}.
    */

   public static @NonNull Optional<File> getRenderFile(java.nio.file.@NonNull Path pathRoot,
      @NonNull FilenameSpecification filenameSpecification) {
      final var safePathRoot = Conditions.requireNonNull(pathRoot, "pathRoot");
      final var safeFilenameSpecification = Conditions.requireNonNull(filenameSpecification, "filenameSpecification");

      try {
         final var filename = safeFilenameSpecification.build();
         final var path = safePathRoot.resolve(filename);
         final var file = path.toFile();
         return Optional.of(file);
      } catch (Exception e) {
         return Optional.empty();
      }
   }

   /**
    * Splits the provided <code>pathString</code> using both the UNIX and Windows path delimiters. Each segment of the
    * path is converted into a safe name using {@link FilenameFactory#makeNameSafer}. Any segments with names that
    * degenerate into an empty string are ignored. Any path segments matching {@link RendererUtil#BAD_PATH_SEGMENTS} are
    * also ignored. The remaining segments are built into an {@link IPath} object representing the path.
    *
    * @param pathString the string to process.
    * @return an {@link IPath} representing the path.
    */

   public static IPath makeRenderPath(CharSequence pathString) {
      if (Strings.isInvalid(pathString)) {
         return null;
      }

      IPath[] pathArray = new IPath[] {new Path(Strings.EMPTY_STRING)};

      //@formatter:off
      RendererUtil.PATH_SPLITTER_PATTERN
         .splitAsStream( pathString )
         .map( FilenameFactory::makeNameSafer )
         .filter( Strings::isValidAndNonBlank )
         .filter( ( pathSegment ) -> Strings.notEquals( pathSegment, RendererUtil.BAD_PATH_SEGMENTS ) )
         .forEach
         (
            ( sb ) ->
            {
               pathArray[0] = pathArray[0].append(sb);
            }
         )
         ;
      //@formatter:on

      var path = pathArray[0];

      if (path.segmentCount() == 0) {
         return null;
      }

      return path;
   }

   /**
    * Splits the provided <code>pathString</code> using both the UNIX and Windows path delimiters. Each segment of the
    * path is converted into a safe name using {@link FilenameFactory#makeNameSafer}. Any path segments with names that
    * degenerate into an empty string are ignored. Any path segments matching {@link #BAD_PATH_SEGMENTS} are also
    * ignored. The remaining segments are build into an {@link java.nio.file.Path} object representing the path.
    * <p>
    * When the parameter <code>pathString</code> is <code>null</code> or blank, the path "." is returned.
    *
    * @param pathString the string to process.
    * @return an {@link java.nio.file.Path} representing the path.
    */

   public static java.nio.file.@NonNull Path makePath(@Nullable CharSequence pathString) {

      if (Strings.isInvalidOrBlank(pathString)) {
         return java.nio.file.Path.of(".");
      }

      //@formatter:off
      final var result =
         RendererUtil.PATH_SPLITTER_PATTERN
            .splitAsStream( pathString )
            .map( FilenameFactory::makeNameSafer )
            .filter( Strings::isBlank )
            .filter( ( segment ) -> Strings.notEquals( segment, RendererUtil.BAD_PATH_SEGMENTS ) )
            .collect( Collectors.toPath() )
            .orElse( java.nio.file.Path.of( ".") );
      //@formatter:on

      return result;
   }

}

/* EOF */
