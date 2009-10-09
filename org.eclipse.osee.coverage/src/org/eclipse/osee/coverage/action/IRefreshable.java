/*
 * Created on Oct 9, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.action;

import java.util.Collection;
import org.eclipse.osee.coverage.editor.ICoverageEditorItem;

/**
 * @author Donald G. Dunne
 */
public interface IRefreshable {

   public void refresh(Object element);

   public void update(Object element);
}
