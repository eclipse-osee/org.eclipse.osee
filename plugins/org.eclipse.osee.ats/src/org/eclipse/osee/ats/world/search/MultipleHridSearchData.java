/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.world.search;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.util.AtsEditor;
import org.eclipse.osee.ats.world.IWorldEditorConsumer;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class MultipleHridSearchData implements IWorldEditorConsumer {
   private String enteredIds = "";
   private List<String> ids = new ArrayList<String>();
   private boolean includeArtIds;
   private Branch branch;
   private String name;
   private WorldEditor worldEditor;
   private AtsEditor atsEditor;

   public MultipleHridSearchData(String name, AtsEditor atsEditor) {
      this.name = name;
      this.atsEditor = atsEditor;
   }

   public boolean hasValidInput() {
      return Strings.isValid(enteredIds);
   }

   public boolean isIncludeArtIds() {
      return includeArtIds;
   }

   public String getEnteredIds() {
      return enteredIds;
   }

   public List<String> getIds() {
      return ids;
   }

   public Branch getBranchForIncludeArtIds() {
      return branch;
   }

   public Branch getBranch() {
      return branch;
   }

   public String getName() {
      return name;
   }

   @Override
   public void setWorldEditor(WorldEditor worldEditor) {
      this.worldEditor = worldEditor;
   }

   @Override
   public WorldEditor getWorldEditor() {
      return worldEditor;
   }

   public void setEnteredIds(String enteredIds) {
      this.enteredIds = enteredIds;
   }

   public void setIds(List<String> ids) {
      this.ids = ids;
   }

   public void setIncludeArtIds(boolean includeArtIds) {
      this.includeArtIds = includeArtIds;
   }

   public void setBranch(Branch branch) {
      this.branch = branch;
   }

   public void setName(String name) {
      this.name = name;
   }

   public AtsEditor getAtsEditor() {
      return atsEditor;
   }

   public void setAtsEditor(AtsEditor atsEditor) {
      this.atsEditor = atsEditor;
   }

}
