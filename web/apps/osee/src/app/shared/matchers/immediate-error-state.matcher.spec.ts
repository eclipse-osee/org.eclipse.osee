/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
import { AbstractControl } from '@angular/forms';
import { ImmediateErrorStateMatcher } from './immediate-error-state.matcher';

describe('ImmediateErrorStateMatcher', () => {
	const matcher = new ImmediateErrorStateMatcher();

	it('reports an error for an invalid, untouched, pristine control', () => {
		const control = {
			invalid: true,
			touched: false,
			dirty: false,
		} as AbstractControl;
		expect(matcher.isErrorState(control, null)).toBe(true);
	});

	it('reports no error for a valid control', () => {
		const control = {
			invalid: false,
			touched: true,
			dirty: true,
		} as AbstractControl;
		expect(matcher.isErrorState(control, null)).toBe(false);
	});

	it('reports no error for a null control', () => {
		expect(matcher.isErrorState(null, null)).toBe(false);
	});
});
