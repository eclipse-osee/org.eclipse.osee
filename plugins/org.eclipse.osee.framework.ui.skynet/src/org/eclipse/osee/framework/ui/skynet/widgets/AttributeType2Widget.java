/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * Used to bind an attribute type without tie to artifact. For use when widget is in dialog or form where artifact has
 * not yet been created (eg: BLAMs).
 *
 * @author Donald G. Dunne
 */
public interface AttributeType2Widget {

   void setAttributeType2(AttributeTypeToken attributeTypeToken);

   AttributeTypeToken getAttributeType2();

}
