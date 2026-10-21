/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.core.demo.test;

import static org.eclipse.osee.ats.api.data.AtsAttributeTypes.*;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XXWorkPackageEnumArtWidget;
import static org.eclipse.osee.ats.api.util.WidgetIdAts.XXWorkingBranchWfeWidget;
import static org.eclipse.osee.framework.core.widget.WidgetId.*;
import static org.eclipse.osee.framework.core.widget.XOption.*;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsUserGroups;
import org.eclipse.osee.ats.api.demo.DemoWorkDefinitions;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.osee.ats.api.workdef.StateToken;
import org.eclipse.osee.ats.api.workdef.StateType;
import org.eclipse.osee.ats.api.workdef.model.AttrWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.CompositeLayoutItem;
import org.eclipse.osee.ats.api.workdef.model.LabelWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.RuleDefinitionOption;
import org.eclipse.osee.ats.api.workdef.model.SignDateWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.SpaceWidgetDef;
import org.eclipse.osee.ats.api.workdef.model.WidgetDef;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.core.task.TaskSetDefinitionTokensDemo;
import org.eclipse.osee.ats.core.workdef.builder.WorkDefBuilder;
import org.eclipse.osee.ats.core.workdef.defaults.AbstractWorkDef;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.widget.XOption;
import org.osgi.service.component.annotations.Component;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionBuilder;
import org.eclipse.osee.ats.core.demo.DemoUtil;

/**
 * This work def is used to test and demo all the available widgets. It's tied to the Tools/Website AI and Team so it
 * won't interfere with other tests and demoing.
 *
 * @author Donald G. Dunne
 */
@Component(service = IAtsWorkDefinitionBuilder.class, immediate = true)
public class WorkDefTeamDemoXWidgetTest extends AbstractWorkDef {

   public WorkDefTeamDemoXWidgetTest() {
      super(DemoWorkDefinitions.WorkDefTeamXDemoWidgetTest);
   }

   @Override
   public WorkDefinition build() {
      WorkDefBuilder bld = new WorkDefBuilder(workDefToken);

      bld.andPointsAttributeType(PointsEnum);
      bld.andCustomHeader() //
         .andLayout( //
            bld.getChangeTypeComposite(), //
            new AttrWidgetDef(AtsAttributeTypes.WorkPackage, XXWorkPackageEnumArtWidget) //
         ) //
         .showMetricsHeader(true);

      bld.andState(1, "Analyze", StateType.Working).isStartState() //
         .andToStates(StateToken.Completed, StateToken.Cancelled) //
         .andColor(StateColor.BLACK) //
         .andLayout( //

            new LabelWidgetDef(
               "This Workflow Definition and Action are intended to develop, test and demo the available widgets.").set(
                  XOption.LABEL_RED),

            new WidgetDef("Layouts", XXTextWidget).set(LABEL_BLUE).set(XOption.FILL_BOTH, XOption.READ,
               XOption.NO_INLINE_EDIT).andDefaultValue(
                  "To Start, you can see the general layout capabilities in the header above.\n" //
                     + "This is done with simple Java dot.notation and does not require \nknowledge of Eclipse, " //
                     + "SWT or it's powerful but complex layout architecture."),

            new SpaceWidgetDef(),

            //////////////////////////////////
            // Labels
            //////////////////////////////////
            new LabelWidgetDef("Labels").set(LABEL_BLUE, LABEL_BOLD),

            new SpaceWidgetDef(),

            new LabelWidgetDef("Basic Label"),
            new LabelWidgetDef("Label in color and bold...").set(LABEL_YELLOW, LABEL_BOLD), //
            new WidgetDef("Standard Label", XXLabelValueWidget).andDefaultValue(
               "With a Value, Image and Tooltip (if you hover over label or value)").andImage(
                  AtsImage.PLAY_GREEN).andToolTip("This is the tooltip"), //

            new SpaceWidgetDef(),

            //////////////////////////////////
            // Text Boxes (XXTextWidget, XXLongSTextWidget, XXStringSTextHEditWidget)
            //////////////////////////////////
            new LabelWidgetDef("StyledText Entry").set(LABEL_BLUE, LABEL_BOLD),

            new WidgetDef("Read - Label/Value", XXTextWidget, VAL_SINGLE_TEXT, READ, NO_BORDER).andDefaultValue(
               "Value with spll check"),
            new LabelWidgetDef("   XXTextWidget: SINGLE_LINE, READ, NO_BORDER"),

            new SpaceWidgetDef(),

            // Hyperlink labels
            new WidgetDef("Write - Hyperlink Label: Single-Line Value", XXTextWidget, VAL_SINGLE_TEXT, WRITE,
               VAL_SINGLE_SEL, NO_BORDER).andDefaultValue("My Default all spll chk hav thees"),
            new LabelWidgetDef("   XXTextWidget: SINGLE_LINE, WRITE, SINGLE_SEL, NO_BORDER"),

            new WidgetDef("Write - Hyperlink Label: Multi-Line Value", XXTextWidget, VAL_MULTI_TEXT, VERT_LABEL,
               FILL_VERT, FILL_HORZ, WRITE, VAL_SINGLE_SEL).andDefaultValue("My Default"),
            new LabelWidgetDef("   XXTextWidget: MULTI_LINE, VERT_LABEL, FILL_VERT, FILL_HORZ, WRITE, SINGLE_SEL"),

            new SpaceWidgetDef(),

            // Inline edit - Single
            new WidgetDef("Write - Normal Label: Single-Line InLine Edit Value", XXTextWidget, VAL_SINGLE_TEXT, WRITE,
               VAL_SINGLE_SEL, INLINE_EDIT).andDefaultValue("My Default"),
            new LabelWidgetDef("   XXTextWidget: SINGLE_LINE, WRITE, SINGLE_SEL, INLINE_EDIT"),

            new WidgetDef("Write - Normal Label: Single-Line InLine Edit Value - Fill", XXTextWidget, VAL_SINGLE_TEXT,
               WRITE, VAL_SINGLE_SEL, INLINE_EDIT, FILL_HORZ).andDefaultValue("My Default"),
            new LabelWidgetDef("   XXTextWidget: SINGLE_LINE, WRITE, SINGLE_SEL, INLINE_EDIT, FILL_HORZ"),

            new SpaceWidgetDef(),

            // Inline edit - Multi
            new WidgetDef("Write - Normal Label: Multi-Line InLine Edit Value", XXTextWidget, VAL_MULTI_TEXT,
               INLINE_EDIT, VERT_LABEL, FILL_VERT, WRITE, VAL_SINGLE_SEL).andDefaultValue("My Default"),
            new LabelWidgetDef("   XXTextWidget: MULTI_LINE, INLINE_EDIT, VERT_LABEL, FILL_VERT, WRITE, SINGLE_SEL"),

            new WidgetDef("Write - Normal Label: Multi-Line InLine Edit Value - Fill", XXTextWidget, VAL_MULTI_TEXT,
               INLINE_EDIT, VERT_LABEL, FILL_VERT, FILL_HORZ, WRITE, VAL_SINGLE_SEL).andDefaultValue(
                  "My Default and dunt forget splling chk"),
            new LabelWidgetDef(
               "   XXTextWidget: MULTI_LINE, INLINE_EDIT, VERT_LABEL, FILL_VERT, FILL_HORZ, WRITE, SINGLE_SEL"),

            // Long styled text (multi-line, fill vertical by default)
            new WidgetDef("Long Text Entry", XXLongSTextWidget, FILL_VERT).andDefaultValue(
               "This is a long text widget that fills vertically"),
            new LabelWidgetDef("   XXLongSTextWidget: FILL_VERT"),

            // String styled text with history edit
            new WidgetDef("String with History Edit", XXStringSTextHEditWidget, FILL_HORZ).andDefaultValue(
               "Editable string with history"),
            new LabelWidgetDef("   XXStringSTextHEditWidget: FILL_HORZ"),

            new SpaceWidgetDef(),

            //////////////////////////////////
            // Numeric Entry (XXIntegerSTextWidget, XXDoubleSTextWidget, XXPercentSTextWidget)
            //////////////////////////////////
            new LabelWidgetDef("Numeric Entry").set(LABEL_BLUE, LABEL_BOLD),

            new WidgetDef("Integer Value", XXIntegerSTextWidget, FILL_HORZ).andDefaultValue("42"),
            new LabelWidgetDef("   XXIntegerSTextWidget: FILL_HORZ"),

            new WidgetDef("Double Value", XXDoubleSTextWidget, FILL_HORZ).andDefaultValue("3.14"),
            new LabelWidgetDef("   XXDoubleSTextWidget: FILL_HORZ"),

            new WidgetDef("Percent Value", XXPercentSTextWidget, FILL_HORZ).andDefaultValue("75"),
            new LabelWidgetDef("   XXPercentSTextWidget: FILL_HORZ"),

            new SpaceWidgetDef(),

            //////////////////////////////////
            // Checkboxes (XXCheckBoxWidget, XXBooleanWidget)
            //////////////////////////////////
            new LabelWidgetDef("Checkboxes and Booleans").set(LABEL_BLUE, LABEL_BOLD),
            new AttrWidgetDef(AllowWebExport), //
            new AttrWidgetDef("Crash Display w/ User Group w/o Joe", CrashOrBlankDisplay,
               XXCheckBoxWidget).andUserGroup(AtsUserGroups.PeerReviewCreationNotify), //
            new LabelWidgetDef("   XXCheckBoxWidget"),

            new WidgetDef("Boolean Yes/No/Unset", XXBooleanWidget).andDefaultValue("true"),
            new LabelWidgetDef("   XXBooleanWidget"),

            new SpaceWidgetDef(),

            //////////////////////////////////
            // Date Selection (XXDateSelWidget)
            //////////////////////////////////
            new LabelWidgetDef("Date Selection").set(LABEL_BLUE, LABEL_BOLD),

            new AttrWidgetDef(NeedBy), //
            new AttrWidgetDef(EstimatedCompletionDate), //

            new SpaceWidgetDef(),

            //////////////////////////////////
            // String Selection (XXStringsWidget, XXStringsEnumArtWidget)
            //////////////////////////////////
            new LabelWidgetDef("String Selection Widgets").set(LABEL_BLUE, LABEL_BOLD),

            new AttrWidgetDef(Priority), //
            new AttrWidgetDef(ChangeType), //
            new WidgetDef(XXWorkPackageEnumArtWidget), //

            new SpaceWidgetDef(),

            //////////////////////////////////
            // List Widgets (XXListWidget, XXListDropWidget, XXListRelationWidget)
            //////////////////////////////////
            new LabelWidgetDef("List Widgets").set(LABEL_BLUE, LABEL_BOLD),

            new WidgetDef("List Selection", XXListWidget, VAL_MULTI_SEL, FILL_VERT),
            new LabelWidgetDef("   XXListWidget: MULTI_SEL, FILL_VERT"), new SpaceWidgetDef(),

            new WidgetDef("Drag/Drop Artifact List", XXListArtifactDropWidget, VAL_MULTI_SEL, FILL_VERT),
            new LabelWidgetDef("   XXListDropWidget: MULTI_SEL, FILL_VERT"), new SpaceWidgetDef(),

            new WidgetDef("Relation List", XXListRelationWidget, VAL_MULTI_SEL, FILL_VERT),
            new LabelWidgetDef("   XXListRelationWidget: MULTI_SEL, FILL_VERT"),

            new SpaceWidgetDef(),

            //////////////////////////////////
            // Artifact Selection (XXArtifactSelWidget, XXArtifactRefWidget, XXArtifactListWidget, XXChildSelWidget)
            //////////////////////////////////
            new LabelWidgetDef("Artifact Selection Widgets").set(LABEL_BLUE, LABEL_BOLD),

            new WidgetDef("Artifact Selection", XXArtifactSelWidget), new LabelWidgetDef("   XXArtifactSelWidget"),

            new WidgetDef("Artifact Reference", XXArtifactRefWidget), new LabelWidgetDef("   XXArtifactRefWidget"),

            new WidgetDef("Artifact List", XXArtifactListWidget, VAL_MULTI_SEL, FILL_VERT),
            new LabelWidgetDef("   XXArtifactListWidget: MULTI_SEL, FILL_VERT"),

            new WidgetDef("Child Selection", XXChildSelWidget), new LabelWidgetDef("   XXChildSelWidget"),

            new SpaceWidgetDef(),

            //////////////////////////////////
            // Type Selection (XXArtifactTypeWidget, XXAttributeTypeWidget)
            //////////////////////////////////
            new LabelWidgetDef("Type Selection Widgets").set(LABEL_BLUE, LABEL_BOLD),

            new WidgetDef("Artifact Type", XXArtifactTypeWidget), new LabelWidgetDef("   XXArtifactTypeWidget"),

            new WidgetDef("Attribute Type", XXAttributeTypeWidget), new LabelWidgetDef("   XXAttributeTypeWidget"),

            new SpaceWidgetDef(),

            //////////////////////////////////
            // Branch Widgets (XXBranchListWidget, XXBranchViewWidget, XXBranchAndViewWidget)
            //////////////////////////////////
            new LabelWidgetDef("Branch Widgets").set(LABEL_BLUE, LABEL_BOLD),

            new WidgetDef("Branch List", XXBranchSelWidget), //
            new LabelWidgetDef("   XXBranchListWidget"),

            new WidgetDef("Branch View", XXBranchViewWidget), //
            new LabelWidgetDef("   XXBranchViewWidget"),

            new WidgetDef("Branch and View", XXBranchAndViewSelWidget), new LabelWidgetDef("   XXBranchAndViewWidget"),

            new SpaceWidgetDef(),

            //////////////////////////////////
            // User Selection (XXUserTokenWidget)
            //////////////////////////////////
            new LabelWidgetDef("User Widgets").set(LABEL_BLUE, LABEL_BOLD),

            new WidgetDef("User Selection", XXUserTokenWidget, VAL_SINGLE_SEL, ACTIVE),
            new LabelWidgetDef("   XXUserTokenWidget: SINGLE_SEL, ACTIVE"),

            new SpaceWidgetDef(),

            //////////////////////////////////
            // Button Widgets (XXButtonHyperWidget, XXButtonPushWidget)
            //////////////////////////////////
            new LabelWidgetDef("Button Widgets").set(LABEL_BLUE, LABEL_BOLD),

            new WidgetDef("Hyperlink Button", XXButtonHyperWidget), //
            new LabelWidgetDef("   XXButtonHyperWidget"),

            new WidgetDef("Push Button", XXButtonPushWidget), new LabelWidgetDef("   XXButtonPushWidget"),

            new SpaceWidgetDef(),

            //////////////////////////////////
            // File/Directory Selection (XXFileSelWidget, XXDirSelWidget)
            //////////////////////////////////
            new LabelWidgetDef("File/Directory Selection").set(LABEL_BLUE, LABEL_BOLD),

            new WidgetDef("File Selection", XXFileSelWidget, FILL_HORZ),
            new LabelWidgetDef("   XXFileSelWidget: FILL_HORZ"),

            new WidgetDef("Directory Selection", XXDirSelWidget, FILL_HORZ),
            new LabelWidgetDef("   XXDirSelWidget: FILL_HORZ"),

            new SpaceWidgetDef(),

            //////////////////////////////////
            // Computed Characteristic (XXComputedCharacteristicWidget)
            //////////////////////////////////
            new LabelWidgetDef("Computed Widgets").set(LABEL_BLUE, LABEL_BOLD),

            new WidgetDef("Computed Characteristic", XXComputedCharacteristicWidget),
            new LabelWidgetDef("   XXComputedCharacteristicWidget"),

            new SpaceWidgetDef(),

            //////////////////////////////////
            // Map Entry (XXMapEntryWidget)
            //////////////////////////////////
            new LabelWidgetDef("Map Entry Widgets").set(LABEL_BLUE, LABEL_BOLD),

            new WidgetDef("Map Entry", XXMapEntryWidget, FILL_HORZ),
            new LabelWidgetDef("   XXMapEntryWidget: FILL_HORZ"),

            new SpaceWidgetDef(),

            //////////////////////////////////
            // Flat Attrs (XXFlatAttrsWidget)
            //////////////////////////////////
            new LabelWidgetDef("Flat Attributes Widget").set(LABEL_BLUE, LABEL_BOLD),

            new AttrWidgetDef("Flat Attributes", AtsAttributeTypes.PcrId, XXFlatAttrsWidget, FILL_VERT),
            new LabelWidgetDef("   XXFlatAttrsWidget: FILL_VERT"),

            new SpaceWidgetDef(),

            //////////////////////////////////
            // Stacked Widget (XXStackedWidget)
            //////////////////////////////////
            new LabelWidgetDef("Stacked Widget").set(LABEL_BLUE, LABEL_BOLD),

            new AttrWidgetDef("Stacked", CoreAttributeTypes.StaticId, XXStackedWidget, FILL_VERT),
            new LabelWidgetDef("   XXStackedWidget: FILL_VERT"),

            new SpaceWidgetDef(),

            //////////////////////////////////
            // Composite Layouts
            //////////////////////////////////
            new LabelWidgetDef("Composite Layouts").set(LABEL_BLUE, LABEL_BOLD),

            new CompositeLayoutItem(4, //
               new WidgetDef("Col 1", XXTextWidget, VAL_SINGLE_TEXT, FILL_HORZ).andDefaultValue("A"), //
               new WidgetDef("Col 2", XXTextWidget, VAL_SINGLE_TEXT, FILL_HORZ).andDefaultValue("B"), //
               new WidgetDef("Col 3", XXIntegerSTextWidget, FILL_HORZ).andDefaultValue("100"), //
               new WidgetDef("Col 4", XXDoubleSTextWidget, FILL_HORZ).andDefaultValue("1.5") //
            ),

            new SpaceWidgetDef(),

            //////////////////////////////////
            // ATS-Specific Widgets
            //////////////////////////////////
            new LabelWidgetDef("ATS-Specific Widgets").set(LABEL_BLUE, LABEL_BOLD),

            new WidgetDef(WidgetIdAts.XXAssigneesWidget), new WidgetDef(WidgetIdAts.XXOriginatorWidget),
            new WidgetDef(WidgetIdAts.XXTargetedVersionWidget), new WidgetDef(WidgetIdAts.XXRelatedStateWfeWidget),
            new WidgetDef(XXWorkingBranchWfeWidget), new WidgetDef(WidgetIdAts.XXPointsEnumWidget),
            new WidgetDef(WidgetIdAts.XXPriorityWidget), new WidgetDef(WidgetIdAts.XXChangeTypeWidget),
            new WidgetDef(WidgetIdAts.XXPercentWfeWidget), new WidgetDef(WidgetIdAts.XXHoursSpentWfeWidget),
            new WidgetDef(WidgetIdAts.XXBlockWfeWidget), new WidgetDef(WidgetIdAts.XXHoldWfeWidget),

            new SpaceWidgetDef(),

            //////////////////////////////////
            // Supporting
            //////////////////////////////////
            new LabelWidgetDef("Supporting....").set(LABEL_BLUE, LABEL_BOLD),
            new WidgetDef("In-Line - Text Entry, Type in the box", XXTextWidget, VAL_SINGLE_TEXT, FILL_HORZ),
            new LabelWidgetDef("   XXTextWidget: SINGLE_LINE, FILL_HORZ"), new SpaceWidgetDef(),

            new WidgetDef("Hyperlink - Text Entry, Have to select label", XXTextWidget, VAL_SINGLE_TEXT, FILL_HORZ,
               NO_BORDER),
            new LabelWidgetDef("   XXTextWidget: SINGLE_LINE, FILL_HORZ, NO_BORDER"), new SpaceWidgetDef(),

            new WidgetDef("Hyperlink Label - Multi Line Text Entry", XXTextWidget, NO_INLINE_EDIT, FILL_VERT, REQUIRED), //
            new LabelWidgetDef("   XXTextWidget: NO_INLINE_EDIT, FILL_VERT, REQUIRED"),

            new SpaceWidgetDef(),

            new AttrWidgetDef(ProposedResolution), //
            new AttrWidgetDef(ValidationRequired), //
            new SignDateWidgetDef("Lead Sign Off", SignedOffBy, SignedOffByDate), //
            new SignDateWidgetDef("Manager Signoff", ManagerSignedOffBy, ManagerSignedOffByDate), //
            new SignDateWidgetDef("Approved Hours", ApproveRequestedHoursBy, ApproveRequestedHoursByDate) //
               .andImage(AtsImage.CHECK_CLIPBOARD));

      bld.andState(2, "Completed", StateType.Completed) //
         .andRules(RuleDefinitionOption.AddDecisionValidateBlockingReview) //
         .andColor(StateColor.BLACK) //
         .andTransitionListener(TaskSetDefinitionTokensDemo.SawCreateTasksFromReqChanges);

      bld.andState(3, "Cancelled", StateType.Cancelled) //
         .andColor(StateColor.BLACK);

      return bld.getWorkDefinition();
   }

   @Override
   public boolean isApplicable() {
      return DemoUtil.isDemoDatabase();
   }
}
