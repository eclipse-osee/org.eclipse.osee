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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a top-level SysML V2 package containing defs, usages, and connections.
 *
 * @author Donald G. Dunne
 */
public class SysmlPackage {

   private final String name;
   private final List<String> imports = new ArrayList<>();
   private final List<SysmlEnumDef> enumDefs = new ArrayList<>();
   private final List<SysmlPartDef> partDefs = new ArrayList<>();
   private final List<SysmlStateMachine> stateMachines = new ArrayList<>();
   private final List<SysmlConnectionDef> connectionDefs = new ArrayList<>();
   private final List<SysmlPartUsage> partUsages = new ArrayList<>();
   private final List<SysmlConnection> connections = new ArrayList<>();

   public SysmlPackage(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public List<String> getImports() {
      return imports;
   }

   public void addImport(String importPath) {
      imports.add(importPath);
   }

   public List<SysmlEnumDef> getEnumDefs() {
      return enumDefs;
   }

   public void addEnumDef(SysmlEnumDef enumDef) {
      enumDefs.add(enumDef);
   }

   public List<SysmlPartDef> getPartDefs() {
      return partDefs;
   }

   public void addPartDef(SysmlPartDef partDef) {
      partDefs.add(partDef);
   }

   public List<SysmlStateMachine> getStateMachines() {
      return stateMachines;
   }

   public void addStateMachine(SysmlStateMachine stateMachine) {
      stateMachines.add(stateMachine);
   }

   public List<SysmlConnectionDef> getConnectionDefs() {
      return connectionDefs;
   }

   public void addConnectionDef(SysmlConnectionDef connectionDef) {
      connectionDefs.add(connectionDef);
   }

   public List<SysmlPartUsage> getPartUsages() {
      return partUsages;
   }

   public void addPartUsage(SysmlPartUsage partUsage) {
      partUsages.add(partUsage);
   }

   public List<SysmlConnection> getConnections() {
      return connections;
   }

   public void addConnection(SysmlConnection connection) {
      connections.add(connection);
   }
}
