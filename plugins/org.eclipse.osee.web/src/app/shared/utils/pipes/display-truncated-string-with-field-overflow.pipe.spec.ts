/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { DisplayTruncatedStringWithFieldOverflowPipe } from './display-truncated-string-with-field-overflow.pipe';

describe('DisplayTruncatedStringWithFieldOverflowPipe', () => {
	it('create an instance', () => {
		const pipe = new DisplayTruncatedStringWithFieldOverflowPipe();
		expect(pipe).toBeTruthy();
	});

	it('create a string of length 15+3', () => {
		const pipe = new DisplayTruncatedStringWithFieldOverflowPipe();
		const result = pipe.transform(
			'ldaj;fjasdjflkdajgddlagj;aljgdlfjalkejriopetopdoghapohgkldnvz,fjg',
			...[15]
		);
		expect(result.length).toEqual(18);
	});

	it('create a string of length 10+3', () => {
		const pipe = new DisplayTruncatedStringWithFieldOverflowPipe();
		const result = pipe.transform(
			'ldaj;fjasdjflkdajgddlagj;aljgdlfjalkejriopetopdoghapohgkldnvz,fjg'
		);
		expect(result.length).toEqual(13);
	});

	it('create a string of length 11', () => {
		const pipe = new DisplayTruncatedStringWithFieldOverflowPipe();
		const result = pipe.transform('Hello World', 15);
		expect(result.length).toEqual(11);
	});
});
