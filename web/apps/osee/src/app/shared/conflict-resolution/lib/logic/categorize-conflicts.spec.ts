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
import { attribute } from '@osee/attributes/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import { categorizeConflicts } from './categorize-conflicts';

type attr = attribute<string, ATTRIBUTETYPEID>;

function makeAttr(id: string, value: string, typeId = '1000'): attr {
	return {
		id,
		typeId,
		gammaId: '5',
		value,
		name: 'Description',
		storeType: 'String',
		multiplicity: { id: '1', name: 'exactly one' },
	} as unknown as attr;
}

describe('categorizeConflicts', () => {
	it('ignores an edit that converged with the server (same value)', () => {
		const base = [makeAttr('a1', 'original')];
		const server = [makeAttr('a1', 'same-as-mine')];
		const pending = new Map([['a1', 'same-as-mine']]);

		const { conflicts, autoSaveAttrs } = categorizeConflicts(
			base,
			server,
			pending
		);
		expect(conflicts).toEqual([]);
		expect(autoSaveAttrs).toEqual([]);
	});

	it('auto-saves an edit the server did not touch', () => {
		const base = [makeAttr('a1', 'original')];
		const server = [makeAttr('a1', 'original')]; // server unchanged from base
		const pending = new Map([['a1', 'my new value']]);

		const { conflicts, autoSaveAttrs } = categorizeConflicts(
			base,
			server,
			pending
		);
		expect(conflicts).toEqual([]);
		expect(autoSaveAttrs).toHaveLength(1);
		// Carries the server's fresh gamma with the local value applied.
		expect(autoSaveAttrs[0].value).toBe('my new value');
		expect(autoSaveAttrs[0].id).toBe('a1');
	});

	it('flags a true value conflict when both sides diverged', () => {
		const base = [makeAttr('a1', 'original')];
		const server = [makeAttr('a1', 'their change')];
		const pending = new Map([['a1', 'my change']]);

		const { conflicts, autoSaveAttrs } = categorizeConflicts(
			base,
			server,
			pending
		);
		expect(autoSaveAttrs).toEqual([]);
		expect(conflicts).toHaveLength(1);
		expect(conflicts[0].serverDeleted).toBe(false);
		expect(conflicts[0].localValue).toBe('my change');
		expect(conflicts[0].serverAttr?.value).toBe('their change');
	});

	it('flags a delete conflict when the server removed the edited attribute', () => {
		const base = [makeAttr('a1', 'original')];
		const server: attr[] = []; // attribute gone from server
		const pending = new Map([['a1', 'my change']]);

		const { conflicts } = categorizeConflicts(base, server, pending);
		expect(conflicts).toHaveLength(1);
		expect(conflicts[0].serverDeleted).toBe(true);
		expect(conflicts[0].serverAttr).toBeUndefined();
	});

	it('skips a pending edit that has no matching base attribute', () => {
		const base: attr[] = [];
		const server = [makeAttr('a1', 'x')];
		const pending = new Map([['a1', 'my change']]);

		const { conflicts, autoSaveAttrs } = categorizeConflicts(
			base,
			server,
			pending
		);
		expect(conflicts).toEqual([]);
		expect(autoSaveAttrs).toEqual([]);
	});

	it('partitions a mixed set into conflicts and auto-saves', () => {
		const base = [
			makeAttr('a1', 'orig1'),
			makeAttr('a2', 'orig2'),
			makeAttr('a3', 'orig3'),
		];
		const server = [
			makeAttr('a1', 'orig1'), // untouched -> auto-save
			makeAttr('a2', 'their2'), // diverged -> conflict
			makeAttr('a3', 'orig3'), // untouched -> auto-save
		];
		const pending = new Map([
			['a1', 'mine1'],
			['a2', 'mine2'],
			['a3', 'mine3'],
		]);

		const { conflicts, autoSaveAttrs } = categorizeConflicts(
			base,
			server,
			pending
		);
		expect(conflicts.map((c) => c.baseAttr.id)).toEqual(['a2']);
		expect(autoSaveAttrs.map((a) => a.id).sort()).toEqual(['a1', 'a3']);
	});

	it('supports matching by typeId via keyOf option', () => {
		const base = [makeAttr('a1', 'original', 'T1')];
		const server = [makeAttr('a1', 'their', 'T1')];
		const pending = new Map([['T1', 'mine']]);

		const { conflicts } = categorizeConflicts(base, server, pending, {
			keyOf: (a) => a.typeId,
		});
		expect(conflicts).toHaveLength(1);
	});
});
