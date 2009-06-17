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
package org.eclipse.osee.ote.core.environment;

import java.io.Serializable;
import org.eclipse.osee.ote.core.OSEEPerson1_4;


public final class UserTestSessionKey implements Serializable{
	
	private static final long serialVersionUID = -1445868944158880309L;
	
	private final long value;
	private final OSEEPerson1_4 user;
	
	public UserTestSessionKey(OSEEPerson1_4 user) {
		this.user = user;
		value = System.nanoTime();
	}
	
	long getKeyValue() {
		return value;
	}

	public boolean equals(Object obj) {
		if (obj instanceof UserTestSessionKey) {
			final UserTestSessionKey other = (UserTestSessionKey) obj;
			return other.value == this.value;
		} else {
			return false;
		}
	}

	public int hashCode() {
		return (int) (value ^ (value >>> 32));
	}
	
	public OSEEPerson1_4 getUser() {
		return user;
	}
}
