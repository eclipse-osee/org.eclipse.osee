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
package org.eclipse.osee.ats.rest.internal.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * See ConvertWorkDefinitionsToJava for explanation of what this operation does.
 *
 * @author Donald G. Dunne
 */
public class ConvertWorkDefinitionsToJavaOperation {

   private final AtsApi atsApi;
   Pattern idPattern = Pattern.compile("id \"(\\d+)\"");
   List<String> javaStates = new ArrayList<>();
   Set<String> stateNames = new HashSet<>();

   public ConvertWorkDefinitionsToJavaOperation(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public XResultData convert(XResultData rd) {

      rd.error("Code must be revived for conversion");
      //      loadJavaStates();
      //
      //      String javaTemplate =
      //         OseeInf.getResourceContents("atsConfig/convert/JavaTemplate.java", WorkDefinitionSheet.class);
      //      String stateTemplate = OseeInf.getResourceContents("atsConfig/convert/StateDef.txt", WorkDefinitionSheet.class);
      //
      //      //      List<String> workDefs = Arrays.asList("WorkDef_Team_F18_Requirements.ats", "WorkDef_Team_F18.ats");
      //      //      for (String workDef : workDefs) {
      //      for (ArtifactToken workDefArt : atsApi.getQueryService().getArtifacts(AtsArtifactTypes.WorkDefinition)) {
      //
      //         //         String dslSheet = OseeInf.getResourceContents("atsConfig/" + workDef, WorkDefinitionSheet.class);
      //         String dslSheet =
      //            atsApi.getAttributeResolver().getSoleAttributeValueAsString(workDefArt, AtsAttributeTypes.DslSheet, "");
      //         IAtsWorkDefinition wd = atsApi.getWorkDefinitionService().getWorkDefinitionFromStr(dslSheet);
      //
      //         // Confirm file isn't already converted
      //         File oldFile = new File(
      //            WorkDefinitionSheet.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "/src/org/eclipse/osee/ats/core/workdef/defaults/" + wd.getName().replaceAll(
      //               "_", "") + ".java");
      //         if (oldFile.exists()) {
      //            continue;
      //         }
      //
      //         File file = new File(
      //            WorkDefinitionSheet.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "/src/org/eclipse/osee/ats/core/workdef/defaults/newest/" + wd.getName().replaceAll(
      //               "_", "") + ".java");
      //
      //         String jFile = javaTemplate;
      //         jFile = jFile.replaceAll("PUT_CLASSNAME_HERE", wd.getName().replaceAll("_", ""));
      //         jFile = jFile.replaceAll("PUT_NAME_HERE", wd.getName());
      //         jFile = jFile.replaceAll("2345", String.valueOf(getId(dslSheet, workDefArt, wd.getName())));
      //
      //         String statesStr = "";
      //         for (IAtsStateDefinition state : wd.getStates()) {
      //            String str = stateTemplate;
      //            str = str.replaceFirst("PUT_ORDINAL_HERE", String.valueOf(state.getOrdinal()));
      //            str = str.replaceFirst("PUT_NAME_HERE", state.getName());
      //            str = str.replaceFirst("PUT_TYPE_HERE", state.getStateType().toString());
      //            if (wd.getStartState().equals(state)) {
      //               str = str.replaceFirst("PUT_IS_START_STATE_HERE", ".isStartState()");
      //            } else {
      //               str = str.replaceFirst("PUT_IS_START_STATE_HERE", "");
      //            }
      //            if (state.getDefaultToState() == null) {
      //               str = str.replaceFirst("PUT_TO_DEFAULT_STATE\n", "");
      //            } else {
      //               str = str.replaceFirst("PUT_TO_DEFAULT_STATE",
      //                  ".andToDefaultState(StateToken." + getStateNameToken(state.getDefaultToState().getName()) + ") //");
      //            }
      //            if (state.getColor() == null) {
      //               str = str.replaceFirst("PUT_COLOR_HERE\n", "");
      //            } else {
      //               str = str.replaceFirst("PUT_COLOR_HERE", ".andColor(StateColor." + state.getColor().name() + ") //");
      //            }
      //            if (state.getToStates().isEmpty()) {
      //               str = str.replaceFirst("PUT_TO_STATES_HERE\n", "");
      //            } else {
      //               String toStates = "";
      //               for (IAtsStateDefinition toState : state.getToStates()) {
      //                  toStates += "StateToken." + getStateNameToken(toState.getName()) + ",";
      //                  // Only need on java state token
      //                  if (!stateNames.contains(toState.getName())) {
      //                     String javaState = String.format("public static StateToken %s = StateToken.valueOf(%sL, \"%s\");",
      //                        getStateNameToken(toState.getName()), Lib.generateArtifactIdAsInt(), toState.getName());
      //                     javaStates.add(javaState);
      //                     stateNames.add(toState.getName());
      //                  }
      //               }
      //               str = str.replaceFirst("PUT_TO_STATES_HERE", ".andToStates(" + toStates.replaceFirst(",$", "") + ") //");
      //            }
      //            if (state.getOverrideAttributeValidationStates().isEmpty()) {
      //               str = str.replaceFirst("PUT_OV_STATES_HERE\n", "");
      //            } else {
      //               String toStates = "";
      //               for (IAtsStateDefinition toState : state.getOverrideAttributeValidationStates()) {
      //                  toStates += "StateToken." + getStateNameToken(toState.getName()) + ",";
      //               }
      //               str = str.replaceFirst("PUT_OV_STATES_HERE",
      //                  ".andOverrideValidationStates(" + toStates.replaceFirst(",$", "") + ") //");
      //            }
      //            if (state.getRules().isEmpty()) {
      //               str = str.replaceFirst("PUT_RULES_HERE\n", "");
      //            } else {
      //               String toStates = "";
      //               for (String rule : state.getRules()) {
      //                  toStates += "RuleDefinitionOption." + rule + ",";
      //               }
      //               str = str.replaceFirst("PUT_RULES_HERE", ".andRules(" + toStates.replaceFirst(",$", "") + ") //");
      //            }
      //            if (state.getLayoutItems().isEmpty()) {
      //               str = str.replaceFirst("PUT_LAYOUT_HERE", "");
      //            } else {
      //               String layoutStr = getLayoutStr(state, workDefArt);
      //               str = str.replaceFirst("PUT_LAYOUT_HERE", ".andLayout ( //\n" + layoutStr + "\n);"); // TBD
      //            }
      //
      //            str = str.replaceFirst(" //[\n ]+$", ";");
      //            statesStr += str + "\n";
      //         }
      //         jFile = jFile.replaceFirst("//PUT_STATES_HERE", statesStr);
      //
      //         try {
      //            Lib.writeStringToFile(jFile.toString(), file);
      //         } catch (IOException ex) {
      //            ex.printStackTrace();
      //         }
      //
      //      }
      //      Collections.sort(javaStates);
      //      for (String state : javaStates) {
      //         System.err.println(state);
      //      }
      return rd;
   }
   //
   //   private String getStateNameToken(String toStateName) {
   //      return toStateName.replaceAll("[ \\-]+", "");
   //   }
   //
   //   private Long getId(String jFile, ArtifactToken workDefArt, String name) {
   //      Matcher m = idPattern.matcher(jFile);
   //      if (m.find()) {
   //         String id = m.group(1);
   //         if (Strings.isNumeric(id)) {
   //            return Long.valueOf(id);
   //         } else if (workDefArt.isInvalid()) {
   //            System.err.println("Invalid id " + id + " in " + name);
   //         }
   //      } else if (workDefArt.isInvalid()) {
   //         System.err.println("Invalid id in " + name);
   //      }
   //      return workDefArt.getId();
   //   }
   //
   //   private String getLayoutStr(IAtsStateDefinition state, ArtifactToken workDefArt) {
   //      StringBuilder sb = new StringBuilder();
   //      for (IAtsLayoutItem item : state.getLayoutItems()) {
   //         handleLayoutItem(sb, item);
   //      }
   //      String layoutStr = sb.toString();
   //      layoutStr = layoutStr.replaceAll(" //[\n ]+$", "");
   //      layoutStr = layoutStr.replaceAll(", //[\n ]+\\)", " //\n)");
   //      layoutStr = layoutStr.replaceAll("\\),$", ")");
   //      return layoutStr;
   //   }
   //
   //   List<String> ignoreAttrTypes = Arrays.asList("Review Required", "Fast Track", "Training Required",
   //      "Tool Change Required", "Meeting Attendees", "SW Build Id");
   //
   //   private void handleLayoutItem(StringBuilder sb, IAtsLayoutItem item) {
   //      if (item instanceof CompositeLayoutItem) {
   //         CompositeLayoutItem comp = (CompositeLayoutItem) item;
   //         sb.append(String.format("new CompositeLayoutItem(%s, //\n", String.valueOf(comp.getNumColumns())));
   //         for (IAtsLayoutItem child : comp.getaLayoutItems()) {
   //            handleLayoutItem(sb, child);
   //         }
   //         sb.append("), //\n");
   //      } else if (item instanceof WidgetDefinition) {
   //         WidgetDefinition def = (WidgetDefinition) item;
   //         if (Strings.isValid(def.getAtrributeName())) {
   //            AttributeTypeToken attrType = atsApi.getStoreService().getAttributeType(def.getAtrributeName());
   //            String unqualifiedName = attrType.getUnqualifiedName();
   //            if (!def.getName().equals(unqualifiedName)) {
   //               String defStr = String.format("new WidgetDefinition(\"%s\", CoreAttributeTypes.Name, \"%s\"%s), //\n",
   //                  def.getName(), def.getXWidgetName(), getWidgetOptions(def.getOptions()));
   //               sb.append(defStr);
   //            } else {
   //               sb.append(getAttrWidget(attrType, def.getXWidgetName(), getWidgetOptions(def.getOptions())));
   //            }
   //         } else {
   //            sb.append(getWidget(def.getName(), def.getAtrributeName(), def.getXWidgetName(),
   //               getWidgetOptions(def.getOptions())));
   //         }
   //      }
   //   }
   //
   //   private String getWidgetOptions(IAtsWidgetOptionHandler options) {
   //      StringBuilder sb = new StringBuilder(", ");
   //      for (WidgetOption option : options.getXOptions()) {
   //         sb.append(option.name());
   //         sb.append(", ");
   //      }
   //      return sb.toString().replaceFirst(", $", "");
   //   }
   //
   //   private String getAttrWidget(AttributeTypeToken attrType, String xWidgetName, String widgetOptions) {
   //      String attrTypeTokStr = getAttrTypeTokStr(attrType);
   //
   //      return String.format("new WidgetDefinition(%s, \"%s\"%s), //\n", attrTypeTokStr, xWidgetName, widgetOptions);
   //   }
   //
   //   private final Map<String, String> attrTypeMap = new HashMap<>();
   //
   //   private Map<String, String> getMap() {
   //      if (attrTypeMap.isEmpty()) {
   //         attrTypeMap.put("ats.Priority", "PriorityType");
   //         attrTypeMap.put("Priority", "PriorityType");
   //         attrTypeMap.put("LegacyPCRId", "LegacyPcrId");
   //         attrTypeMap.put("LOCChanged", "LocChanged");
   //         attrTypeMap.put("UnPlannedPoints", "UnPlannedPoints");
   //         attrTypeMap.put("SMANote", "SmaNote");
   //         attrTypeMap.put("Category", "Category1");
   //         attrTypeMap.put("Question", "Name");
   //         attrTypeMap.put("SW Build Id", "SwBuildId");
   //         attrTypeMap.put("ats.Legacy PCR Id", "LegacyPcrId");
   //         attrTypeMap.put("ats.LOC Changed", "LocChanged");
   //         attrTypeMap.put("LOCReviewed", "LocReviewed");
   //         attrTypeMap.put("LBA Change Type", "ChangeType");
   //         attrTypeMap.put("Type", "PcrToolId");
   //         attrTypeMap.put("Description of Action", "Description");
   //      }
   //      return attrTypeMap;
   //   }
   //
   //   private String getAttrTypeTokStr(AttributeTypeToken attrType) {
   //      String attrTypeTokStr = "";
   //      if (attrType.getName().contains("lba")) {
   //         attrTypeTokStr = "LbaAttributeTypes.";
   //      } else if (attrType.getName().contains("ats")) {
   //         attrTypeTokStr = "AtsAttributeTypes.";
   //      } else {
   //         attrTypeTokStr = "CoreAttributeTypes.";
   //      }
   //
   //      String attrName = attrType.getUnqualifiedName().replaceAll(" ", "");
   //      attrName = attrName.replaceAll("-", "");
   //      if (getMap().containsKey(attrName)) {
   //         attrName = getMap().get(attrName);
   //      }
   //      attrTypeTokStr += attrName;
   //      return attrTypeTokStr;
   //   }
   //
   //   private String getWidget(String name, String attrTypeName, String xWidgetName, String widgetOptions) {
   //      String attrName = null;
   //      if (Strings.isValid(attrTypeName)) {
   //         attrName = String.format("\"%s\"", attrTypeName);
   //      }
   //      return String.format("new WidgetDefinition(\"%s\", %s, \"%s\"%s), //\n", name, attrName, xWidgetName,
   //         widgetOptions);
   //   }
   //
   //   private void loadJavaStates() {
   //      javaStates.addAll(
   //         Arrays.asList("public static StateToken Prepare = StateToken.valueOf(32483247988L, \"Prepare\");",
   //            "public static StateToken Review = StateToken.valueOf(98937432L, \"Review\");",
   //            "public static StateToken Followup = StateToken.valueOf(98983282387L, \"Followup\");",
   //            "public static StateToken Meeting = StateToken.valueOf(4383477878L, \"Meeting\");",
   //            "public static StateToken Decision = StateToken.valueOf(98983282387L, \"Decision\");",
   //            "public static StateToken InReview = StateToken.valueOf(9939475738L, \"InReview\");",
   //            "public static StateToken None = StateToken.valueOf(38383883L, \"None\");",
   //            "public static StateToken InWork = StateToken.valueOf(32432487L, \"InWork\");",
   //            "public static StateToken NotRequired = StateToken.valueOf(233223455L, \"NotRequired\");",
   //            "public static StateToken Endorse = StateToken.valueOf(23420230948L, \"Endorse\");",
   //            "public static StateToken Analyze = StateToken.valueOf(593820493L, \"Analyze\");",
   //            "public static StateToken Authorize = StateToken.valueOf(91727489234L, \"Authorize\");",
   //            "public static StateToken Implement = StateToken.valueOf(43298928340L, \"Implement\");",
   //            "public static StateToken Completed = StateToken.valueOf(3532702930L, \"Completed\");",
   //            "public static StateToken Cancelled = StateToken.valueOf(48239402L, \"Cancelled\");"));
   //      stateNames.addAll(Arrays.asList("Prepare", "Review", "Followup", "Meeting", "Decision", "InReview", "None",
   //         "InWork", "NotRequired", "Endorse", "Analyze", "Authorize", "Implement", "Completed", "Cancelled"));
   //   }
}
