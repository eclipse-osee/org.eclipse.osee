/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { HasValidNameDirective } from './has-valid-name.directive';

describe('HasValidNameDirective', () => {
	it('should create an instance', () => {
		const directive = new HasValidNameDirective();
		expect(directive).toBeTruthy();
	});
});
