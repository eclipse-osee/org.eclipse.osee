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
import { FormatMillisecondsPipe } from './format-milliseconds.pipe';

describe('FormatMillisecondsPipe', () => {
	it('create an instance', () => {
		const pipe = new FormatMillisecondsPipe();
		expect(pipe).toBeTruthy();
	});

	it('formats milliseconds to a time string', () => {
		const pipe = new FormatMillisecondsPipe();
		expect(pipe.transform(0)).toEqual('00:00:00');
		expect(pipe.transform(100)).toEqual('00:00:00');
		expect(pipe.transform(2300)).toEqual('00:00:02');
		expect(pipe.transform(1245749)).toEqual('00:20:45');
		expect(pipe.transform(10239487)).toEqual('02:50:39');
		expect(pipe.transform('10239487')).toEqual('02:50:39');
		expect(pipe.transform(400783744)).toEqual('111:19:43');
		expect(pipe.transform('736YY89324')).toEqual('');
		expect(pipe.transform('')).toEqual('');
	});
});
