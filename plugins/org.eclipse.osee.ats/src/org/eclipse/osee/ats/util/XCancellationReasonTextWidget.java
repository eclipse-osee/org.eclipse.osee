/*
 * Created on May 11, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util;

import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.swt.Widgets;

/**
 * @author Donald G. Dunne
 */
public class XCancellationReasonTextWidget extends XText implements IArtifactWidget {

   private StateMachineArtifact sma;

   public XCancellationReasonTextWidget(StateMachineArtifact sma) throws OseeCoreException {
      super("Cancallation Reason");
      setArtifact(sma, null);
   }

   @Override
   public Result isDirty() throws OseeCoreException {
      if (!Widgets.isAccessible(getControl())) return Result.FalseResult;
      if (!getText().equals(sma.getLog().getCancellationReason())) {
         return new Result(true, "Cancallation Reason dirty");
      }
      return Result.FalseResult;
   }

   @Override
   public void revert() throws OseeCoreException {
   }

   @Override
   public void saveToArtifact() throws OseeCoreException {
      if (Strings.isValid(getText())) {
         sma.getLog().setCancellationReason(getText());
      }
   }

   @Override
   public void setArtifact(Artifact artifact, String attrName) throws OseeCoreException {
      if (artifact instanceof StateMachineArtifact) {
         this.sma = (StateMachineArtifact) artifact;
         setText(sma.getLog().getCancellationReason());
      }
   }

}
