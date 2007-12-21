/*
 * Created on Dec 19, 2007
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.editor.toolbar;

import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.editor.SMAManager;

/**
 * @author Donald G. Dunne
 */
public interface IAtsEditorToolBarService {

   public boolean showInToolbar(SMAManager smaMgr);

   public void dispose();

   public void refreshToolbarAction();

   public Action getToolbarAction(SMAManager smaMgr);

}
