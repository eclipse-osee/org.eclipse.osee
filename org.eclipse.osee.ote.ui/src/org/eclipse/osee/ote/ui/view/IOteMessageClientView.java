/*
 * Created on Dec 8, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.ui.view;

import org.eclipse.osee.ote.client.msg.IOteMessageService;
import org.eclipse.ui.IViewPart;

/**
 * @author Ken J. Aguilar
 *
 */
public interface IOteMessageClientView extends IViewPart{

	void oteMessageServiceAcquired(IOteMessageService service);
	void oteMessageServiceReleased();
	
}
