/*
 * Created on Jul 18, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.swt.widgets.Menu;

/**
 * @author Donald G. Dunne
 */
public interface IWorldEditor {

   public abstract void reflow();

   public void setTableTitle(final String title, final boolean warning);

   public void reSearch() throws OseeCoreException;

   public IActionable getIActionable();

   public IWorldEditorProvider getWorldEditorProvider();

   public void createToolBarPulldown(Menu menu);

   public String getCurrentTitleLabel();

}
