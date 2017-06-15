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
import org.eclipse.osee.define.internal.Activator;
import org.eclipse.osee.define.traceability.ITraceUnitResourceLocator;
import org.eclipse.osee.define.traceability.ResourceIdentifier;
import org.eclipse.osee.define.traceability.TraceUnitExtensionManager;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditorInput;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Roberto E. Escobar
 */
public final class FindTraceUnitFromResource {

   private FindTraceUnitFromResource() {
      //
   }

   private static HashCollection<IArtifactType, ResourceIdentifier> toIdentifiers(IResource... resources) {
      HashCollection<IArtifactType, ResourceIdentifier> returnCollection =
         new HashCollection<IArtifactType, ResourceIdentifier>(false, HashSet.class);
      if (resources != null && resources.length > 0) {
         try {
            Collection<ITraceUnitResourceLocator> locators =
               TraceUnitExtensionManager.getInstance().getAllTraceUnitLocators();
            for (IResource resource : resources) {
               resourceToId(returnCollection, resource, locators);
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return returnCollection;
   }

   private static void resourceToId(HashCollection<IArtifactType, ResourceIdentifier> idStore, IResource resource, Collection<ITraceUnitResourceLocator> locators) {
      try {
         IFileStore fileStore = EFS.getStore(resource.getLocationURI());
         for (ITraceUnitResourceLocator locator : locators) {
            if (locator.isValidFile(fileStore)) {
               InputStream inputStream = null;
               try {
                  inputStream = fileStore.openInputStream(EFS.NONE, new NullProgressMonitor());
                  CharBuffer buffer = Lib.inputStreamToCharBuffer(inputStream);
                  ResourceIdentifier identifier = locator.getIdentifier(fileStore, buffer);
                  IArtifactType traceType = locator.getTraceUnitType(identifier.getName(), buffer);
                  idStore.put(traceType, identifier);
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
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
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public static void search(BranchId branch, IResource... resources) {
      HashCollection<IArtifactType, ResourceIdentifier> typeAndIds = toIdentifiers(resources);
      if (!typeAndIds.isEmpty()) {
         Set<Artifact> artifacts = new HashSet<>();
         for (IArtifactType artifactType : typeAndIds.keySet()) {
            Collection<ResourceIdentifier> items = typeAndIds.getValues(artifactType);
            if (items != null) {
               for (ResourceIdentifier resource : items) {
                  try {
                     if (GUID.isValid(resource.getGuid())) {
                        Artifact checkArtifactFromId =
                           ArtifactQuery.checkArtifactFromId(resource.getGuid(), branch, DeletionFlag.EXCLUDE_DELETED);
                        if (checkArtifactFromId != null) {
                           artifacts.add(checkArtifactFromId);
                        }
                     } else {
                        artifacts.addAll(
                           ArtifactQuery.getArtifactListFromTypeAndName(artifactType, resource.getName(), branch));
                     }
                  } catch (OseeCoreException ex) {
                     OseeLog.log(Activator.class, Level.SEVERE, ex);
                  }
               }
            }
         }

         if (!artifacts.isEmpty()) {
            openArtifacts(artifacts);
         } else {
            AWorkbench.popup("Find Trace Unit from Resource",
               String.format("Unable to find trace for: %s on branch [%s]", Arrays.deepToString(resources), branch));
         }
      } else {
         AWorkbench.popup("Find Trace Unit from Resource",
            String.format("Unable to find trace handler for: %s", Arrays.deepToString(resources)));
      }
   }

   private static void openArtifacts(final Collection<Artifact> artifacts) {
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            try {
               for (Artifact artifact : artifacts) {
                  AWorkbench.getActivePage().openEditor(new ArtifactEditorInput(artifact), ArtifactEditor.EDITOR_ID,
                     true);
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      });
   }
}
