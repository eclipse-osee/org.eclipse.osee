/*********************************************************************
 * Copyright (c) 2019 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.core.workdef.defaults;

import static org.eclipse.osee.ats.api.workdef.WidgetOption.SAVE;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.model.ChangeTypeWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.LayoutItem;
import org.eclipse.osee.ats.api.workdef.model.PriorityWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.WidgetDef;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

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

   public void addCompositeHeader(WorkDefBuilder workDefBld) {

      workDefBld.andHeader() //
         .andLayout(getChangeTypeComposite()) //
         .isShowMetricsHeader(false); //
   }

   public CompositeLayoutItem getChangeTypeComposite() {
      return getChangeTypeComposite(AtsAttributeTypes.PointsNumeric);
   }

   public CompositeLayoutItem getChangeTypeComposite(AttributeTypeToken pointsAttrType) {
      return new CompositeLayoutItem(11, //
         new ChangeTypeWidgetDefinition(true).andRequired(), //
         new WidgetDef("   ", "XLabel"), //
         new PriorityWidgetDefinition(true), //
         new WidgetDef("   ", "XLabel"), //
         new WidgetDef("Points", pointsAttrType, "XHyperlinkLabelValueSelectionDam", SAVE), //
         new WidgetDef("   ", "XLabel"), //
         new WidgetDef(AtsAttributeTypes.NeedBy, "XDateDam", WidgetOption.COMPOSITE_END) //
      );
   }

   // For override in sub-classes
   public LayoutItem[] getLayout() {
      return null;
   }

   @Override
   abstract public WorkDefinition build();

   protected CompositeLayoutItem getWorkingBranchWidgetComposite() {
      return new CompositeLayoutItem(2, //
         new WidgetDef("XWorkingBranchLabel", "XWorkingBranchLabel"), //
         new CompositeLayoutItem(16, //
            new WidgetDef("XWorkingBranchButtonCreate", "XWorkingBranchButtonCreate"), //
            new WidgetDef("XWorkingBranchButtonArtifactExplorer", "XWorkingBranchButtonArtifactExplorer"), //
            new WidgetDef("XWorkingBranchButtonChangeReport", "XWorkingBranchButtonChangeReport"), //
            new WidgetDef("XWorkingBranchButtonWordChangeReport", "XWorkingBranchButtonWordChangeReport"),
            new WidgetDef("XWorkingBranchButtonContextChangeReport", "XWorkingBranchButtonContextChangeReport"), //
            new WidgetDef("XWorkingBranchButtonDelete", "XWorkingBranchButtonDelete"), //
            new WidgetDef("XWorkingBranchButtonFavorites", "XWorkingBranchButtonFavorites"), //
            new WidgetDef("XWorkingBranchButtonLock", "XWorkingBranchButtonLock"), //
            new WidgetDef("XWorkingBranchUpdate", "XWorkingBranchUpdate"), //
            new WidgetDef("XWorkingBranchButtonDeleteMergeBranches", "XWorkingBranchButtonDeleteMergeBranches") //
         ) //
      );
   }

}
