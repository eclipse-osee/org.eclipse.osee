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
package org.eclipse.osee.framework.core.test.enums;

import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test Case for {@link PermissionEnum}
 * 
 * @author Roberto E. Escobar
 */
public class PermissionEnumTest {

	@Ignore
	@Test
	public void testMatches() {
		//		PermissionEnum.getPermission(permissionId);
		//		PermissionEnum.getPermission(name);
		//		

		//		PermissionEnum value = null;
		//
		//		value.getName();
		//		value.getPermId()PermId();
		//		value.getRank();
		//
		//		boolean expectedMatch = false;
		//		boolean actualMatch = value.matches(toMatch);
		//		Assert.assertEquals(expectedMatch, actualMatch);

	}

	@Test
	public void testGetPermissionFromId() {
		for (PermissionEnum permission : PermissionEnum.values()) {
			int permissionId = permission.getRank();
			PermissionEnum enumFromId = PermissionEnum.getPermission(permissionId);
			Assert.assertEquals(permission, enumFromId);
		}
	}

	@Test
	public void testGetPermissionId() {
		int[] expectedIds = new int[] {5, 10, 20, 30, 40, 65535};
		PermissionEnum[] permissions = PermissionEnum.values();
		Assert.assertEquals(expectedIds.length, permissions.length);
		for (int index = 0; index < expectedIds.length; index++) {
			PermissionEnum permission = permissions[index];
			int actualId = permission.getRank();
			Assert.assertEquals(expectedIds[index], actualId);
		}
	}

	@Test
	public void testGetPermissionNames() {
		String[] expectedNames = new String[] {"None", "Read", "Write", "Full Access", "Lock", "Deny"};
		String[] actualNames = PermissionEnum.getPermissionNames();
		Assert.assertEquals(expectedNames.length, actualNames.length);
		for (int index = 0; index < expectedNames.length; index++) {
			Assert.assertEquals(expectedNames[index], actualNames[index]);
		}
	}
}
