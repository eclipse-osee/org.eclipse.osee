/*
 * Created on May 3, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.core.runtime.IStatus;

/**
 * @author Donald G. Dunne
 */
public interface IXWidgetValidityProvider {

   public IStatus isValid(XWidget widget);
}
