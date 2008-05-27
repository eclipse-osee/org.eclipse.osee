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

package org.eclipse.osee.framework.ui.skynet.mergeWizard;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Theron Virgin
 */
public interface IEmbeddedAttributeEditor {

   public void update(Object value);

   public boolean create(Composite composite, GridData gd);

   public boolean commit();

   public boolean canClear();

   public boolean canFinish();

}
