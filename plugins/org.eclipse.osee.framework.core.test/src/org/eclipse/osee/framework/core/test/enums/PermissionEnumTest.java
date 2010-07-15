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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.junit.Test;

/**
 * Test Case for {@link PermissionEnum}
 * 
 * @author Roberto E. Escobar
 */
public class PermissionEnumTest {

	private static class PermissionTest {
		PermissionEnum permission;
		boolean[] expectedMatches;

		public PermissionTest(PermissionEnum permission, boolean... expectedMatches) {
			super();
			this.permission = permission;
			this.expectedMatches = expectedMatches;
		}
	}

	@Test
	public void testMatches() {
		PermissionEnum[] toMatches = PermissionEnum.values();
		System.out.println("Boolean order: " + Arrays.deepToString(toMatches));
		//NONE, READ, WRITE, FULLACCESS, LOCK, DENY

		Collection<PermissionTest> datas = new ArrayList<PermissionTest>();
		datas.add(new PermissionTest(PermissionEnum.DENY, false, false, false, false, false, false));
		datas.add(new PermissionTest(PermissionEnum.FULLACCESS, true, true, true, true, false, false));
		datas.add(new PermissionTest(PermissionEnum.LOCK, false, true, false, false, false, false));
		datas.add(new PermissionTest(PermissionEnum.NONE, true, false, false, false, false, false));
		datas.add(new PermissionTest(PermissionEnum.READ, true, true, false, false, false, false));
		datas.add(new PermissionTest(PermissionEnum.WRITE, true, true, true, false, false, false));

		int test = 0;
		for (PermissionTest data : datas) {
			Assert.assertEquals("test data error", toMatches.length, data.expectedMatches.length);

			for (int index = 0; index < toMatches.length; index++) {
				boolean expectedMatch = data.expectedMatches[index];
				PermissionEnum toMatch = toMatches[index];

				boolean actualMatch = data.permission.matches(toMatch);
				String message =
							String.format("Test[%s] [%s matches %s] expected:[%s] actual:[%s]", test, data.permission,
										toMatch, expectedMatch, actualMatch);
				Assert.assertEquals(message, expectedMatch, actualMatch);
			}
			test++;
		}
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
		PermissionEnum[] enums = PermissionEnum.values();
		Assert.assertEquals(expectedNames.length, actualNames.length);
		Assert.assertEquals(expectedNames.length, enums.length);
		for (int index = 0; index < expectedNames.length; index++) {
			Assert.assertEquals(expectedNames[index], enums[index].getName());
			Assert.assertEquals(expectedNames[index], actualNames[index]);
		}
	}
}
