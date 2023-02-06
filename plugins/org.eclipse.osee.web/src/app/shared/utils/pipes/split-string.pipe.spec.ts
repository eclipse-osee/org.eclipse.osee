/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { SplitStringPipe } from 'src/app/shared/utils/pipes/split-string.pipe';

describe('SplitOnSemicolonPipe', () => {
	it('create an instance', () => {
		const pipe = new SplitStringPipe();
		expect(pipe).toBeTruthy();
	});

	it('should return an array of strings', () => {
		const pipe = new SplitStringPipe();
		let result = pipe.transform('testing', ';');
		expect(result).toEqual(['testing']);

		result = pipe.transform('testing;testing', ';');
		expect(result).toEqual(['testing', 'testing']);

		result = pipe.transform('testing,testing,testing', ',');
		expect(result).toEqual(['testing', 'testing', 'testing']);
	});

	it('should split on semicolon by default', () => {
		const pipe = new SplitStringPipe();
		let result = pipe.transform('testing;testing');
		expect(result).toEqual(['testing', 'testing']);
	});

	it('should return an empty array', () => {
		const pipe = new SplitStringPipe();
		let result = pipe.transform(1, ';');
		expect(result).toEqual([]);
	});
});
