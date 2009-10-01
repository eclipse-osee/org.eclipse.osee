/*
 * Created on Aug 2, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.editor.widget;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.VersionArtifact.VersionReleaseType;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.AtsUtil;
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

   private final SMAManager smaMgr;

   /**
    * @param label
    */
   public TargetVersionXWidget(IManagedForm managedForm, final SMAManager smaMgr, Composite composite, int horizontalSpan, XModifiedListener xModListener) {
      super("Target Version");
      this.smaMgr = smaMgr;
      if (xModListener != null) {
         addXModifiedListener(xModListener);
      }
      setFillHorizontally(false);
      try {
         setEditable(!smaMgr.getSma().isReadOnly());
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
         setEditable(false);
      }
      super.createWidgets(managedForm, composite, horizontalSpan);
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      // Don't transition without targeted version if so configured
      try {
         boolean required =
               smaMgr.teamDefHasWorkRule(AtsWorkDefinitions.RuleWorkItemId.atsRequireTargetedVersion.name()) || smaMgr.getWorkPageDefinition().hasWorkRule(
                     AtsWorkDefinitions.RuleWorkItemId.atsRequireTargetedVersion.name());

         if (required && smaMgr.getTargetedForVersion() == null) {
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
         if (smaMgr.promptChangeVersion(AtsUtil.isAtsAdmin() ? VersionReleaseType.Both : VersionReleaseType.UnReleased,
               false)) {
            refresh();
            smaMgr.getEditor().onDirtied();
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
         if (smaMgr.getTargetedForVersion() != null) {
            return String.valueOf(smaMgr.getTargetedForVersion());
         } else {
            return "<edit>";
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
         return ex.getLocalizedMessage();
      }
   }

}
