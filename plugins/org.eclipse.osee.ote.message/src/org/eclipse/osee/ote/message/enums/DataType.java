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
package org.eclipse.osee.ote.message.enums;

import java.io.Serializable;
import org.eclipse.osee.ote.message.interfaces.INamespace;



/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public interface DataType extends Serializable, INamespace {

	String name();
	int getToolingDepth();
	int getToolingBufferSize();
}
