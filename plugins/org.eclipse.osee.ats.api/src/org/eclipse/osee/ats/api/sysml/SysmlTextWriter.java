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

package org.eclipse.osee.ats.api.sysml;

import java.util.List;

/**
 * Serializes the SysML V2 Java model to textual notation (.sysml format).
 *
 * @author Donald G. Dunne
 */
public class SysmlTextWriter {

   private final StringBuilder sb = new StringBuilder();
   private int indent = 0;

   public String write(SysmlPackage pkg) {
      sb.setLength(0);
      indent = 0;
      writePackage(pkg);
      return sb.toString();
   }

   private void writePackage(SysmlPackage pkg) {
      line("package %s {", sysmlName(pkg.getName()));
      indent++;

      for (String imp : pkg.getImports()) {
         line("import %s;", imp);
      }
      if (!pkg.getImports().isEmpty()) {
         blankLine();
      }

      // Enum defs
      for (SysmlEnumDef enumDef : pkg.getEnumDefs()) {
         writeEnumDef(enumDef);
         blankLine();
      }

      // Part defs
      for (SysmlPartDef partDef : pkg.getPartDefs()) {
         writePartDef(partDef);
         blankLine();
      }

      // State machines
      for (SysmlStateMachine sm : pkg.getStateMachines()) {
         writeStateMachine(sm);
         blankLine();
      }

      // Connection defs
      for (SysmlConnectionDef connDef : pkg.getConnectionDefs()) {
         writeConnectionDef(connDef);
         blankLine();
      }

      // Part usages (instances)
      for (SysmlPartUsage usage : pkg.getPartUsages()) {
         writePartUsage(usage);
         blankLine();
      }

      // Connection usages (instances)
      for (SysmlConnection conn : pkg.getConnections()) {
         writeConnection(conn);
      }

      indent--;
      line("}");
   }

   private void writeEnumDef(SysmlEnumDef enumDef) {
      line("enum def %s {", sysmlName(enumDef.getName()));
      indent++;
      for (String literal : enumDef.getLiterals()) {
         line("enum %s;", sysmlName(literal));
      }
      indent--;
      line("}");
   }

   private void writePartDef(SysmlPartDef partDef) {
      if (partDef.hasSuperType()) {
         line("part def %s :> %s {", sysmlName(partDef.getName()), sysmlName(partDef.getSuperType()));
      } else {
         line("part def %s {", sysmlName(partDef.getName()));
      }
      indent++;
      for (SysmlAttributeDef attr : partDef.getAttributes()) {
         writeAttributeDef(attr);
      }
      indent--;
      line("}");
   }

   private void writeAttributeDef(SysmlAttributeDef attr) {
      String mult = attr.hasMultiplicity() ? attr.getMultiplicity() : "";
      if (mult.isEmpty()) {
         line("attribute %s : %s;", sysmlIdentifier(attr.getName()), attr.getType());
      } else {
         line("attribute %s : %s%s;", sysmlIdentifier(attr.getName()), attr.getType(), mult);
      }
   }

   private void writeStateMachine(SysmlStateMachine sm) {
      line("state def %s {", sysmlName(sm.getName()));
      indent++;
      if (sm.hasWorkDefinitionId()) {
         line("attribute workflowDefinitionReference : Integer = %d;", sm.getWorkDefinitionId());
      }
      for (SysmlState state : sm.getStates()) {
         if (state.isEntry()) {
            line("entry state %s;", sysmlIdentifier(state.getName()));
         } else {
            line("state %s;", sysmlIdentifier(state.getName()));
         }
      }
      blankLine();
      for (SysmlTransition transition : sm.getTransitions()) {
         line("transition %s first %s then %s;", sysmlIdentifier(transition.getName()),
            sysmlIdentifier(transition.getSource()), sysmlIdentifier(transition.getTarget()));
      }
      indent--;
      line("}");
   }

   private void writeConnectionDef(SysmlConnectionDef connDef) {
      line("connection def %s {", sysmlName(connDef.getName()));
      indent++;
      line("end %s : %s%s;", sysmlIdentifier(connDef.getEndAName()), sysmlName(connDef.getEndAType()),
         connDef.getEndAMultiplicity());
      line("end %s : %s%s;", sysmlIdentifier(connDef.getEndBName()), sysmlName(connDef.getEndBType()),
         connDef.getEndBMultiplicity());
      indent--;
      line("}");
   }

   private void writePartUsage(SysmlPartUsage usage) {
      List<SysmlAttributeValue> values = usage.getAttributeValues();
      boolean hasContent = !values.isEmpty() || usage.hasExhibitState();
      if (!hasContent) {
         line("part %s : %s;", sysmlName(usage.getName()), sysmlName(usage.getDefName()));
      } else {
         line("part %s : %s {", sysmlName(usage.getName()), sysmlName(usage.getDefName()));
         indent++;
         for (SysmlAttributeValue attrVal : values) {
            writeAttributeValue(attrVal);
         }
         if (usage.hasExhibitState()) {
            line("exhibit state : %s;", sysmlName(usage.getExhibitState()));
         }
         indent--;
         line("}");
      }
   }

   @SuppressWarnings("unchecked")
   private void writeAttributeValue(SysmlAttributeValue attrVal) {
      if (attrVal.isMultiValued()) {
         List<Object> values = (List<Object>) attrVal.getValue();
         if (values.isEmpty()) {
            return;
         }
         StringBuilder valStr = new StringBuilder("(");
         for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
               valStr.append(", ");
            }
            valStr.append(formatValue(values.get(i), attrVal.isEnum(), attrVal.getEnumDefName()));
         }
         valStr.append(")");
         line("attribute %s = %s;", sysmlIdentifier(attrVal.getName()), valStr.toString());
      } else {
         String formatted = formatValue(attrVal.getValue(), attrVal.isEnum(), attrVal.getEnumDefName());
         line("attribute %s = %s;", sysmlIdentifier(attrVal.getName()), formatted);
      }
   }

   private String formatValue(Object value, boolean isEnum, String enumDefName) {
      if (value == null) {
         return "null";
      }
      if (isEnum && enumDefName != null) {
         return sysmlName(enumDefName) + "::" + sysmlName(value.toString());
      }
      if (value instanceof Boolean || value instanceof Integer || value instanceof Long || value instanceof Double) {
         return value.toString();
      }
      // String values get quoted
      return "\"" + escapeString(value.toString()) + "\"";
   }

   private void writeConnection(SysmlConnection conn) {
      line("connection : %s connect %s to %s;", sysmlName(conn.getConnectionDefName()),
         sysmlName(conn.getSourcePartName()), sysmlName(conn.getTargetPartName()));
   }

   private void line(String format, Object... args) {
      for (int i = 0; i < indent; i++) {
         sb.append("    ");
      }
      sb.append(String.format(format, args));
      sb.append("\n");
   }

   private void blankLine() {
      sb.append("\n");
   }

   /**
    * Returns a SysML-safe name: if it contains spaces or special chars, wraps in single quotes.
    */
   static String sysmlName(String name) {
      if (name == null || name.isEmpty()) {
         return "''";
      }
      if (needsQuoting(name)) {
         return "'" + name + "'";
      }
      return name;
   }

   /**
    * Returns a SysML-safe identifier: converts spaces to underscores, lowercases first char.
    */
   static String sysmlIdentifier(String name) {
      if (name == null || name.isEmpty()) {
         return "_";
      }
      String id = name.replace(" ", "_").replace("-", "_").replace("/", "_");
      // If it starts with a digit, prefix with underscore
      if (Character.isDigit(id.charAt(0))) {
         id = "_" + id;
      }
      return id;
   }

   private static boolean needsQuoting(String name) {
      for (int i = 0; i < name.length(); i++) {
         char c = name.charAt(i);
         if (c == ' ' || c == '-' || c == '/' || c == '(' || c == ')' || c == '.') {
            return true;
         }
      }
      // Also quote if starts with digit
      if (Character.isDigit(name.charAt(0))) {
         return true;
      }
      return false;
   }

   private static String escapeString(String s) {
      return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
   }
}
