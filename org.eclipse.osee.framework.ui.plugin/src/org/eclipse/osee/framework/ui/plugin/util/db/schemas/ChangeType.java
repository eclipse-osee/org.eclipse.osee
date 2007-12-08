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
package org.eclipse.osee.framework.ui.plugin.util.db.schemas;

import java.io.Serializable;

/**
 * Values for Change Types.
 * 
 * @author Robert A. Fisher
 */
public enum ChangeType implements Serializable {
   INCOMING, OUTGOING, CONFLICTING
}
