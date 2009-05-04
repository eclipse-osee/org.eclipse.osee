/*
 * Created on May 4, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.editor.service;

import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;

/**
 * @author Donald G. Dunne
 */
public class ReadOnlyHyperlinkListener implements IHyperlinkListener {

   private final SMAManager smaMgr;

   public ReadOnlyHyperlinkListener(SMAManager smaMgr) {
      this.smaMgr = smaMgr;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.events.IHyperlinkListener#linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent)
    */
   @Override
   public void linkActivated(HyperlinkEvent e) {
      if (smaMgr.isHistoricalVersion())
         AWorkbench.popup(
               "Historical Error",
               "You can not change a historical version of " + smaMgr.getSma().getArtifactTypeName() + ":\n\n" + smaMgr.getSma());

      else
         AWorkbench.popup("Authentication Error",
               "You do not have permissions to edit " + smaMgr.getSma().getArtifactTypeName() + ":" + smaMgr.getSma());
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.events.IHyperlinkListener#linkEntered(org.eclipse.ui.forms.events.HyperlinkEvent)
    */
   @Override
   public void linkEntered(HyperlinkEvent e) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.events.IHyperlinkListener#linkExited(org.eclipse.ui.forms.events.HyperlinkEvent)
    */
   @Override
   public void linkExited(HyperlinkEvent e) {
   }

}
