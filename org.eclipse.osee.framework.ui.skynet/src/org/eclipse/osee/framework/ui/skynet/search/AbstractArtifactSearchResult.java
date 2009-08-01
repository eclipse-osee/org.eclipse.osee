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
package org.eclipse.osee.framework.ui.skynet.search;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditorInput;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.IFileMatchAdapter;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;

/**
 * @author Michael S. Rodgers
 */
public abstract class AbstractArtifactSearchResult extends AbstractTextSearchResult implements IEditorMatchAdapter {
   private final Match[] EMPTY_ARR = new Match[0];

   /**
    * Constructs a new <code>AbstractTextSearchResult</code>
    */
   protected AbstractArtifactSearchResult() {
      super();
   }

   public List<Artifact> getArtifactResults() {
      List<Artifact> toReturn = new ArrayList<Artifact>();
      for (Object element : getElements()) {
         if (element instanceof Artifact) {
            toReturn.add((Artifact) element);
         }
      }
      return toReturn;
   }

   //   /**
   //    * Removes the children artifacts from the search
   //    * 
   //    * @param children
   //    */
   //   public void removeArtifacts(Collection<Artifact> children) {
   //      for (Artifact artifact : children) {
   //         Match match = artifacts.get(artifact);
   //         removeMatch(match);
   //         artifacts.remove(artifact);
   //
   //         try {
   //            // remove all of its children
   //            removeArtifacts(artifact.getChildren());
   //         } catch (OseeCoreException ex) {
   //            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
   //         }
   //      }
   //   }

   public boolean hasAttributeMatches() {
      return true; //!attributes.isEmpty();
   }

   public IEditorMatchAdapter getEditorMatchAdapter() {
      return this;
   }

   public boolean isShownInEditor(Match match, IEditorPart editor) {
      IEditorInput ei = editor.getEditorInput();
      if (ei instanceof ArtifactEditorInput) {
         ArtifactEditorInput fi = (ArtifactEditorInput) ei;
         return match.getElement().equals(fi.getArtifact());
      }
      return false;
   }

   public Match[] computeContainedMatches(AbstractTextSearchResult result, IEditorPart editor) {
      IEditorInput ei = editor.getEditorInput();
      if (ei instanceof ArtifactEditorInput) {
         ArtifactEditorInput fi = (ArtifactEditorInput) ei;
         return getMatches(fi.getArtifact());
      }
      return EMPTY_ARR;
   }

   @Override
   public IFileMatchAdapter getFileMatchAdapter() {
      return null;
   }
}
