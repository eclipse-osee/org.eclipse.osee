/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workdef.defaults;

import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractWorkDef implements IAtsWorkDefinitionBuilder {

   protected WorkDefinition workDef;
   protected AtsWorkDefinitionToken workDefToken;

   public AbstractWorkDef(AtsWorkDefinitionToken workDefToken) {
      this.workDefToken = workDefToken;
      if (workDef == null) {
         workDef = build();
      }
   }

   @Override
   abstract public WorkDefinition build();

   protected CompositeLayoutItem getWorkingBranchWidgetComposite() {
      return new CompositeLayoutItem(2, //
         new WidgetDefinition("XWorkingBranchLabel", "XWorkingBranchLabel"), //
         new CompositeLayoutItem(16, //
            new WidgetDefinition("XWorkingBranchButtonCreate", "XWorkingBranchButtonCreate"), //
            new WidgetDefinition("XWorkingBranchButtonArtifactExplorer", "XWorkingBranchButtonArtifactExplorer"), //
            new WidgetDefinition("XWorkingBranchButtonChangeReport", "XWorkingBranchButtonChangeReport"), //
            new WidgetDefinition("XWorkingBranchButtonDelete", "XWorkingBranchButtonDelete"), //
            new WidgetDefinition("XWorkingBranchButtonFavorites", "XWorkingBranchButtonFavorites"), //
            new WidgetDefinition("XWorkingBranchButtonLock", "XWorkingBranchButtonLock"), //
            new WidgetDefinition("XWorkingBranchUpdate", "XWorkingBranchUpdate"), //
            new WidgetDefinition("XWorkingBranchButtonDeleteMergeBranches", "XWorkingBranchButtonDeleteMergeBranches") //
         ) //
      );
   }

}
