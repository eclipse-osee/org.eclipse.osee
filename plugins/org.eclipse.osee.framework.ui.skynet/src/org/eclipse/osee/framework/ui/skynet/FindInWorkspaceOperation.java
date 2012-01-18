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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author John Misinco
 */
public class FindInWorkspaceOperation extends AbstractOperation {

   public static interface FindInWorkspaceCollector {

      void onResource(IResource resource);

      void onNotFound(Artifact artifact);
   }

   private final List<Artifact> artifacts;
   private final FindInWorkspaceCollector collector;
   final static Pattern objectIdPattern = Pattern.compile("ObjectId\\(\"(.*)?\"");

   public FindInWorkspaceOperation(List<Artifact> artifacts, FindInWorkspaceCollector collector) {
      super("Find In Workspace", Activator.PLUGIN_ID);
      this.artifacts = artifacts;
      this.collector = collector;
   }

   private Map<String, Artifact> getGuidMap() {
      Map<String, Artifact> guids = new HashMap<String, Artifact>();
      for (Artifact art : artifacts) {
         guids.put(art.getGuid(), art);
      }
      return guids;
   }

   @Override
   protected void doWork(final IProgressMonitor monitor) throws Exception {
      final Map<String, Artifact> guids = getGuidMap();
      monitor.beginTask("Searching Java Files", guids.size());
      final NullProgressMonitor subMonitor = new NullProgressMonitor();

      SearchPattern searchPattern =
         SearchPattern.createPattern("ObjectId", IJavaSearchConstants.ANNOTATION_TYPE,
            IJavaSearchConstants.ANNOTATION_TYPE_REFERENCE, SearchPattern.R_PATTERN_MATCH);

      IJavaSearchScope workspaceScope = SearchEngine.createWorkspaceScope();
      SearchRequestor requestor = new SearchRequestor() {

         @Override
         public void acceptSearchMatch(SearchMatch match) throws CoreException {
            ICompilationUnit unit = null;
            IJavaElement jElement = JavaCore.create(match.getResource());
            if (jElement != null && jElement.exists() && jElement.getElementType() == IJavaElement.COMPILATION_UNIT) {
               unit = (ICompilationUnit) jElement;
            }

            Matcher matcher = objectIdPattern.matcher(unit.getSource());
            if (matcher.find()) {
               String uuid = matcher.group(1);
               if (guids.containsKey(uuid)) {
                  monitor.worked(1);
                  collector.onResource(unit.getResource());
                  guids.remove(uuid);
                  if (guids.isEmpty()) {
                     subMonitor.setCanceled(true);
                  }
               }
            }
         }
      };

      SearchEngine engine = new SearchEngine();
      try {
         engine.search(searchPattern, new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()},
            workspaceScope, requestor, subMonitor);
      } catch (OperationCanceledException ex) {
         //do nothings
      }

      for (Artifact artifact : guids.values()) {
         collector.onNotFound(artifact);
      }
   }
}
