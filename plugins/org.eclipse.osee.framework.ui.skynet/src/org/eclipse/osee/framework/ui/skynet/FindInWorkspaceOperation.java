/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.ws.AWorkspace;

/**
 * @author John Misinco
 */
public class FindInWorkspaceOperation extends AbstractOperation {

   List<Artifact> artifacts;
   List<IResource> matches;
   List<Artifact> notMatched;

   public FindInWorkspaceOperation(List<Artifact> artifacts, List<IResource> matches, List<Artifact> notMatched) {
      super("Find In Workspace", Activator.PLUGIN_ID);
      this.artifacts = artifacts;
      this.matches = matches;
      this.notMatched = notMatched;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      IContainer ws = ResourcesPlugin.getWorkspace().getRoot();
      for (Artifact artifact : artifacts) {
         if (artifact.getArtifactType().equals(CoreArtifactTypes.TestCase)) {
            String artifactName = artifact.getName();
            int endOfPackageName = artifactName.lastIndexOf(".");
            if (endOfPackageName != -1) {
               String packageName = artifactName.substring(0, endOfPackageName);
               String fileName = artifactName.substring(endOfPackageName + 1) + ".java";
               List<IResource> finds = new ArrayList<IResource>();
               AWorkspace.recursiveFileFind(fileName, ws, finds);
               boolean matched = false;
               for (IResource find : finds) {
                  if (find.toString().contains(packageName)) {
                     matches.add(find);
                     matched = true;
                  }
               }
               if (!matched) {
                  notMatched.add(artifact);
               }
            }
         }
      }
   }
}
