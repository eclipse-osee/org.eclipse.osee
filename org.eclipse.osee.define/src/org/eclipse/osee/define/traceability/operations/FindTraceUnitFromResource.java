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
package org.eclipse.osee.define.traceability.operations;

import java.io.InputStream;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.define.traceability.ITraceUnitResourceLocator;
import org.eclipse.osee.define.traceability.TraceUnitExtensionManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditorInput;
import org.eclipse.swt.widgets.Display;

/**
 * @author Roberto E. Escobar
 */
public class FindTraceUnitFromResource {

   private FindTraceUnitFromResource() {
   }

   private static HashCollection<String, String> toIdentifiers(IResource... resources) {
      HashCollection<String, String> toReturn = new HashCollection<String, String>(false, HashSet.class);
      if (resources != null && resources.length > 0) {
         try {
            Collection<ITraceUnitResourceLocator> locators =
                  TraceUnitExtensionManager.getInstance().getAllTraceUnitLocators();
            for (IResource resource : resources) {
               resourceToId(toReturn, resource, locators);
            }
         } catch (Exception ex) {
            OseeLog.log(DefinePlugin.class, Level.SEVERE, ex);
         }
      }
      return toReturn;
   }

   private static void resourceToId(HashCollection<String, String> idStore, IResource resource, Collection<ITraceUnitResourceLocator> locators) {
      try {
         IFileStore fileStore = EFS.getStore(resource.getLocationURI());
         for (ITraceUnitResourceLocator locator : locators) {
            if (locator.isValidFile(fileStore)) {
               InputStream inputStream = null;
               try {
                  inputStream = fileStore.openInputStream(EFS.NONE, new NullProgressMonitor());
                  CharBuffer buffer = Lib.inputStreamToCharBuffer(inputStream);
                  String identifier = locator.getIdentifier(fileStore, buffer);
                  String traceType = locator.getTraceUnitType(identifier, buffer);
                  idStore.put(traceType, identifier);
               } catch (Exception ex) {
                  OseeLog.log(DefinePlugin.class, Level.SEVERE, ex);
               } finally {
                  if (inputStream != null) {
                     try {
                        inputStream.close();
                     } catch (Exception ex) {
                        // do nothing
                     }
                  }
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(DefinePlugin.class, Level.SEVERE, ex);
      }
   }

   public static void search(Branch branch, IResource... resources) {
      HashCollection<String, String> typeAndIds = toIdentifiers(resources);
      if (!typeAndIds.isEmpty()) {
         Set<Artifact> artifacts = new HashSet<Artifact>();
         for (String artifactTypeName : typeAndIds.keySet()) {
            Collection<String> items = typeAndIds.getValues(artifactTypeName);
            if (items != null) {
               for (String artifactName : items) {
                  try {
                     artifacts.addAll(ArtifactQuery.getArtifactListFromTypeAndName(artifactTypeName, artifactName, branch));
                  } catch (OseeCoreException ex) {
                     OseeLog.log(DefinePlugin.class, Level.SEVERE, ex);
                  }
               }
            }
         }

         if (!artifacts.isEmpty()) {
            openArtifacts(artifacts);
         } else {
            AWorkbench.popup("Find Trace Unit from Resource", String.format("Unable to find trace for: %s",
                  Arrays.deepToString(resources)));
         }
      } else {
         AWorkbench.popup("Find Trace Unit from Resource", String.format("Unable to find trace handler for: %s",
               Arrays.deepToString(resources)));
      }
   }

   private static void openArtifacts(final Collection<Artifact> artifacts) {
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            try {
               for (Artifact artifact : artifacts) {
                  AWorkbench.getActivePage().openEditor(new ArtifactEditorInput(artifact), ArtifactEditor.EDITOR_ID,
                        true);
               }
            } catch (Exception ex) {
               OseeLog.log(DefinePlugin.class, Level.SEVERE, ex);
            }
         }
      });
   }
}
