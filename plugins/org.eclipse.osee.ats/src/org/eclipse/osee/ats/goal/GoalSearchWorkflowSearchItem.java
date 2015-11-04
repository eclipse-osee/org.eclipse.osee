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
package org.eclipse.osee.ats.goal;

import java.util.Collection;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.ats.world.search.GoalSearchItem;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class GoalSearchWorkflowSearchItem extends WorldEditorParameterSearchItem {

   private GoalSearchItem searchItem;

   public GoalSearchWorkflowSearchItem(String name) {
      super(name, AtsImage.GOAL);
      setShortName("Goal");
   }

   public GoalSearchWorkflowSearchItem() {
      this("Goal Search");
   }

   public GoalSearchWorkflowSearchItem(GoalSearchWorkflowSearchItem goalWorkflowSearchItem) {
      super(goalWorkflowSearchItem, AtsImage.GOAL);
      setShortName("Goal");
   }

   @Override
   public GoalSearchWorkflowSearchItem copy() {
      return new GoalSearchWorkflowSearchItem(this);
   }

   @Override
   public GoalSearchWorkflowSearchItem copyProvider() {
      return new GoalSearchWorkflowSearchItem(this);
   }

   @Override
   public String getParameterXWidgetXml() {
      getTitle().addWidget();
      getUser().addWidget(4);
      getUserType().addWidget();
      getStateType().addWidget();
      return super.getParameterXWidgetXml();
   }

   @Override
   public void setupSearch() {
      Collection<StateType> collection = getStateType().getTypes();
      searchItem = new GoalSearchItem("", this.getTitle().get(), collection.contains(StateType.Completed),
         collection.contains(StateType.Cancelled), this.getUser().get());
   }

   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException {
      return searchItem.performSearchGetResults(false);
   }

}
