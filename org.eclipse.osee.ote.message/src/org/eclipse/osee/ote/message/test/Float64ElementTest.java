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

import java.util.Random;

import junit.framework.TestCase;

import org.eclipse.osee.ote.message.data.HeaderData;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.elements.Float64Element;

public class Float64ElementTest extends TestCase{

	public void testFloat64() {
		final HeaderData hd = new HeaderData("test_data", new MemoryResource(new byte[64], 0, 64));
		Float64Element[] e = new Float64Element[8];
		for (int i = 0; i < e.length; i++) {
			e[i] = new Float64Element(null, "Float64@"+i, hd, i*8, 0, 63);
		}
		Random r = new Random(System.currentTimeMillis());
		double[] vals = new double[e.length];
		for (int i = 0; i < 1000; i++) {
			generateAscending(r, e, vals, e.length);
			check(e, vals, e.length);
			
			generateDescending(r, e, vals, e.length);
			check(e, vals, e.length);
			
			// zeroize test
			for (int z = 0; z < e.length; z+=2) {
				e[z].zeroize();
				vals[z] = 0;
			}
			
			check(e, vals, e.length);
		}
	}
	
	private void generateAscending(Random r, Float64Element[] e, double[] expectedVals, int length) {
		for (int i = 0; i < length; i++) {
			double val = r.nextDouble();
			Float64Element el = e[i];
			el.setValue(val);
			expectedVals[i] = val;
			if (el.get() != expectedVals[i]) {
				failNotEquals(
						String.format("set/get fail on %s: msb=%d, lsb=%d", el.getName(), el.getMsb(), el.getLsb()), 
						expectedVals[i], 
						el.get());
			}
			
		}
	}
	
	private void generateDescending(Random r, Float64Element[] e, double[] expectedVals, int length) {
		for (int i = length - 1; i >= 0; i--) {
			double val = r.nextDouble();
			Float64Element el = e[i];
			el.setValue(val);
			expectedVals[i] = val;
			if (el.get() != expectedVals[i]) {
				failNotEquals(
						String.format("set/get fail on %s: msb=%d, lsb=%d", el.getName(), el.getMsb(), el.getLsb()), 
						Double.toHexString(expectedVals[i]), 
						Double.toHexString(el.get()));
			}
		}
	}
	
	private void check(Float64Element[] e, double[] expectedVals, int length) {
		for (int i = 0; i < length; i++) {
			Float64Element el = e[i];
			if (el.get() != expectedVals[i]) {
				failNotEquals(
						String.format("corruption detect on %s: msb=%d, lsb=%d", el.getName(), el.getMsb(), el.getLsb()), 
						expectedVals[i], 
						e[i].get());
			}
			
			String v = Double.toString(expectedVals[i]);
			if (!el.valueOf().equals(v)) {
				failNotEquals(
						String.format("set/get fail on %s: msb=%d, lsb=%d", el.getName(), el.getMsb(), el.getLsb()), 
						v, 
						el.valueOf());
			}
		}
	}
}
