/*********************************************************************
 * Copyright (c) 2018 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.widgets.checkbox;

/**
 * @author Donald G. Dunne
 */
public interface ICheckBoxStateTreeViewer {

   boolean isEnabled(Object element);

   boolean isChecked(Object element);

   void setEnabled(Object object, boolean enabled);

   void deSelectAll();

}
