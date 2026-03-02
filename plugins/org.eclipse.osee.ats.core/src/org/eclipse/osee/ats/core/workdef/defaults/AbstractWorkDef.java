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

import static org.eclipse.osee.ats.api.util.WidgetIdAts.*;
import static org.eclipse.osee.ats.api.workdef.WidgetOption.SAVE;
import static org.eclipse.osee.framework.core.widget.WidgetId.XDateArtWidget;
import static org.eclipse.osee.framework.core.widget.WidgetId.XHyperlinkWfdForEnumAttrArtWidget;
import static org.eclipse.osee.framework.core.widget.WidgetId.XLabelWidget;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workdef.AtsWorkDefinitionToken;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;
import org.eclipse.osee.ats.api.workdef.WidgetOption;
import org.eclipse.osee.ats.api.workdef.model.ChangeTypeWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.LayoutItem;
import org.eclipse.osee.ats.api.workdef.model.PriorityWidgetDefinition;
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

      workDefBld.andHeader() //
         .andLayout(getChangeTypeComposite()) //
         .isShowMetricsHeader(false); //
   }

   public CompositeLayoutItem getChangeTypeComposite() {
      return getChangeTypeComposite(AtsAttributeTypes.PointsNumeric);
   }

   public CompositeLayoutItem getChangeTypeComposite(AttributeTypeToken pointsAttrType) {
      return new CompositeLayoutItem(11, //
         new ChangeTypeWidgetDefinition().andRequired(), //
         new WidgetDefinition("   ", XLabelWidget), //
         new PriorityWidgetDefinition(), //
         new WidgetDefinition("   ", XLabelWidget), //
         new WidgetDefinition(pointsAttrType, XHyperlinkWfdForEnumAttrArtWidget, SAVE), //
         new WidgetDefinition("   ", XLabelWidget), //
         new WidgetDefinition(AtsAttributeTypes.NeedBy, XDateArtWidget, WidgetOption.COMPOSITE_END) //
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
         new WidgetDefinition("XWorkingBranchLabelWidget", XWorkingBranchLabelWidget), //
         new CompositeLayoutItem(16, //
            new WidgetDefinition("XWorkingBranchLabelWidget", XWorkingBranchLabelWidget), //
            new WidgetDefinition("XWrkBranchButtonArtifactExplorerWidget", XWrkBranchButtonArtifactExplorerWidget), //
            new WidgetDefinition("XWrkBranchButtonChangeReportWidget", XWrkBranchButtonChangeReportWidget), //
            new WidgetDefinition("XWrkBranchButtonWordChangeReportWidget", XWrkBranchButtonWordChangeReportWidget),
            new WidgetDefinition("XWrkBranchButtonContextChangeReportWidget",
               XWrkBranchButtonContextChangeReportWidget), //
            new WidgetDefinition("XWrkBranchButtonDeleteWidget", XWrkBranchButtonDeleteWidget), //
            new WidgetDefinition("XWrkBranchButtonFavoritesWidget", XWrkBranchButtonFavoritesWidget), //
            new WidgetDefinition("XWrkBranchButtonLockWidget", XWrkBranchButtonLockWidget), //
            new WidgetDefinition("XWrkBranchUpdateWidget", XWrkBranchUpdateWidget), //
            new WidgetDefinition("XWrkBranchButtonDeleteMergeBranchesWidget", XWrkBranchButtonDeleteMergeBranchesWidget) //
         ) //
      );
   }

}
