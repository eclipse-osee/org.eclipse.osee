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
package org.eclipse.osee.ats.world.search;

import java.sql.SQLException;
import java.util.Collection;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;

/**
 * @author Donald G. Dunne
 */
public class StateWorldSearchItem extends WorldSearchItem {

   private final String stateClass;
   private String selectedStateClass;

   public StateWorldSearchItem(String name) {
      this(name, null);
   }

   public StateWorldSearchItem() {
      this("Search by Current State", null);

   }

   public String getStateSearchName() {
      if (stateClass != null)
         return stateClass;
      else
         return selectedStateClass;
   }

   @Override
   public String getSelectedName(SearchType searchType) {
      return String.format("%s - %s", super.getSelectedName(searchType), getStateSearchName());
   }

   public StateWorldSearchItem(String name, String stateClass) {
      super(name);
      this.stateClass = stateClass;
   }

   private String getSearchStateClass() {
      if (stateClass != null) return stateClass;
      return selectedStateClass;
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException, SQLException {
      Collection<Artifact> arts =
            ArtifactQuery.getArtifactsFromAttribute(ATSAttributes.CURRENT_STATE_ATTRIBUTE.getStoreName(),
                  getSearchStateClass() + ";%", BranchPersistenceManager.getAtsBranch());
      if (isCancelled()) return EMPTY_SET;
      return arts;

   }

   @Override
   public void performUI(SearchType searchType) throws OseeCoreException, SQLException {
      if (stateClass != null) return;
      if (searchType == SearchType.ReSearch && selectedStateClass != null) return;
      EntryDialog ed = new EntryDialog("Enter State", "Enter state name.");
      if (ed.open() == 0) {
         selectedStateClass = ed.getEntry();
         return;
      }
      cancelled = true;
   }

   /**
    * @param selectedStateClass the selectedStateClass to set
    */
   public void setSelectedStateClass(String selectedStateClass) {
      this.selectedStateClass = selectedStateClass;
   }

}
