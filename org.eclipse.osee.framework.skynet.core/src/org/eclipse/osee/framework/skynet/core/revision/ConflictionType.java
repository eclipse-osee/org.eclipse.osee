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
package org.eclipse.osee.framework.skynet.core.revision;

/**
 * Types of conflicts that can occur between data on seperate branches.
 * 
 * @author Robert A. Fisher
 */
public enum ConflictionType {
   CONFLICTING_CHANGE(), CONFLICTING_DELETE();
}
