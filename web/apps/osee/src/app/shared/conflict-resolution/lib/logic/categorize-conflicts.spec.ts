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

	it('reports a converged edit (both sides set the same value) as neither conflict nor auto-save', () => {
		// Base was 'original'; the user edited to 'analysis' and another user also set
		// 'analysis'. There is nothing to save and nothing to decide, but it must be
		// surfaced (convergedAttrs) so the UI can account for the still-flagged field.
		const base = [makeAttr('a1', 'original')];
		const server = [makeAttr('a1', 'analysis')];
		const pending = new Map([['a1', 'analysis']]);

		const { conflicts, autoSaveAttrs, convergedAttrs } =
			categorizeConflicts(base, server, pending);
		expect(conflicts).toEqual([]);
		expect(autoSaveAttrs).toEqual([]);
		expect(convergedAttrs).toHaveLength(1);
		expect(convergedAttrs[0].id).toBe('a1');
		expect(convergedAttrs[0].value).toBe('analysis');
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

	describe('staged additions', () => {
		/** A new (unpersisted) instance the user staged while conflicted. */
		function makeStagedAttr(value: string, typeId = 'T1'): attr {
			return {
				id: '-1',
				gammaId: '-1',
				typeId,
				value,
				name: 'Description',
				storeType: 'String',
				multiplicity: { id: '1', name: 'any' },
			} as unknown as attr;
		}

		it('applies a staged add as-is when the server did not add the same type', () => {
			const base = [makeAttr('a1', 'orig', 'T1')];
			const server = [makeAttr('a1', 'orig', 'T1')]; // no new server instance of T2
			const staged = [
				{ key: 'TEMP-1', attr: makeStagedAttr('new', 'T2') },
			];

			const { conflicts, autoSaveAttrs, stagedAddAttrs } =
				categorizeConflicts(base, server, new Map(), undefined, staged);

			expect(conflicts).toEqual([]);
			expect(autoSaveAttrs).toEqual([]);
			expect(stagedAddAttrs).toHaveLength(1);
			expect(stagedAddAttrs[0].typeId).toBe('T2');
			expect(stagedAddAttrs[0].value).toBe('new');
			// Keeps the add sentinel so it commits as an add op.
			expect(stagedAddAttrs[0].id).toBe('-1');
		});

		it('flags a conflict when the server also added an instance of the staged type', () => {
			const base: attr[] = []; // no T1 instance existed at base
			const server = [makeAttr('s1', 'their new', 'T1')]; // server added T1
			const staged = [
				{ key: 'TEMP-1', attr: makeStagedAttr('my new', 'T1') },
			];

			const { conflicts, stagedAddAttrs } = categorizeConflicts(
				base,
				server,
				new Map(),
				undefined,
				staged
			);

			expect(stagedAddAttrs).toEqual([]);
			expect(conflicts).toHaveLength(1);
			expect(conflicts[0].stagedAdd).toBe(true);
			expect(conflicts[0].conflictKey).toBe('TEMP-1');
			expect(conflicts[0].localValue).toBe('my new');
			expect(conflicts[0].serverAttr?.value).toBe('their new');
			expect(conflicts[0].serverDeleted).toBe(false);
		});

		it('pairs each staged add of a type with a distinct server add, staging any surplus', () => {
			const base: attr[] = [];
			// Server added two instances of T1.
			const server = [
				makeAttr('s1', 'srv1', 'T1'),
				makeAttr('s2', 'srv2', 'T1'),
			];
			// User staged three T1 adds: first two collide, the third is safe.
			const staged = [
				{ key: 'TEMP-1', attr: makeStagedAttr('mine1', 'T1') },
				{ key: 'TEMP-2', attr: makeStagedAttr('mine2', 'T1') },
				{ key: 'TEMP-3', attr: makeStagedAttr('mine3', 'T1') },
			];

			const { conflicts, stagedAddAttrs } = categorizeConflicts(
				base,
				server,
				new Map(),
				undefined,
				staged
			);

			expect(conflicts.map((c) => c.conflictKey)).toEqual([
				'TEMP-1',
				'TEMP-2',
			]);
			expect(conflicts.map((c) => c.serverAttr?.value)).toEqual([
				'srv1',
				'srv2',
			]);
			expect(stagedAddAttrs).toHaveLength(1);
			expect(stagedAddAttrs[0].value).toBe('mine3');
		});

		it('pairs a staged add with the actually-added server instance, not a pre-existing one of the same type', () => {
			// A T1 instance ('e1') already existed at base and remains on the server;
			// the server ALSO added a second T1 ('s2'). The staged add must pair with
			// the genuinely new instance ('s2'), regardless of server ordering -- here
			// the pre-existing instance is returned AFTER the added one to prove the
			// pairing is by id-set difference, not position.
			const base = [makeAttr('e1', 'existing', 'T1')];
			const server = [
				makeAttr('s2', 'their added', 'T1'), // new (not in base) -- listed first
				makeAttr('e1', 'existing', 'T1'), // pre-existing -- listed second
			];
			const staged = [
				{ key: 'TEMP-1', attr: makeStagedAttr('mine', 'T1') },
			];

			const { conflicts, stagedAddAttrs } = categorizeConflicts(
				base,
				server,
				new Map(),
				undefined,
				staged
			);

			expect(stagedAddAttrs).toEqual([]);
			expect(conflicts).toHaveLength(1);
			// Must be the added instance's value, never the pre-existing one's.
			expect(conflicts[0].serverAttr?.id).toBe('s2');
			expect(conflicts[0].serverAttr?.value).toBe('their added');
		});

		it('does not collide when the server instance of the type predates the conflict (present at base)', () => {
			// A T1 instance already existed at base and still exists on the server:
			// that is NOT a server-side add, so a staged T1 add is safe.
			const base = [makeAttr('a1', 'orig', 'T1')];
			const server = [makeAttr('a1', 'orig', 'T1')];
			const staged = [
				{ key: 'TEMP-1', attr: makeStagedAttr('mine', 'T1') },
			];

			const { conflicts, stagedAddAttrs } = categorizeConflicts(
				base,
				server,
				new Map(),
				undefined,
				staged
			);

			expect(conflicts).toEqual([]);
			expect(stagedAddAttrs).toHaveLength(1);
			expect(stagedAddAttrs[0].value).toBe('mine');
		});

		it('flags a single-multiplicity staged-add collision without marking it multi-instance', () => {
			// Original bug scenario: a single-instance type that had no instance at base.
			// U2 added one; U1 staged one. It is a collision, but allowsMultiple is false
			// so the dialog will not offer "take both" (you cannot keep two).
			const single = (value: string, id = '-1'): attr =>
				({
					id,
					gammaId: id === '-1' ? '-1' : '9',
					typeId: 'SINGLE',
					value,
					name: 'CUI',
					storeType: 'String',
					multiplicity: { id: '2', name: 'exactly one' },
				}) as unknown as attr;

			const base: attr[] = [];
			const server = [single('theirs', 's1')];
			const staged = [{ key: 'TEMP-1', attr: single('mine') }];

			const { conflicts } = categorizeConflicts(
				base,
				server,
				new Map(),
				undefined,
				staged
			);

			expect(conflicts).toHaveLength(1);
			expect(conflicts[0].stagedAdd).toBe(true);
			expect(conflicts[0].allowsMultiple).toBe(false);
		});

		it('does not double-count a staged add that also has a pending value under its key', () => {
			// The panel records a staged add both in the staged store AND as a pending
			// value (keyed by the client temp key) so an in-place edit is captured. It
			// also merges the staged instance into the base set it passes here. Without
			// the staged-key guard, the pending-values loop would see the temp key,
			// find it in base, miss it on the server, and emit a bogus "server deleted"
			// conflict alongside the correct staged add. It must appear exactly once.
			const stagedInstance = makeStagedAttr('added', 'T2');
			const base = [makeAttr('a1', 'orig', 'T1'), stagedInstance];
			const server = [makeAttr('a1', 'orig', 'T1')];
			const pending = new Map([['TEMP-1', 'added']]);
			const staged = [{ key: 'TEMP-1', attr: stagedInstance }];

			const { conflicts, autoSaveAttrs, stagedAddAttrs } =
				categorizeConflicts(base, server, pending, undefined, staged);

			expect(conflicts).toEqual([]);
			expect(autoSaveAttrs).toEqual([]);
			expect(stagedAddAttrs).toHaveLength(1);
			expect(stagedAddAttrs[0].value).toBe('added');
		});

		it('categorizes value edits and staged adds together', () => {
			const base = [makeAttr('a1', 'orig', 'T1')];
			const server = [makeAttr('a1', 'orig', 'T1')]; // edit safe; no T2 server add
			const pending = new Map([['a1', 'edited']]);
			const staged = [
				{ key: 'TEMP-1', attr: makeStagedAttr('added', 'T2') },
			];

			const { conflicts, autoSaveAttrs, stagedAddAttrs } =
				categorizeConflicts(base, server, pending, undefined, staged);

			expect(conflicts).toEqual([]);
			expect(autoSaveAttrs.map((a) => a.value)).toEqual(['edited']);
			expect(stagedAddAttrs.map((a) => a.value)).toEqual(['added']);
		});
	});
});
