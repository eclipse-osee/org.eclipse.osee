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

import static org.eclipse.osee.ats.api.workdef.WidgetOption.AUTO_SAVE;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.REQUIRED_FOR_TRANSITION;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.model.ChangeTypeWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.LayoutItem;
import org.eclipse.osee.ats.api.workdef.model.WidgetDefinition;
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
      if (workDefToken.equals(DemoWorkDefinitions.WorkDef_Team_Demo_Change_Request)) {
         workDefBld.andHeader() //
            .andLayout(getChangeTypeComposite()) //
            .isShowWorkPackageHeader(false) //
            .isShowMetricsHeader(false); //
      } else {
         workDefBld.andHeader() //
            .andLayout(getChangeTypeComposite()) //
            .isShowWorkPackageHeader(false) //
            .isShowMetricsHeader(false); //
      }
   }

   public CompositeLayoutItem getChangeTypeComposite() {
      return getChangeTypeComposite(AtsAttributeTypes.PointsNumeric);
   }

   public CompositeLayoutItem getChangeTypeComposite(AttributeTypeToken pointsAttrType) {
      return new CompositeLayoutItem(11, //
         new ChangeTypeWidgetDefinition(true).andRequired(), //
         new WidgetDefinition("   ", "XLabel"), //
         new WidgetDefinition(AtsAttributeTypes.Priority, "XHyperlinkLabelValueSelectionDam", REQUIRED_FOR_TRANSITION,
            AUTO_SAVE), //
         new WidgetDefinition("   ", "XLabel"), //
         new WidgetDefinition(pointsAttrType, "XHyperlinkLabelValueSelectionDam", AUTO_SAVE), //
         new WidgetDefinition("   ", "XLabel"), //
         new WidgetDefinition(AtsAttributeTypes.NeedBy, "XDateDam", WidgetOption.END_COMPOSITE) //
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
         new WidgetDefinition("XWorkingBranchLabel", "XWorkingBranchLabel"), //
         new CompositeLayoutItem(16, //
            new WidgetDefinition("XWorkingBranchButtonCreate", "XWorkingBranchButtonCreate"), //
            new WidgetDefinition("XWorkingBranchButtonArtifactExplorer", "XWorkingBranchButtonArtifactExplorer"), //
            new WidgetDefinition("XWorkingBranchButtonChangeReport", "XWorkingBranchButtonChangeReport"), //
            new WidgetDefinition("XWorkingBranchButtonWordChangeReport", "XWorkingBranchButtonWordChangeReport"),
            new WidgetDefinition("XWorkingBranchButtonContextChangeReport", "XWorkingBranchButtonContextChangeReport"), //
            new WidgetDefinition("XWorkingBranchButtonDelete", "XWorkingBranchButtonDelete"), //
            new WidgetDefinition("XWorkingBranchButtonFavorites", "XWorkingBranchButtonFavorites"), //
            new WidgetDefinition("XWorkingBranchButtonLock", "XWorkingBranchButtonLock"), //
            new WidgetDefinition("XWorkingBranchUpdate", "XWorkingBranchUpdate"), //
            new WidgetDefinition("XWorkingBranchButtonDeleteMergeBranches", "XWorkingBranchButtonDeleteMergeBranches") //
         ) //
      );
   }

}
