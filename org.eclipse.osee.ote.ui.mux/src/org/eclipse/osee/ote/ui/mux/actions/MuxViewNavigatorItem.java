/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.mux.actions;

import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.ote.ui.mux.OteMuxImage;
import org.eclipse.osee.ote.ui.navigate.IOteNavigateItem;

public class MuxViewNavigatorItem implements IOteNavigateItem {

    public MuxViewNavigatorItem() {
	// TODO Auto-generated constructor stub
    }

    @Override
    public List<XNavigateItem> getNavigateItems() {
	XNavigateItem action = new XNavigateItemAction(null,
		new OpenMuxViewAction(), OteMuxImage.MUX, false);
	
	return Collections.singletonList(action);
    }

}
