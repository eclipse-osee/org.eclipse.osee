/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.ide.traceability.blam;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.osee.define.ide.traceability.ResourceIdentifier;
import org.eclipse.osee.define.ide.traceability.TestUnitTagger;
import org.eclipse.osee.define.ide.utility.IResourceHandler;
import org.eclipse.osee.define.ide.utility.IResourceLocator;
import org.eclipse.osee.define.ide.utility.UriResourceContentFinder;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author John Misinco
 */
@SuppressWarnings("deprecation")
public class RemoveObjectIdsFromTraceUnits extends AbstractBlam {

   @Override
   public String getName() {
      return "Remove ObjectIds from Tests";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Define.Trace");
   }

   @Override
   public String getDescriptionUsage() {
      return "Removes ObjectIds from files/folders selected.";
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append(
         "<XWidget xwidgetType=\"XLabel\" displayName=\"Select File Or Folder (file can have a list of folders separated by newlines)\"/>");
      builder.append("<XWidget xwidgetType=\"XFileSelectionDialog\" displayName=\"Select File\" />");
      builder.append("<XWidget xwidgetType=\"XDirectorySelectionDialog\" displayName=\"Select Folder\" />");
      builder.append(
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Include Sub-Folders\" labelAfter=\"true\" horizontalLabel=\"true\" />");
      builder.append(
         "<XWidget xwidgetType=\"XCheckBox\" displayName=\"File With Embedded Paths\" labelAfter=\"true\" horizontalLabel=\"true\" />");
      builder.append("</xWidgets>");
      return builder.toString();
   }

   private void checkPath(String filePath, String type) {
      if (!Strings.isValid(filePath)) {
         throw new OseeArgumentException("Please enter a valid %s path", type);
      }
      File file = new File(filePath);
      if (!file.exists()) {
         throw new OseeArgumentException("%s path [%s] is not accessible", type, filePath);
      }
   }

   private URI getSourceURI(VariableMap variableMap) {
      String filePath = variableMap.getString("Select File");
      String folderPath = variableMap.getString("Select Folder");

      String pathToUse = null;
      if (Strings.isValid(folderPath) && Strings.isValid(filePath)) {
         throw new OseeArgumentException("Enter file or folder but not both");
      } else if (Strings.isValid(folderPath)) {
         checkPath(folderPath, "folder");
         pathToUse = folderPath;
      } else {
         checkPath(filePath, "file");
         pathToUse = filePath;
      }
      return new File(pathToUse).toURI();
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      final URI source = getSourceURI(variableMap);
      final boolean isRecursionAllowed = variableMap.getBoolean("Include Sub-Folders");
      final boolean isFileWithMultiplePaths = variableMap.getBoolean("File With Embedded Paths");

      final int TOTAL_WORK = Integer.MAX_VALUE;
      monitor.beginTask(getName(), TOTAL_WORK);

      UriResourceContentFinder resourceFinder =
         new UriResourceContentFinder(Arrays.asList(source), isRecursionAllowed, isFileWithMultiplePaths);
      resourceFinder.addLocator(new IResourceLocator() {

         @Override
         public boolean isValidDirectory(IFileStore fileStore) {
            return true;
         }

         @Override
         public boolean isValidFile(IFileStore fileStore) {
            return fileStore.getName().endsWith(".java");
         }

         @Override
         public boolean hasValidContent(CharBuffer fileBuffer) {
            return true;
         }

         @Override
         public ResourceIdentifier getIdentifier(IFileStore fileStore, CharBuffer fileBuffer) throws Exception {
            return new ResourceIdentifier(fileStore.getName());
         }
      }, new TraceRemover());

      SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, TOTAL_WORK);
      resourceFinder.execute(subMonitor);
      monitor.done();

   }

   private static final class TraceRemover implements IResourceHandler {
      private final TestUnitTagger tagger = TestUnitTagger.getInstance();

      @Override
      public void onResourceFound(URI uriPath, String name, CharBuffer fileBuffer) {
         try {
            tagger.removeSourceTag(uriPath);
         } catch (IOException ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      }
   }

   @Override
   public String getTarget() {
      return TARGET_ALL;
   }

}
