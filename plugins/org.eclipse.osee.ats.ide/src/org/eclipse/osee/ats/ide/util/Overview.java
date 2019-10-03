/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.ide.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.log.IAtsLog;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.workflow.log.AtsLogUtility;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.AbstractAtsArtifact;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.task.TaskArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultBrowserHyperCmd;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.AHTML.CellItem;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;

/**
 * @author Donald G. Dunne
 */
public class Overview {

   public final static String normalColor = "#EEEEEE";
   private final static String activeColor = "#9CCCFF";
   public final static String errorColor = "#FFD6AC";
   public final static String subscribedColor = "#FFCCAA";
   public final static String labelColor = "darkcyan";
   public static enum PreviewStyle {
      NONE,
      MAP,
      TASKS,
      NOTES,
      LOG,
      HYPEROPEN,
      NO_SUBSCRIBE_OR_FAVORITE;

      public static boolean contains(PreviewStyle[] styles, PreviewStyle style) {
         for (PreviewStyle st : styles) {
            if (st.equals(style)) {
               return true;
            }
         }
         return false;
      }
   };
   private StringBuilder html;
   public final static String labelFont = "<font color=\"darkcyan\" face=\"Arial\" size=\"-1\">";
   public final static int TABLE_WIDTH = 95;
   public boolean showTasks = false;

   public Overview() {
      clearHtml();
   }

   public void clearHtml() {
      html = new StringBuilder(1000);
   }

   /**
    * @return HTML page for browser display
    */
   public String getPage() {
      return "<html><body>\n" + html + "\n</body></html>";
   }

   /**
    * Return label with value converted to show html reserved characters
    *
    * @return string to embed into html
    */
   public String getLabelValue(String label, String value) {
      String valueStr = AHTML.textToHtml(value);
      return getLabel(label) + valueStr;
   }

   public static String getLabel(String label) {
      return AHTML.getLabelStr(labelFont, label + ": ");
   }

   public void addTable(String... strs) {
      addTable(strs, 100);
   }

   public void addTable(String[] strs, int width) {
      if (strs.length == 1) {
         this.html.append(AHTML.simpleTable(strs[0]));
      } else {
         this.html.append(AHTML.multiColumnTable(width, strs));
      }
   }

   public void addHeader(AbstractWorkflowArtifact awa, PreviewStyle... styles) {
      startBorderTable(100, false, "");
      addTable(getLabelValue("Title", awa.getName()));
      this.html.append(AHTML.multiColumnTable(new String[] {
         AHTML.getLabelStr(labelFont, "State: ") + awa.getStateMgr().getCurrentStateName(),
         AHTML.getLabelStr(labelFont, "Type: ") + awa.getArtifactTypeName(),
         AHTML.getLabelStr(labelFont, "Id: ") + awa.getAtsId()}));
      addTable(getLabelValue("Originator", awa.getCreatedBy().getName()),
         getLabelValue("Creation Date", DateUtil.getMMDDYYHHMM(awa.getCreatedDate())));
      if (awa.isTeamWorkflow()) {
         addTable(getLabelValue("Team", ((TeamWorkFlowArtifact) awa).getTeamName()),
            getLabelValue("Assignees", AtsObjects.toString("; ", awa.getStateMgr().getAssignees())));
      } else {
         addTable(getLabelValue("Assignees", AtsObjects.toString("; ", awa.getStateMgr().getAssignees())));
      }
      addTable(getLabelValue("Description", awa.getDescription()));
      if (awa.isCancelled()) {
         addTable(getLabelValue("Cancelled From", awa.getCancelledFromState()));
         addTable(getLabelValue("Cancellation Reason", awa.getCancelledReason()));
      }
      if (awa.isTypeEqual(AtsArtifactTypes.Task)) {
         AbstractWorkflowArtifact parentArt = ((TaskArtifact) awa).getParentAWA();
         if (parentArt != null) {
            this.html.append(AHTML.multiColumnTable(
               new String[] {AHTML.getLabelStr(labelFont, "Parent Workflow: ") + parentArt.getName()}));
            this.html.append(AHTML.multiColumnTable(new String[] {
               AHTML.getLabelStr(labelFont,
                  "Parent State: ") + ((TaskArtifact) awa).getStateMgr().getCurrentStateName()}));
         }
         this.html.append(AHTML.multiColumnTable(new String[] {
            AHTML.getLabelStr(labelFont, "Task Owner: ") + AtsObjects.toString("; ",
               awa.getStateMgr().getAssignees())}));
      }
      endBorderTable();
   }

   public void addFooter(AbstractWorkflowArtifact sma, PreviewStyle... styles) {
      this.html.append(AHTML.newline());

      if (PreviewStyle.contains(styles, PreviewStyle.HYPEROPEN)) {
         this.html.append("Start OSEE, select the ATS perspective and search by the Id shown.");
      }
   }

   public void addRelationsBlock(AbstractAtsArtifact artifact) {
      addRelationTable("Is Superceded By", CoreRelationTypes.Supercedes_Supercedes, artifact);
      addRelationTable("Supercedes", CoreRelationTypes.Supercedes_SupercededBy, artifact);
      addRelationTable("Supports", CoreRelationTypes.SupportingInfo_IsSupportedBy, artifact);
      addRelationTable("Is Supported By", CoreRelationTypes.SupportingInfo_SupportingInfo, artifact);
   }

   public void addNotes(Artifact artifact) {
      if (artifact instanceof AbstractWorkflowArtifact) {
         String notesHtml =
            AtsClientService.get().getWorkItemService().getNotes((AbstractWorkflowArtifact) artifact).getTable(null);
         if (notesHtml.equals("")) {
            return;
         }
         this.html.append(notesHtml);
      }
   }

   public static String getGenericArtifactTable(String name, Collection<Artifact> arts) {
      return getGenericArtifactTable(name, arts, 100);
   }

   public static String getGenericArtifactTable(String name, Collection<Artifact> arts, int width) {
      StringBuilder builder = new StringBuilder();
      builder.append(AHTML.addSpace(1) + Overview.getLabel(name));
      if (arts.size() > 0) {
         builder.append(
            "<TABLE BORDER=\"1\" align=\"center\" cellspacing=\"1\" cellpadding=\"3%\" width=\"" + width + "%\"><THEAD><TR><TH>Type</TH>" + "<TH>Name</TH></THEAD></TR>");
         for (Artifact art : arts) {
            builder.append("<TR>");
            builder.append("<TD>" + art.getArtifactTypeName() + "</TD>");
            builder.append("<TD>" + Overview.getOpenHyperlinkHtml(art) + "</TD>");
            builder.append("</TR>");
         }
         builder.append("</TABLE>");
      }
      return builder.toString();
   }

   @SuppressWarnings("deprecation")
   public void addRelationTable(String name, RelationTypeSide side, Artifact parent) {
      try {
         List<Artifact> arts = parent.getRelatedArtifacts(side);
         if (arts.isEmpty()) {
            return;
         }
         startBorderTable(false, name);
         html.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Type", "Name", "Rationale"}));
         for (Artifact art : arts) {
            String rationale = "";
            RelationLink link = parent.getRelations(side, art).iterator().next();
            if (!link.getRationale().equals("")) {
               rationale = link.getRationale();
            }
            String hyperStr = Overview.getOpenHyperlinkHtml(art);
            html.append(AHTML.addRowMultiColumnTable(new String[] {art.getArtifactTypeName(), hyperStr, rationale}));
         }
         endBorderTable();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public void addTeams(Collection<TeamWorkFlowArtifact> teams) {
      startBorderTable(TABLE_WIDTH, false, "Team Workflows");
      StringBuffer sb = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
      sb.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Type", "State"}, new Integer[] {70, 150}));
      ArrayList<CellItem> cells = new ArrayList<>();
      for (TeamWorkFlowArtifact team : teams) {
         cells.add(new AHTML.CellItem(Overview.getOpenHyperlinkHtml(team)));
         cells.add(new AHTML.CellItem(team.getStateMgr().getCurrentStateName()));
         sb.append(AHTML.addRowMultiColumnTable(cells));
         cells.clear();
      }
      sb.append(AHTML.endMultiColumnTable());
      html.append(sb.toString());

      endBorderTable();
   }

   public static String getOpenHyperlinkHtml(Artifact art) {
      return getOpenHyperlinkHtml(art.getName(), art);
   }

   public static String getOpenHyperlinkHtml(String name, Artifact art) {
      return AHTML.getHyperlink(
         XResultBrowserHyperCmd.getHyperCmdStr(XResultBrowserHyperCmd.openAction, art.getIdString()), name);
   }

   public void addLog(AbstractWorkflowArtifact artifact) {
      IAtsLog artifactLog = artifact.getLog();
      if (artifactLog != null && artifactLog.getLogItems().size() > 0) {
         AtsLogUtility.getTable(artifactLog, AtsClientService.get().getLogFactory().getLogProvider(artifact,
            AtsClientService.get().getAttributeResolver()), AtsClientService.get().getUserService());
      }
   }

   public void startStateBorderTable(boolean active, String name, String assignee) {
      if (Strings.isValid(assignee)) {
         startBorderTable(active, String.format("%s (%s)", name, assignee));
      } else {
         startBorderTable(active, String.format("%s", name));
      }
   }

   public void startBorderTable(boolean active, String caption) {
      this.html.append(AHTML.startBorderTable(TABLE_WIDTH, active ? activeColor : normalColor, caption));
   }

   public void startBorderTable(int width, boolean active, String caption) {
      this.html.append(AHTML.startBorderTable(width, active ? activeColor : normalColor, caption));
   }

   public void startBorderTable(int width, String caption, String backgroundColor) {
      this.html.append(AHTML.startBorderTable(width, backgroundColor, caption));
   }

   public void endBorderTable() {
      this.html.append(AHTML.endBorderTable());
   }

   public boolean isShowTasks() {
      return showTasks;
   }

   public void setShowTasks(boolean showTasks) {
      this.showTasks = showTasks;
   }
}