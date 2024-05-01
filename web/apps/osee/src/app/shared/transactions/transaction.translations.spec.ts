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
import { TransactionTranslations } from './transactions.translations';

describe('Transaction Translations', () => {
	let translate: TransactionTranslations;
	beforeEach(() => {
		translate = new TransactionTranslations();
	});
	it('should copy a value', () => {
		expect(translate.transform('interfaceMaxSimultaneity')).toEqual(
			'Interface Maximum Simultaneity'
		);
	});
	it('should return value back', () => {
		expect(translate.transform('abcdef')).toEqual('abcdef');
	});
	it('should contain interface min simultaneity', () => {
		expect(translate.contains('interfaceMaxSimultaneity')).toEqual(true);
	});
	it('should not contain abcdef', () => {
		expect(translate.contains('abcdef')).toEqual(false);
	});
});
