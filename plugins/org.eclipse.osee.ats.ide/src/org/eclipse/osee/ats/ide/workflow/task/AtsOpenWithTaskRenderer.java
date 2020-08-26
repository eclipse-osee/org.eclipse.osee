/*
 * Created on Feb 3, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.ide.workflow.task;

import static org.eclipse.osee.framework.core.enums.PresentationType.PREVIEW;
import static org.eclipse.osee.framework.core.util.RendererOption.OPEN_OPTION;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.task.related.AbstractShowRelatedAction;
import org.eclipse.osee.ats.ide.workflow.task.related.ShowRelatedRequirementAction;
import org.eclipse.osee.ats.ide.workflow.task.related.ShowRelatedRequirementDiffsAction;
import org.eclipse.osee.ats.ide.workflow.task.related.ShowRelatedRequirementInArtifactExplorerAction;
import org.eclipse.osee.ats.ide.workflow.task.related.ShowRelatedTasksAction;
import org.eclipse.osee.ats.ide.workflow.task.related.ShowRelatedTestCasesAction;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.MenuCmdDef;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;

/**
 * @author John R. Misinco
 */
public class AtsOpenWithTaskRenderer extends DefaultArtifactRenderer {
   private static final String Option_TRACEABILITY = "tasks.traceability.option";
   private static final String Option_RELATED_REQ = "tasks.related.requirement.option";
   private static final String Option_RELATED_REQ_ART_EXP = "tasks.related.requirement.art.explorer.option";
   private static final String Option_REQ_DIFF = "tasks.requirement.diff.option";
   private static final String Option_RELATED_TASK = "tasks.related.task.option";

   public AtsOpenWithTaskRenderer(Map<RendererOption, Object> rendererOptions) {
      super(rendererOptions);
   }

   public AtsOpenWithTaskRenderer() {
      super(new HashMap<RendererOption, Object>());
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, Map<RendererOption, Object> rendererOptions) {
      int rating = NO_MATCH;
      try {
         if (artifact instanceof IAtsTask && AtsApiService.get().getTaskRelatedService().isAutoGenChangeReportRelatedTask(
            (IAtsTask) artifact)) {
            rating = getPresentationType(presentationType);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return rating;
   }

   public int getPresentationType(PresentationType presentationType) {
      int type = NO_MATCH;
      if (presentationType.matches(PresentationType.PREVIEW)) {
         type = PRESENTATION_SUBTYPE_MATCH;
      } else if (presentationType.matches(PresentationType.F5_DIFF)) {
         type = SPECIALIZED_KEY_MATCH;
      }
      return type;
   }

   @Override
   public int minimumRanking() {
      return ARTIFACT_TYPE_MATCH;
   }

   @Override
   public String getName() {
      return "ATS Task Editor";
   }

   @Override
   public void addMenuCommandDefinitions(ArrayList<MenuCmdDef> commands, Artifact artifact) {
      commands.add(new MenuCmdDef(CommandGroup.PREVIEW, PREVIEW, "Show Related Test Cases", AtsImage.TASK,
         OPEN_OPTION.getKey(), Option_TRACEABILITY));

      commands.add(new MenuCmdDef(CommandGroup.PREVIEW, PREVIEW, "Show Related Requirement", AtsImage.TASK,
         OPEN_OPTION.getKey(), Option_RELATED_REQ));

      commands.add(new MenuCmdDef(CommandGroup.PREVIEW, PREVIEW, "Show Related Requirement in Artifact Explorer",
         FrameworkImage.ARTIFACT_EXPLORER, OPEN_OPTION.getKey(), Option_RELATED_REQ_ART_EXP));

      commands.add(new MenuCmdDef(CommandGroup.PREVIEW, PREVIEW, "Show Requirement Diffs - F5", AtsImage.TASK,
         OPEN_OPTION.getKey(), Option_REQ_DIFF));

      commands.add(new MenuCmdDef(CommandGroup.PREVIEW, PREVIEW, "Show Related Tasks", AtsImage.TASK,
         OPEN_OPTION.getKey(), Option_RELATED_TASK));

   }

   @Override
   public AtsOpenWithTaskRenderer newInstance() {
      return new AtsOpenWithTaskRenderer(new HashMap<RendererOption, Object>());
   }

   @Override
   public AtsOpenWithTaskRenderer newInstance(Map<RendererOption, Object> rendererOptions) {
      return new AtsOpenWithTaskRenderer(rendererOptions);
   }

   @Override
   public void open(final List<Artifact> artifacts, PresentationType presentationType) {
      final List<TaskArtifact> taskArts = new LinkedList<>();
      for (Artifact art : artifacts) {
         if (art instanceof TaskArtifact) {
            taskArts.add((TaskArtifact) art);
         }
      }

      ISelectedAtsArtifacts selectedArtifacts = new ISelectedAtsArtifacts() {

         @Override
         public Set<Artifact> getSelectedWorkflowArtifacts() {
            return null;
         }

         @Override
         public List<Artifact> getSelectedAtsArtifacts() {
            return null;
         }

         @Override
         public List<TaskArtifact> getSelectedTaskArtifacts() {
            return taskArts;
         }
      };

      if (!taskArts.isEmpty()) {
         AbstractShowRelatedAction action = null;
         String openOption = (String) getRendererOptionValue(OPEN_OPTION);
         if (Option_RELATED_REQ.equals(openOption)) {
            action = new ShowRelatedRequirementAction(selectedArtifacts);
         } else if (Option_REQ_DIFF.equals(openOption) || presentationType.equals(PresentationType.F5_DIFF)) {
            action = new ShowRelatedRequirementDiffsAction(selectedArtifacts);
         } else if (Option_RELATED_TASK.equals(openOption)) {
            action = new ShowRelatedTasksAction(selectedArtifacts);
         } else if (Option_TRACEABILITY.equals(openOption)) {
            action = new ShowRelatedTestCasesAction(selectedArtifacts);
         } else if (Option_RELATED_REQ_ART_EXP.equals(openOption)) {
            action = new ShowRelatedRequirementInArtifactExplorerAction(selectedArtifacts);
         }
         if (action != null) {
            action.run();
         }
      }
   }

}