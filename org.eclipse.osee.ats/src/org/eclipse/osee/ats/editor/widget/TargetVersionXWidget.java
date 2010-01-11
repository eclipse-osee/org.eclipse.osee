/*
 * Created on Aug 2, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.editor.widget;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact.VersionReleaseType;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.PromptChangeUtil;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelValueSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

/**
 * @author Donald G. Dunne
 */
public class TargetVersionXWidget extends XHyperlinkLabelValueSelection {

   private final StateMachineArtifact sma;

   /**
    * @param label
    */
   public TargetVersionXWidget(IManagedForm managedForm, final StateMachineArtifact sma, Composite composite, int horizontalSpan, XModifiedListener xModListener) {
      super("Target Version");
      this.sma = sma;
      if (xModListener != null) {
         addXModifiedListener(xModListener);
      }
      setFillHorizontally(false);
      setEditable(!sma.isReadOnly());
      super.createWidgets(managedForm, composite, horizontalSpan);
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      // Don't transition without targeted version if so configured
      try {
         boolean required =
               sma.teamDefHasWorkRule(AtsWorkDefinitions.RuleWorkItemId.atsRequireTargetedVersion.name()) || sma.getWorkPageDefinition().hasWorkRule(
                     AtsWorkDefinitions.RuleWorkItemId.atsRequireTargetedVersion.name());

         if (required && sma.getTargetedForVersion() == null) {
            status = new Status(IStatus.ERROR, AtsPlugin.PLUGIN_ID, "Workflow must be targeted for a version.");
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return status;
   }

   @Override
   public boolean handleSelection() {
      try {
         if (PromptChangeUtil.promptChangeVersion(sma,
               AtsUtil.isAtsAdmin() ? VersionReleaseType.Both : VersionReleaseType.UnReleased, false)) {
            refresh();
            sma.getEditor().onDirtied();
            return true;
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   @Override
   public String getCurrentValue() {
      try {
         if (sma.getTargetedForVersion() != null) {
            return String.valueOf(sma.getTargetedForVersion());
         } else {
            return "<edit>";
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
         return ex.getLocalizedMessage();
      }
   }

}
