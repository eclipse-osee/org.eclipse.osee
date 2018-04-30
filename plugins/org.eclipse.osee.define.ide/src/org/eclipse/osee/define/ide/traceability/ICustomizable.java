/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.ide.traceability;

import org.eclipse.osee.framework.jdk.core.type.IVariantData;

/**
 * @author Roberto E. Escobar
 */
public interface ICustomizable {

   public IVariantData getOptions();

   public void setOptions(IVariantData options);
}
