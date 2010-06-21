/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.type;

/**
 * @author Ryan D. Brooks
 */
public class CaseInsensitiveString implements CharSequence {
	private String string;

	public CaseInsensitiveString(String string) {
		this.string = string;
	}

	@Override
	public String toString() {
		return string;
	}

	@Override
	public int hashCode() {
		return  ((string == null) ? 0 : string.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CaseInsensitiveString) {
			if (string == null) {
				return string == ((CaseInsensitiveString) obj).string;
			} else {
				return string.equalsIgnoreCase(((CaseInsensitiveString) obj).string);
			}
		} else if (obj instanceof String) {
			return string.equalsIgnoreCase(((String) obj));
		}
		return false;
	}

	@Override
	public int length() {
		return string == null ? 0 : string.length();
	}

	@Override
	public char charAt(int index) {
		return string.charAt(index);
	}

	@Override
	public CharSequence subSequence(int beginIndex, int endIndex) {
		return string.subSequence(beginIndex, endIndex);
	}
}