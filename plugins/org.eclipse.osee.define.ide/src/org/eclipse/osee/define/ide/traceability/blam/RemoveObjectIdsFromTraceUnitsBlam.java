/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.define.ide.traceability.blam;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.osee.define.ide.traceability.ResourceIdentifier;
import org.eclipse.osee.define.ide.traceability.TestUnitTagger;
import org.eclipse.osee.define.ide.utility.IResourceHandler;
import org.eclipse.osee.define.ide.utility.IResourceLocator;
import org.eclipse.osee.define.ide.utility.UriResourceContentFinder;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.osgi.service.component.annotations.Component;

/**
 * @author John Misinco
 */
@SuppressWarnings("deprecation")
@Component(service = AbstractBlam.class, immediate = true)
public class RemoveObjectIdsFromTraceUnitsBlam extends AbstractBlam {

   private static final String BLAM_NAME = "Remove ObjectIds from Tests";
   static final String INCLUDE_SUBFOLDERS = "Include Sub-Folders";
   static final String FILE_WITH_EMBEDDED_PATHS = "File With Embedded Paths";

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public RemoveObjectIdsFromTraceUnitsBlam() {
      super(BLAM_NAME, CoreImage.TRACE);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andLabelWidget("Select File Or Folder (file can have a list of folders separated by newlines)");
      wb.andFileSelWidget(XWidgetLabels.SELECT_FILE);
      wb.andDirSelWidget(XWidgetLabels.SELECT_DIR);
      wb.andCheckBoxWidget(INCLUDE_SUBFOLDERS);
      wb.andCheckBoxWidget(FILE_WITH_EMBEDDED_PATHS);
      return wb.getXWidgetDatas();
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      final URI source = getSourceURI(variableMap);
      final boolean isRecursionAllowed = variableMap.getBoolean(INCLUDE_SUBFOLDERS);
      final boolean isFileWithMultiplePaths = variableMap.getBoolean(FILE_WITH_EMBEDDED_PATHS);

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

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

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
      String filePath = variableMap.getString(XWidgetLabels.SELECT_FILE);
      String folderPath = variableMap.getString(XWidgetLabels.SELECT_DIR);

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

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.TRACE);
   }

   @Override
   public String getDescriptionUsage() {
      return "Removes ObjectIds from files/folders selected.";
   }

}
