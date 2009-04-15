/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.traceability.blam;

import java.io.File;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.define.traceability.ITraceParser;
import org.eclipse.osee.define.traceability.ITraceUnitResourceLocator;
import org.eclipse.osee.define.traceability.TraceUnitExtensionManager;
import org.eclipse.osee.define.traceability.TraceUnitExtensionManager.TraceHandler;
import org.eclipse.osee.define.traceability.data.TraceMark;
import org.eclipse.osee.define.utility.IResourceHandler;
import org.eclipse.osee.define.utility.UriResourceContentFinder;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class RemoveTraceMarksFromTraceUnits extends AbstractBlam {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getName()
    */
   @Override
   public String getName() {
      return "Remove Trace Marks from Resource";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getCategories()
    */
   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Define.Trace");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getDescriptionUsage()
    */
   @Override
   public String getDescriptionUsage() {
      return "Removes trace marks from files selected.\n*** WARNING: When \"Persist Changes\" is selected, files will be modified in place.\n There is no way to undo this operation - make sure you know what you are doing. ***\n ";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getXWidgetsXml()
    */
   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append("<XWidget xwidgetType=\"XFileSelectionDialog\" displayName=\"Select Path\" />");
      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\"Select Trace Types:\"/>");
      for (TraceHandler handler : getTraceHandlers()) {
         builder.append(getOperationsCheckBoxes(handler.getName()));
      }
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"Persist Changes\" labelAfter=\"true\" horizontalLabel=\"true\" />");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"Include Sub-Folders\" labelAfter=\"true\" horizontalLabel=\"true\" />");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"File With Embedded Paths\" labelAfter=\"true\" horizontalLabel=\"true\" />");
      builder.append("</xWidgets>");
      return builder.toString();
   }

   private TraceHandler getCheckedTraceHandler(VariableMap variableMap) throws OseeArgumentException {
      List<TraceHandler> toReturn = new ArrayList<TraceHandler>();
      for (TraceHandler handler : getTraceHandlers()) {
         if (variableMap.getBoolean(handler.getName())) {
            toReturn.add(handler);
         }
      }
      if (toReturn.isEmpty()) {
         throw new OseeArgumentException("Please select a trace type");
      } else if (toReturn.size() > 1) {
         throw new OseeArgumentException("Only (1) trace type can be selected per run. Please de-select other types.");
      }
      return toReturn.get(0);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      try {
         String filePath = variableMap.getString("Select Path");
         if (!Strings.isValid(filePath)) {
            throw new OseeArgumentException("Please enter a valid path");
         }
         File file = new File(filePath);
         if (file == null || !file.exists()) {
            throw new OseeArgumentException("UI list file not accessible");
         }
         URI source = file.toURI();

         TraceHandler handler = getCheckedTraceHandler(variableMap);

         boolean isInPlaceStorageAllowed = variableMap.getBoolean("Persist Changes");
         boolean isRecursionAllowed = variableMap.getBoolean("Include Sub-Folders");
         boolean isFileWithMultiplePaths = variableMap.getBoolean("File With Embedded Paths");

         isInPlaceStorageAllowed = false; // TODO Remember to enable or nothing will be saved;

         final int TOTAL_WORK = Integer.MAX_VALUE;
         monitor.beginTask(getName(), TOTAL_WORK);

         boolean result =
               isInPlaceStorageAllowed ? MessageDialog.openConfirm(
                     PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), getName(),
                     "Are you sure you want to remove trace marks from files?") : true;
         if (result) {
            ITraceUnitResourceLocator locator = handler.getLocator();
            ITraceParser parser = handler.getParser();

            UriResourceContentFinder resourceFinder =
                  new UriResourceContentFinder(source, isRecursionAllowed, isFileWithMultiplePaths);
            resourceFinder.addLocator(locator, new TraceRemover(isInPlaceStorageAllowed, locator, parser));

            SubProgressMonitor subMonitor = new SubProgressMonitor(monitor, TOTAL_WORK);
            resourceFinder.execute(subMonitor);
         }
      } finally {
         monitor.done();
      }
   }

   private String getOperationsCheckBoxes(String value) {
      StringBuilder builder = new StringBuilder();
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"");
      builder.append(value);
      builder.append("\" labelAfter=\"true\" horizontalLabel=\"true\"/>");
      return builder.toString();
   }

   private List<TraceHandler> getTraceHandlers() {
      List<TraceHandler> handlers = new ArrayList<TraceHandler>();
      try {
         for (TraceHandler handler : TraceUnitExtensionManager.getInstance().getAllTraceHandlers()) {
            if (handler.getParser().isTraceRemovalAllowed()) {
               handlers.add(handler);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(DefinePlugin.class, Level.SEVERE, ex);
      }
      return handlers;
   }
   private final class TraceRemover implements IResourceHandler {
      private final ITraceParser traceParser;
      private final ITraceUnitResourceLocator traceUnitLocator;
      private final boolean isStorageAllowed;

      public TraceRemover(boolean isStorageAllowed, ITraceUnitResourceLocator traceUnitLocator, ITraceParser traceParser) {
         this.isStorageAllowed = isStorageAllowed;
         this.traceParser = traceParser;
         this.traceUnitLocator = traceUnitLocator;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.define.utility.IResourceHandler#onResourceFound(java.net.URI, java.lang.String, java.nio.CharBuffer)
       */
      @Override
      public void onResourceFound(URI uriPath, String name, CharBuffer fileBuffer) {
         String traceUnitType = traceUnitLocator.getTraceUnitType(name, fileBuffer);
         if (Strings.isValid(traceUnitType) && !traceUnitType.equalsIgnoreCase(ITraceUnitResourceLocator.UNIT_TYPE_UNKNOWN)) {
            if (traceParser.isTraceRemovalAllowed()) {
               Collection<TraceMark> traceMarks = traceParser.getTraceMarks(fileBuffer);
               if (!traceMarks.isEmpty()) {
                  CharBuffer modifiedBuffer = traceParser.removeTraceMarks(fileBuffer);
                  if (modifiedBuffer != null) {
                     if (isStorageAllowed) {
                        try {
                           Lib.writeCharBufferToFile(modifiedBuffer, new File(uriPath));
                           // Report files Changed
                        } catch (Exception ex) {
                           OseeLog.log(DefinePlugin.class, Level.SEVERE, ex);
                        }
                     }
                  } else {
                     // Report no change
                  }
               }
            }
         }
      }
   }
}
