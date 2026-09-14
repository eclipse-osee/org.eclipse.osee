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
import { MULTIPLICITY_ID } from './multiplicity-id';
import {
	isAttributeInstanceDeletable,
	isRequiredMultiplicity,
} from './multiplicity-rules';

type TestAttr = {
	name?: string;
	typeId: string;
	multiplicity?: { id?: string };
};

describe('multiplicity-rules', () => {
	describe('isRequiredMultiplicity', () => {
		it('is true for EXACTLY_ONE', () => {
			expect(
				isRequiredMultiplicity({
					typeId: 'a',
					multiplicity: { id: MULTIPLICITY_ID.EXACTLY_ONE },
				})
			).toBe(true);
		});

		it('is true for AT_LEAST_ONE', () => {
			expect(
				isRequiredMultiplicity({
					typeId: 'a',
					multiplicity: { id: MULTIPLICITY_ID.AT_LEAST_ONE },
				})
			).toBe(true);
		});

		it('is false for ANY', () => {
			expect(
				isRequiredMultiplicity({
					typeId: 'a',
					multiplicity: { id: MULTIPLICITY_ID.ANY },
				})
			).toBe(false);
		});

		it('is false for ZERO_OR_ONE', () => {
			expect(
				isRequiredMultiplicity({
					typeId: 'a',
					multiplicity: { id: MULTIPLICITY_ID.ZERO_OR_ONE },
				})
			).toBe(false);
		});

		it('is false when multiplicity is missing', () => {
			expect(isRequiredMultiplicity({ typeId: 'a' })).toBe(false);
		});
	});

	describe('isAttributeInstanceDeletable', () => {
		it('never allows deleting the Name attribute', () => {
			const name: TestAttr = {
				name: 'Name',
				typeId: 'name-type',
				multiplicity: { id: MULTIPLICITY_ID.ANY },
			};
			expect(isAttributeInstanceDeletable(name, [name])).toBe(false);
		});

		it('is case-insensitive about the Name attribute', () => {
			const name: TestAttr = {
				name: 'name',
				typeId: 'name-type',
				multiplicity: { id: MULTIPLICITY_ID.ANY },
			};
			expect(isAttributeInstanceDeletable(name, [name])).toBe(false);
		});

		it('allows deleting an optional (ANY) attribute', () => {
			const attr: TestAttr = {
				name: 'Optional',
				typeId: 'opt',
				multiplicity: { id: MULTIPLICITY_ID.ANY },
			};
			expect(isAttributeInstanceDeletable(attr, [attr])).toBe(true);
		});

		it('allows deleting a ZERO_OR_ONE attribute', () => {
			const attr: TestAttr = {
				name: 'Maybe',
				typeId: 'maybe',
				multiplicity: { id: MULTIPLICITY_ID.ZERO_OR_ONE },
			};
			expect(isAttributeInstanceDeletable(attr, [attr])).toBe(true);
		});

		it('does NOT allow deleting the only instance of a required type', () => {
			const attr: TestAttr = {
				name: 'Qualification Method',
				typeId: 'qual',
				multiplicity: { id: MULTIPLICITY_ID.AT_LEAST_ONE },
			};
			expect(isAttributeInstanceDeletable(attr, [attr])).toBe(false);
		});

		it('allows deleting an extra instance of a required type', () => {
			const first: TestAttr = {
				name: 'Qualification Method',
				typeId: 'qual',
				multiplicity: { id: MULTIPLICITY_ID.AT_LEAST_ONE },
			};
			const second: TestAttr = { ...first };
			// Two instances of a required type -> either is deletable.
			expect(isAttributeInstanceDeletable(first, [first, second])).toBe(
				true
			);
		});

		it('does NOT allow deleting the only instance of an EXACTLY_ONE type', () => {
			const attr: TestAttr = {
				name: 'Required One',
				typeId: 'one',
				multiplicity: { id: MULTIPLICITY_ID.EXACTLY_ONE },
			};
			expect(isAttributeInstanceDeletable(attr, [attr])).toBe(false);
		});
	});
});
