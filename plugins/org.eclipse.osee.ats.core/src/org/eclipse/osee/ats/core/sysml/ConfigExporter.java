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

package org.eclipse.osee.ats.core.sysml;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.sysml.OseeRelationToSysmlMapper;
import org.eclipse.osee.ats.api.sysml.SysmlConnection;
import org.eclipse.osee.ats.api.sysml.SysmlConstants;
import org.eclipse.osee.ats.api.sysml.SysmlEnumDef;
import org.eclipse.osee.ats.api.sysml.SysmlPackage;
import org.eclipse.osee.ats.api.sysml.SysmlPartDef;
import org.eclipse.osee.ats.api.sysml.SysmlPartUsage;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactState;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * Exports ATS Program configuration objects (Program, Team Definitions, Actionable Items, Versions, Users) to a
 * SysmlPackage model for serialization to SysML V2 textual notation.
 *
 * @author Donald G. Dunne
 */
public class ConfigExporter {

   private final AtsApi atsApi;

   public ConfigExporter(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   /**
    * Exports all config objects for the given program into a SysmlPackage.
    */
   public SysmlPackage export(ArtifactId programId) {
      IAtsProgram program = atsApi.getProgramService().getProgramById(programId);
      if (program == null) {
         throw new OseeArgumentException("Program not found for id [%s]", programId);
      }
      String packageName = sanitizeName(program.getName()) + "_Config";
      SysmlPackage pkg = new SysmlPackage(packageName);

      // Part Defs
      addConfigEnumDefs(pkg);
      pkg.addPartDef(buildProgramPartDef());
      pkg.addPartDef(buildTeamDefinitionPartDef());
      pkg.addPartDef(buildActionableItemPartDef());
      pkg.addPartDef(buildVersionPartDef());
      pkg.addPartDef(buildUserPartDef());

      // Connection Defs
      pkg.addConnectionDef(OseeRelationToSysmlMapper.toConnectionDef(CoreRelationTypes.DefaultHierarchical));
      pkg.addConnectionDef(OseeRelationToSysmlMapper.toConnectionDef(AtsRelationTypes.TeamActionableItem));
      pkg.addConnectionDef(OseeRelationToSysmlMapper.toConnectionDef(AtsRelationTypes.TeamDefinitionToVersion));
      pkg.addConnectionDef(OseeRelationToSysmlMapper.toConnectionDef(AtsRelationTypes.TeamLead));
      pkg.addConnectionDef(OseeRelationToSysmlMapper.toConnectionDef(AtsRelationTypes.TeamMember));
      pkg.addConnectionDef(OseeRelationToSysmlMapper.toConnectionDef(AtsRelationTypes.ParallelVersion));

      // Program instance
      pkg.addPartUsage(buildProgramUsage(program));

      // Single pass over team defs: emit each team def's part usage, its related
      // objects, and all its connections. Versions and users can be shared across
      // teams, so track which have already been emitted to avoid duplicate parts.
      Set<Long> emittedVersions = new HashSet<>();
      Set<Long> emittedUsers = new HashSet<>();
      Set<Long> emittedAis = new HashSet<>();

      Collection<IAtsTeamDefinition> teamDefs = atsApi.getProgramService().getTeamDefs(program);
      for (IAtsTeamDefinition teamDef : teamDefs) {
         pkg.addPartUsage(buildTeamDefUsage(teamDef));
         pkg.addConnection(new SysmlConnection("DefaultHierarchical", id(program), id(teamDef)));

         for (IAtsActionableItem ai : teamDef.getActionableItems()) {
            emitAiTree(pkg, ai, emittedAis);
            pkg.addConnection(new SysmlConnection("TeamActionableItem", id(teamDef), id(ai)));
         }

         for (IAtsVersion version : atsApi.getTeamDefinitionService().getVersions(teamDef)) {
            if (emittedVersions.add(version.getId())) {
               pkg.addPartUsage(buildVersionUsage(version));
            }
            pkg.addConnection(new SysmlConnection("TeamDefinitionToVersion", id(teamDef), id(version)));
         }

         for (AtsUser lead : atsApi.getTeamDefinitionService().getLeads(teamDef)) {
            if (emittedUsers.add(lead.getId())) {
               pkg.addPartUsage(buildUserUsage(lead));
            }
            pkg.addConnection(new SysmlConnection("TeamLead", id(teamDef), String.valueOf(lead.getId())));
         }

         for (AtsUser member : atsApi.getTeamDefinitionService().getMembers(teamDef)) {
            if (emittedUsers.add(member.getId())) {
               pkg.addPartUsage(buildUserUsage(member));
            }
            pkg.addConnection(new SysmlConnection("TeamMember", id(teamDef), String.valueOf(member.getId())));
         }
      }

      return pkg;
   }

   /**
    * Emits the AI part usage and recurses into children, emitting AI hierarchy connections. The emittedAis set tracks
    * which items have already been written so each is emitted only once.
    */
   private void emitAiTree(SysmlPackage pkg, IAtsActionableItem ai, Set<Long> emittedAis) {
      if (!emittedAis.add(ai.getId())) {
         return;
      }
      pkg.addPartUsage(buildAiUsage(ai));
      for (IAtsActionableItem child : ai.getChildrenActionableItems()) {
         pkg.addConnection(new SysmlConnection("DefaultHierarchical", id(ai), id(child)));
         emitAiTree(pkg, child, emittedAis);
      }
   }

   // Part Defs

   private SysmlPartDef buildProgramPartDef() {
      SysmlPartDef def = new SysmlPartDef("Program");
      def.addAttribute("artifactId", SysmlConstants.INTEGER);
      def.addAttribute("name", SysmlConstants.STRING);
      def.addAttribute("description", SysmlConstants.STRING, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("namespace", SysmlConstants.STRING, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("programId", SysmlConstants.STRING, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      return def;
   }

   private SysmlPartDef buildTeamDefinitionPartDef() {
      SysmlPartDef def = new SysmlPartDef("TeamDefinition");
      def.addAttribute("artifactId", SysmlConstants.INTEGER);
      def.addAttribute("name", SysmlConstants.STRING);
      def.addAttribute("active", SysmlConstants.BOOLEAN);
      def.addAttribute("workflowDefinitionReference", SysmlConstants.STRING, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("requireTargetedVersion", SysmlConstants.BOOLEAN, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("teamUsesVersions", SysmlConstants.BOOLEAN, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("allowCreateBranch", SysmlConstants.BOOLEAN, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("allowCommitBranch", SysmlConstants.BOOLEAN, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("atsIdPrefix", SysmlConstants.STRING, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("workType", SysmlConstants.STRING, SysmlConstants.MULTIPLICITY_ZERO_OR_MORE);
      return def;
   }

   private SysmlPartDef buildActionableItemPartDef() {
      SysmlPartDef def = new SysmlPartDef("ActionableItem");
      def.addAttribute("artifactId", SysmlConstants.INTEGER);
      def.addAttribute("name", SysmlConstants.STRING);
      def.addAttribute("active", SysmlConstants.BOOLEAN);
      def.addAttribute("actionable", SysmlConstants.BOOLEAN);
      def.addAttribute("allowUserActionCreation", SysmlConstants.BOOLEAN, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("workType", SysmlConstants.STRING, SysmlConstants.MULTIPLICITY_ZERO_OR_MORE);
      def.addAttribute("programId", SysmlConstants.STRING, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      return def;
   }

   private SysmlPartDef buildVersionPartDef() {
      SysmlPartDef def = new SysmlPartDef("Version");
      def.addAttribute("artifactId", SysmlConstants.INTEGER);
      def.addAttribute("name", SysmlConstants.STRING);
      def.addAttribute("released", SysmlConstants.BOOLEAN);
      def.addAttribute("versionLocked", SysmlConstants.BOOLEAN, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("nextVersion", SysmlConstants.BOOLEAN, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("allowCreateBranch", SysmlConstants.BOOLEAN, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("allowCommitBranch", SysmlConstants.BOOLEAN, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      def.addAttribute("closureState", SysmlConstants.STRING, SysmlConstants.MULTIPLICITY_ZERO_OR_ONE);
      return def;
   }

   private SysmlPartDef buildUserPartDef() {
      SysmlPartDef def = new SysmlPartDef("User");
      def.addAttribute("artifactId", SysmlConstants.INTEGER);
      def.addAttribute("userId", SysmlConstants.STRING);
      def.addAttribute("name", SysmlConstants.STRING);
      return def;
   }

   // Part Usages (instances)

   private SysmlPartUsage buildProgramUsage(IAtsProgram program) {
      SysmlPartUsage usage = new SysmlPartUsage(String.valueOf(program.getId()), "Program");
      usage.addAttributeValue("artifactId", program.getId());
      usage.addAttributeValue("name", program.getName());
      addIfValid(usage, "description", program.getDescription());
      return usage;
   }

   private SysmlPartUsage buildTeamDefUsage(IAtsTeamDefinition teamDef) {
      SysmlPartUsage usage = new SysmlPartUsage(String.valueOf(teamDef.getId()), "TeamDefinition");
      usage.addAttributeValue("artifactId", teamDef.getId());
      usage.addAttributeValue("name", teamDef.getName());
      usage.addAttributeValue("active", teamDef.isActive());
      return usage;
   }

   private SysmlPartUsage buildAiUsage(IAtsActionableItem ai) {
      SysmlPartUsage usage = new SysmlPartUsage(String.valueOf(ai.getId()), "ActionableItem");
      usage.addAttributeValue("artifactId", ai.getId());
      usage.addAttributeValue("name", ai.getName());
      usage.addAttributeValue("active", ai.isActive());
      usage.addAttributeValue("actionable", ai.isActionable());
      return usage;
   }

   private SysmlPartUsage buildVersionUsage(IAtsVersion version) {
      SysmlPartUsage usage = new SysmlPartUsage(String.valueOf(version.getId()), "Version");
      usage.addAttributeValue("artifactId", version.getId());
      usage.addAttributeValue("name", version.getName());
      usage.addAttributeValue("released", version.isReleased());
      usage.addAttributeValue("versionLocked", version.isLocked());
      usage.addAttributeValue("nextVersion", version.isNextVersion());
      usage.addAttributeValue("allowCreateBranch", version.isAllowCreateBranch());
      usage.addAttributeValue("allowCommitBranch", version.isAllowCommitBranch());
      return usage;
   }

   private SysmlPartUsage buildUserUsage(AtsUser user) {
      SysmlPartUsage usage = new SysmlPartUsage(String.valueOf(user.getId()), "User");
      usage.addAttributeValue("artifactId", user.getId());
      usage.addAttributeValue("userId", user.getUserId());
      usage.addAttributeValue("name", user.getName());
      return usage;
   }

   // Utilities

   private void addIfValid(SysmlPartUsage usage, String attrName, String value) {
      if (value != null && !value.isEmpty()) {
         usage.addAttributeValue(attrName, value);
      }
   }

   private String id(IAtsConfigObject obj) {
      return String.valueOf(obj.getId());
   }

   private String id(IAtsProgram program) {
      return String.valueOf(program.getId());
   }

   private String sanitizeName(String name) {
      return name.replace(" ", "_").replace("-", "_");
   }

   private void addConfigEnumDefs(SysmlPackage pkg) {
      pkg.addEnumDef(new SysmlEnumDef("BuildImpactState", BuildImpactState.getStateNames()));
   }
}
