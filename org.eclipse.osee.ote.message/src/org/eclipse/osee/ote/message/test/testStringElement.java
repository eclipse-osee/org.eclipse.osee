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
package org.eclipse.osee.ote.message.test;

import junit.framework.TestCase;

import org.eclipse.osee.ote.message.data.HeaderData;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.elements.StringElement;


public class testStringElement extends TestCase{

	
	public void testZeroize() {
		for (int i = 0; i < 10; i++) {
			final HeaderData hd = new HeaderData("test_data", new MemoryResource(new byte[128], i, 128));
			StringElement e1 = new StringElement(null, "test string1", hd, 0, 0, (8*10) -1);
			StringElement e2 = new StringElement(null, "test string2", hd, 10, 0, (8*10) -1);
			StringElement e3 = new StringElement(null, "test string3", hd, 20, 0, (8*10) -1);

			String s1 = "aaaa bb  c";
			String s2 = "zeroizing2";
			String s3 = "1234567890";

			e1.setValue(s1);
			e2.setValue(s2);
			e3.setValue(s3);

			check(e1, s1);
			check(e2, s2);
			check(e3, s3);

			e2.zeroize();
			checkEmpty(e2);
			
			check(e1, s1);
			check(e2, "");
			check(e3, s3);

			e2.setValue(s2);
			e1.zeroize();
			checkEmpty(e1);
			
			check(e1, "");
			check(e2, s2);
			check(e3, s3);

			e1.setValue(s1);
			e3.zeroize();

			check(e1, s1);
			check(e2, s2);
			check(e3, "");
		}
	}

	
	public void testStringsTooBig() {
		for (int i = 0; i < 10; i++) {
			final HeaderData hd = new HeaderData("test_data", new MemoryResource(new byte[128], i, 128));

			String s1 = "aaaa bb  c";
			String s2 = "zeroizing2";
			String s3 = "1234567890";
			for (int j = 1; j < 10; j ++) {
				StringElement e1 = new StringElement(null, "test string1", hd, 0, 0, (8*j) -1);
				StringElement e2 = new StringElement(null, "test string2", hd, 10, 0, (8*j) -1);
				StringElement e3 = new StringElement(null, "test string3", hd, 20, 0, (8*j) -1);

				e1.setValue(s1);
				e2.setValue(s2);
				e3.setValue(s3);

				check(e1, s1.substring(0, j));
				check(e2, s2.substring(0, j));
				check(e3, s3.substring(0, j));

				e3.setValue(s3);
				e2.setValue(s2);
				e1.setValue(s1);
				
				check(e1, s1.substring(0, j));
				check(e2, s2.substring(0, j));
				check(e3, s3.substring(0, j));
			}
		}
	}
	
	public void testStringsTooSmall() {
		for (int i = 0; i < 10; i++) {
			final HeaderData hd = new HeaderData("test_data", new MemoryResource(new byte[128], i, 128));
			StringElement e1 = new StringElement(null, "test string1", hd, 0, 0, (8*10) -1);
			StringElement e2 = new StringElement(null, "test string2", hd, 10, 0, (8*10) -1);
			StringElement e3 = new StringElement(null, "test string3", hd, 20, 0, (8*10) -1);
			String ss1 = "aaaa bb  c";
			String ss2 = "zeroizing2";
			String ss3 = "1234567890";
			for (int j = 1; j <= 10; j ++) {
				String s1 = ss1.substring(0, j);
				String s2 = ss2.substring(0, j);
				String s3 = ss3.substring(0, j);
				e1.setValue(s1);
				e2.setValue(s2);
				e3.setValue(s3);

				check(e1, s1);
				check(e2, s2);
				check(e3, s3);

				e3.setValue(s3);
				e2.setValue(s2);
				e1.setValue(s1);
				
				check(e1, s1);
				check(e2, s2);
				check(e3, s3);
			}
		}
	}
	private void check(StringElement elem, String value) {
		if (!elem.getValue().equals(value)) {
			failNotEquals(elem.getName(), value, elem.getValue());
		}
	}
	
	private void checkEmpty(StringElement elem) {
		if (!elem.isEmpty()) {
			failNotEquals(elem.getName(), "rmpty", "not empty");
		}
	}
}
