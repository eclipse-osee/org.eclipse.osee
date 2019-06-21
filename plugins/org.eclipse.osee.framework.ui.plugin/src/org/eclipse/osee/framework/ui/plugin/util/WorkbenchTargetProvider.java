/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.plugin.util;

import org.eclipse.swt.graphics.Image;

/**
 * This provides customization for osee.db
 *
 * @author Branden W. Phillips
 */
public interface WorkbenchTargetProvider {
   Image getWorkbenchImage();

   String getText();
}
