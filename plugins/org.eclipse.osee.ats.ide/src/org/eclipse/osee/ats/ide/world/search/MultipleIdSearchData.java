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
package org.eclipse.osee.ats.ide.world.search;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.ide.util.AtsEditor;
import org.eclipse.osee.ats.ide.world.IWorldEditorConsumer;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class MultipleIdSearchData implements IWorldEditorConsumer {
   private String enteredIds = "";
   private final List<String> ids = new ArrayList<>();
   private boolean includeArtIds;
   private BranchId branch;
   private String name;
   private WorldEditor worldEditor;
   private final AtsEditor atsEditor;

   public MultipleIdSearchData(String name, AtsEditor atsEditor) {
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

   private void extractIds() {
      for (String str : getEnteredIds().split(",")) {
         str = str.replaceAll("^\\s+", "");
         str = str.replaceAll("\\s+$", "");
         if (!str.equals("")) {
            ids.add(str);
         }
         // allow for lower case ats ids
         if (str.length() == 5 && !ids.contains(str.toUpperCase())) {
            ids.add(str.toUpperCase());
         }
      }
   }

   public List<String> getIds() {
      if (ids.isEmpty()) {
         extractIds();
      }
      return ids;
   }

   public BranchId getBranchForIncludeArtIds() {
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

   public void setIncludeArtIds(boolean includeArtIds) {
      this.includeArtIds = includeArtIds;
   }

   public void setBranch(BranchId branch) {
      this.branch = branch;
   }

   public void setName(String name) {
      this.name = name;
   }

   public AtsEditor getAtsEditor() {
      return atsEditor;
   }

}
