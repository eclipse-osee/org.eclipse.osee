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

package org.eclipse.osee.ats.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSArtifact;
import org.eclipse.osee.ats.artifact.ATSLog;
import org.eclipse.osee.ats.artifact.LogItem;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.widgets.SMAState;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.AHTML.CellItem;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.ResultBrowserHyperCmd;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultHtml;

public class Overview {

   public final static String normalColor = "#EEEEEE";
   private final static String activeColor = "#9CCCFF";
   public final static String errorColor = "#FFD6AC";
   public final static String subscribedColor = "#FFCCAA";
   public final static String labelColor = "darkcyan";
   public static enum PreviewStyle {
      NONE, MAP, TASKS, NOTES, LOG, HYPEROPEN, NO_SUBSCRIBE_OR_FAVORITE;

      public static boolean contains(PreviewStyle[] styles, PreviewStyle style) {
         for (PreviewStyle st : styles)
            if (st.equals(style)) return true;
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
    * @param label
    * @param value
    * @return string to embed into html
    */
   public String getLabelValue(String label, String value) {
      String valueStr = AHTML.textToHtml(value);
      return getLabel(label) + valueStr;
   }

   /**
    * Return label and value WITHOUT conversion to handle html reserved characters. Value will be as-is
    * 
    * @param label
    * @param value
    * @return string to embed into html
    */
   public String getLabelValueNoConvert(String label, String value) {
      return getLabel(label) + value;
   }

   public static String getLabel(String label) {
      return AHTML.getLabelStr(labelFont, label + ": ");
   }

   public void addTable(String str) {
      addTable(new String[] {str});
   }

   public void addTable(String str, String str2) {
      addTable(new String[] {str, str2});
   }

   public void addTable(String str, String str2, String str3) {
      addTable(new String[] {str, str2, str3});
   }

   public void addTable(String[] strs) {
      addTable(strs, 100);
   }

   public void addHtml(String html) {
      this.html.append(html);
   }

   public void addTable(String[] strs, int width) {
      if (strs.length == 1) {
         this.html.append(AHTML.simpleTable(strs[0]));
      } else {
         this.html.append(AHTML.multiColumnTable(strs, width));
      }
   }

   public void addHeader(StateMachineArtifact sma, PreviewStyle... styles) throws OseeCoreException {
      SMAManager smaMgr = new SMAManager(sma);
      startBorderTable(100, false, "");
      addTable(getLabelValue("Title", sma.getDescriptiveName()));
      this.html.append(AHTML.multiColumnTable(new String[] {
            AHTML.getLabelStr(labelFont, "State: ") + smaMgr.getStateMgr().getCurrentStateName(),
            AHTML.getLabelStr(labelFont, "Type: ") + sma.getArtifactTypeName(),
            AHTML.getLabelStr(labelFont, "Id: ") + sma.getHumanReadableId()}));
      addTable(getLabelValue("Originator", smaMgr.getOriginator().getDescriptiveName()), getLabelValue("Creation Date",
            XDate.getDateStr(smaMgr.getLog().getCreationDate(), XDate.MMDDYYHHMM)));
      if (smaMgr.getSma() instanceof TeamWorkFlowArtifact)
         addTable(getLabelValue("Team", ((TeamWorkFlowArtifact) smaMgr.getSma()).getTeamName()), getLabelValue(
               "Assignees", Artifacts.toString("; ", smaMgr.getStateMgr().getAssignees())));
      else
         addTable(getLabelValue("Assignees", Artifacts.toString("; ", smaMgr.getStateMgr().getAssignees())));
      addTable(getLabelValue("Description", smaMgr.getSma().getDescription()));
      if (smaMgr.isCancelled()) {
         LogItem item = smaMgr.getLog().getStateEvent(LogType.StateCancelled);
         addTable(getLabelValue("Cancelled From", item.getState()));
         addTable(getLabelValue("Cancellation Reason", item.getMsg()));
      }
      if (sma instanceof TaskArtifact) {
         StateMachineArtifact parentArt = ((TaskArtifact) sma).getParentSMA();
         if (parentArt != null) {
            this.html.append(AHTML.multiColumnTable(new String[] {AHTML.getLabelStr(labelFont, "Parent Workflow: ") + parentArt.getDescriptiveName()}));
            this.html.append(AHTML.multiColumnTable(new String[] {AHTML.getLabelStr(labelFont, "Parent State: ") + ((TaskArtifact) sma).getSmaMgr().getStateMgr().getCurrentStateName()}));
         }
         SMAManager taskSmaMgr = new SMAManager(sma);
         this.html.append(AHTML.multiColumnTable(new String[] {AHTML.getLabelStr(labelFont, "Task Owner: ") + Artifacts.toString(
               "; ", taskSmaMgr.getStateMgr().getAssignees())}));
      }
      endBorderTable();
   }

   public void addFooter(StateMachineArtifact sma, PreviewStyle... styles) {
      this.html.append(AHTML.newline());

      if (PreviewStyle.contains(styles, PreviewStyle.HYPEROPEN)) {
         this.html.append("Start OSEE, select the ATS perspective and search by the Id shown.");
      }
   }

   public void addRelationsBlock(ATSArtifact artifact) {
      addRelationTable("Is Superceded By", AtsRelation.Supercedes_Supercedes, artifact);
      addRelationTable("Supercedes", AtsRelation.Supercedes_Superceded, artifact);
      addRelationTable("Issues Addressed By", AtsRelation.AddressesIssues_AddressesIssues, artifact);
      addRelationTable("Addresses Issues In", AtsRelation.AddressesIssues_IssuedArtifact, artifact);
      addRelationTable("Supports", AtsRelation.SupportingInfo_SupportedBy, artifact);
      addRelationTable("Is Supported By", AtsRelation.SupportingInfo_SupportingInfo, artifact);
   }

   public void addNotes(StateMachineArtifact artifact, String state) {
      if (artifact instanceof StateMachineArtifact) {
         String notesHtml = (artifact).getSmaMgr().getNotes().getTable(state);
         if (notesHtml.equals("")) return;
         this.html.append(notesHtml);
      }
   }

   public void addNotes(Artifact artifact) {
      if (artifact instanceof StateMachineArtifact) {
         String notesHtml = ((StateMachineArtifact) artifact).getSmaMgr().getNotes().getTable(null);
         if (notesHtml.equals("")) return;
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
         builder.append("<TABLE BORDER=\"1\" align=\"center\" cellspacing=\"1\" cellpadding=\"3%\" width=\"" + width + "%\"><THEAD><TR><TH>Type</TH>" + "<TH>Name</TH></THEAD></TR>");
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

   public void addRelationTable(String name, AtsRelation side, Artifact parent) {
      try {
         List<Artifact> arts = parent.getRelatedArtifacts(side);
         if (arts.size() == 0) return;
         startBorderTable(false, name);
         html.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Type", "Name", "Rationale"}));
         for (Artifact art : arts) {
            String rationale = "";
            RelationLink link = parent.getRelations(side, art).iterator().next();
            if (!link.getRationale().equals("")) rationale = link.getRationale();
            String hyperStr = Overview.getOpenHyperlinkHtml(art);
            html.append(AHTML.addRowMultiColumnTable(new String[] {art.getArtifactTypeName(), hyperStr, rationale}));
         }
         endBorderTable();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   public void addTeams(Collection<TeamWorkFlowArtifact> teams) throws OseeCoreException {
      startBorderTable(TABLE_WIDTH, false, "Team Workflows");
      String s = AHTML.beginMultiColumnTable(100, 1);
      s += AHTML.addHeaderRowMultiColumnTable(new String[] {"Type", "State"}, new Integer[] {70, 150});
      ArrayList<CellItem> cells = new ArrayList<CellItem>();
      for (TeamWorkFlowArtifact team : teams) {
         cells.add(new AHTML.CellItem(team.getHyperlinkHtml()));
         cells.add(new AHTML.CellItem(team.getSmaMgr().getStateMgr().getCurrentStateName()));
         s += AHTML.addRowMultiColumnTable(cells);
         cells.clear();
      }
      s += AHTML.endMultiColumnTable();
      html.append(s);

      endBorderTable();
   }

   public static String getOpenHyperlinkHtml(Artifact art) {
      return getOpenHyperlinkHtml(art.getDescriptiveName(), art);
   }

   public static String getOpenHyperlinkHtml(String name, String hrid) {
      return AHTML.getHyperlink(ResultBrowserHyperCmd.getHyperCmdStr(ResultBrowserHyperCmd.openAction, hrid), name);
   }

   public static String getOpenArtEditHyperlinkHtml(String name, String hrid) {
      return AHTML.getHyperlink(ResultBrowserHyperCmd.getHyperCmdStr(ResultBrowserHyperCmd.openArtifactEditor, hrid),
            name);
   }

   public static String getOpenArtViewHyperlinkHtml(String name, String hrid) {
      return AHTML.getHyperlink(ResultBrowserHyperCmd.getHyperCmdStr(ResultBrowserHyperCmd.openArtifactHyperViewer,
            hrid), name);
   }

   public static String getOpenHyperlinkHtml(String name, String guidOrHrid, int branchId) {
      return XResultHtml.getOpenHyperlinkHtml(name, guidOrHrid, branchId);
   }

   public static String getOpenHyperlinkHtml(String name, Artifact art) {
      return AHTML.getHyperlink(ResultBrowserHyperCmd.getHyperCmdStr(ResultBrowserHyperCmd.openAction, art.getGuid()),
            name);
   }

   public void addLog(StateMachineArtifact artifact) throws OseeCoreException {
      ATSLog artifactLog = artifact.getSmaMgr().getLog();
      if (artifactLog != null && artifactLog.getLogItems().size() > 0) addTable(artifact.getSmaMgr().getLog().getTable());
   }

   public void startStateBorderTable(SMAManager smaMgr, SMAState state) throws OseeCoreException {
      String caption = state.getName();
      String assgn = Artifacts.toString("; ", state.getAssignees());
      startStateBorderTable(smaMgr.getStateMgr().getCurrentStateName().equals(state.getName()), caption, assgn);
   }

   public void startStateBorderTable(boolean active, String name, String assignee) {
      if (assignee != null && !assignee.equals(""))
         startBorderTable(active, String.format("%s (%s)", name, assignee));
      else
         startBorderTable(active, String.format("%s", name));
   }

   public void startBorderTable(boolean active, String caption) {
      this.html.append(AHTML.startBorderTable(TABLE_WIDTH, (active) ? activeColor : normalColor, caption));
   }

   public void startBorderTable(int width, boolean active, String caption) {
      this.html.append(AHTML.startBorderTable(width, (active) ? activeColor : normalColor, caption));
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