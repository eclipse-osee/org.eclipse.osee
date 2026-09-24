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
import { TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { Observable, Subject, of, throwError } from 'rxjs';
import { attribute } from '@osee/attributes/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import {
	attributeConflictResolutionDialogResult,
	resolvedConflict,
} from '../types/attribute-conflict.types';
import { resolutionOperations } from '../logic/map-resolutions-to-operations';
import { ConflictResolutionService } from './conflict-resolution.service';
import { conflictResolutionConfig } from './conflict-resolution.service';

type attr = attribute<string, ATTRIBUTETYPEID>;

function makeAttr(id: string, value: string): attr {
	return {
		id,
		typeId: '1000',
		gammaId: '7',
		value,
		name: 'Description',
		storeType: 'String',
		multiplicity: { id: '1', name: 'exactly one' },
	} as unknown as attr;
}

describe('ConflictResolutionService', () => {
	let service: ConflictResolutionService;
	let dialogOpen: ReturnType<typeof vi.fn>;
	let afterClosed: Observable<
		attributeConflictResolutionDialogResult | undefined
	>;

	// Config spies shared across a test.
	let commit: ReturnType<typeof vi.fn>;
	let refresh: ReturnType<typeof vi.fn>;
	let clearLocalState: ReturnType<typeof vi.fn>;
	let onError: ReturnType<typeof vi.fn>;

	beforeEach(() => {
		afterClosed = of(undefined);
		dialogOpen = vi.fn(() => ({ afterClosed: () => afterClosed }));
		commit = vi.fn((_ops: resolutionOperations) => of({ staleGammas: [] }));
		refresh = vi.fn();
		clearLocalState = vi.fn();
		onError = vi.fn();

		TestBed.configureTestingModule({
			providers: [
				ConflictResolutionService,
				{ provide: MatDialog, useValue: { open: dialogOpen } },
			],
		});
		service = TestBed.inject(ConflictResolutionService);
	});

	function baseConfig(
		overrides: Partial<conflictResolutionConfig> = {}
	): conflictResolutionConfig {
		return {
			entityName: 'WF 1',
			entityId: '100',
			baseAttrs: [makeAttr('a1', 'orig')],
			fetchServerAttrs: () => of([makeAttr('a1', 'orig')]),
			pendingValues: new Map([['a1', 'mine']]),
			commit: commit as unknown as conflictResolutionConfig['commit'],
			refresh: refresh as unknown as conflictResolutionConfig['refresh'],
			clearLocalState:
				clearLocalState as unknown as conflictResolutionConfig['clearLocalState'],
			onError: onError as unknown as conflictResolutionConfig['onError'],
			...overrides,
		};
	}

	it('auto-commits safe edits without opening the dialog when there are no true conflicts', () => {
		// server == base, so the pending edit is safe (auto-save), no conflict.
		service.resolve(baseConfig());

		expect(dialogOpen).not.toHaveBeenCalled();
		expect(clearLocalState).toHaveBeenCalled();
		const ops = commit.mock.calls[0][0] as resolutionOperations;
		expect(ops.set).toHaveLength(1);
		expect(ops.set[0].value).toBe('mine');
		expect(refresh).toHaveBeenCalled();
	});

	it('opens the dialog on a true conflict and commits the mapped resolution', () => {
		const conflictResult: attributeConflictResolutionDialogResult = {
			resolutions: [
				{
					conflict: {
						baseAttr: makeAttr('a1', 'orig'),
						localValue: 'mine',
						serverAttr: makeAttr('a1', 'theirs'),
						serverDeleted: false,
						allowsMultiple: false,
					},
					action: 'take-yours',
					resolvedValues: ['mine'],
				} as resolvedConflict,
			],
		};
		afterClosed = of(conflictResult);

		service.resolve(
			baseConfig({
				// server diverged from base -> true conflict
				fetchServerAttrs: () => of([makeAttr('a1', 'theirs')]),
			})
		);

		expect(dialogOpen).toHaveBeenCalled();
		const ops = commit.mock.calls[0][0] as resolutionOperations;
		expect(ops.set[0].value).toBe('mine');
		expect(refresh).toHaveBeenCalled();
	});

	it('leaves state untouched when the conflict dialog is cancelled', () => {
		afterClosed = of(undefined); // cancelled

		service.resolve(
			baseConfig({
				fetchServerAttrs: () => of([makeAttr('a1', 'theirs')]),
			})
		);

		expect(dialogOpen).toHaveBeenCalled();
		expect(commit).not.toHaveBeenCalled();
		expect(clearLocalState).not.toHaveBeenCalled();
		expect(refresh).not.toHaveBeenCalled();
	});

	it('refreshes (without committing) when resolution yields no operations', () => {
		const takeTheirsResult: attributeConflictResolutionDialogResult = {
			resolutions: [
				{
					conflict: {
						baseAttr: makeAttr('a1', 'orig'),
						localValue: 'mine',
						serverAttr: makeAttr('a1', 'theirs'),
						serverDeleted: false,
						allowsMultiple: false,
					},
					action: 'take-theirs',
					resolvedValues: [],
				} as resolvedConflict,
			],
		};
		afterClosed = of(takeTheirsResult);

		service.resolve(
			baseConfig({
				pendingValues: new Map([['a1', 'mine']]),
				fetchServerAttrs: () => of([makeAttr('a1', 'theirs')]),
			})
		);

		expect(clearLocalState).toHaveBeenCalled();
		expect(commit).not.toHaveBeenCalled(); // nothing to persist (take-theirs)
		expect(refresh).toHaveBeenCalled();
	});

	it('still refreshes when the commit fails, and reports the error', () => {
		commit = vi.fn(() => throwError(() => new Error('boom')));

		service.resolve(
			// no-conflict path auto-commits; the commit observable errors
			baseConfig({
				commit: commit as unknown as conflictResolutionConfig['commit'],
			})
		);

		expect(commit).toHaveBeenCalled();
		expect(onError).toHaveBeenCalledWith(
			expect.stringContaining('Conflict resolution failed')
		);
		expect(refresh).toHaveBeenCalled(); // refresh anyway to show authoritative state
	});

	it('re-resolves (without clearing local state) when the commit reports stale gammas', () => {
		// A true optimistic-concurrency rejection: the server moved the attr between the
		// dialog opening and the apply. The write did NOT take -> re-run the flow and keep
		// the user's pending edits (do not clear local state, do not report success).
		commit = vi.fn(() => of({ staleGammas: ['9'] }));
		const conflictResult: attributeConflictResolutionDialogResult = {
			resolutions: [
				{
					conflict: {
						baseAttr: makeAttr('a1', 'orig'),
						localValue: 'mine',
						serverAttr: makeAttr('a1', 'theirs'),
						serverDeleted: false,
						allowsMultiple: false,
					},
					action: 'take-yours',
					resolvedValues: ['mine'],
				} as resolvedConflict,
			],
		};
		// Return the resolution the first time the dialog closes, then cancel on the
		// re-resolve so the flow terminates (otherwise the always-stale commit loops).
		let closeCount = 0;
		afterClosed = new Observable((subscriber) => {
			closeCount += 1;
			subscriber.next(closeCount === 1 ? conflictResult : undefined);
			subscriber.complete();
		});
		const resolveSpy = vi.spyOn(service, 'resolve');

		const config = baseConfig({
			commit: commit as unknown as conflictResolutionConfig['commit'],
			fetchServerAttrs: () => of([makeAttr('a1', 'theirs')]),
		});
		service.resolve(config);

		expect(commit).toHaveBeenCalledTimes(1);
		expect(onError).toHaveBeenCalledWith(
			expect.stringContaining('Someone else changed this')
		);
		// Local state preserved; the flow re-runs against fresh server state.
		expect(clearLocalState).not.toHaveBeenCalled();
		// resolve() called again: initial call + the re-resolve triggered by stale gammas.
		expect(resolveSpy).toHaveBeenCalledTimes(2);
	});

	it('commits the auto-save edit with the freshest gamma after a live re-categorize', () => {
		// Scenario: one true conflict (a1) plus one safe auto-save edit (a2). While the
		// dialog is open, another remote change lands. The auto-save attr (a2) must be
		// committed with the gamma from the LATEST fetch, not the open-time snapshot, so
		// the write is not spuriously rejected by the gamma guard.
		const gammaAttr = (id: string, value: string, gamma: string): attr =>
			({ ...makeAttr(id, value), gammaId: gamma }) as attr;

		let fetchCount = 0;
		const fetchServerAttrs = () => {
			fetchCount += 1;
			// a1 always conflicts (server 'theirs' vs base 'orig', pending 'mine').
			// a2 is untouched by server (equals base) -> auto-save; its gamma advances
			// from '10' to '20' between the first fetch and the live re-fetch.
			const a2Gamma = fetchCount === 1 ? '10' : '20';
			return of([
				gammaAttr('a1', 'theirs', '7'),
				gammaAttr('a2', 'base2', a2Gamma),
			]);
		};

		const changes$ = new Subject<{
			changeTypes: string[];
			isLocal: boolean;
		}>();

		const conflictResult: attributeConflictResolutionDialogResult = {
			resolutions: [
				{
					conflict: {
						baseAttr: gammaAttr('a1', 'orig', '7'),
						localValue: 'mine',
						serverAttr: gammaAttr('a1', 'theirs', '7'),
						serverDeleted: false,
						allowsMultiple: false,
					},
					action: 'take-theirs', // discard local for a1 -> no set op from conflict
					resolvedValues: [],
				} as resolvedConflict,
			],
		};

		// Emit the live change BEFORE the dialog closes so the service re-fetches and
		// refreshes its snapshot, then close with the resolution.
		afterClosed = new Observable((subscriber) => {
			changes$.next({
				changeTypes: ['attribute_modified'],
				isLocal: false,
			});
			subscriber.next(conflictResult);
			subscriber.complete();
		});

		const config = baseConfig({
			baseAttrs: [
				gammaAttr('a1', 'orig', '7'),
				gammaAttr('a2', 'base2', '10'),
			],
			pendingValues: new Map([
				['a1', 'mine'],
				['a2', 'mine2'],
			]),
			fetchServerAttrs,
			changes: changes$.asObservable(),
			commit: commit as unknown as conflictResolutionConfig['commit'],
		});
		service.resolve(config);

		const ops = commit.mock.calls[0][0] as resolutionOperations;
		// Only the auto-save edit (a2) is in the set (a1 was take-theirs).
		expect(ops.set).toHaveLength(1);
		expect(ops.set[0].id).toBe('a2');
		expect(ops.set[0].value).toBe('mine2');
		// The gamma must be the refreshed one ('20'), not the open-time '10'.
		expect(ops.set[0].gammaId).toBe('20');
	});

	it('reports an error when fetching server state fails', () => {
		service.resolve(
			baseConfig({
				fetchServerAttrs: () =>
					throwError(() => new Error('network down')),
			})
		);

		expect(onError).toHaveBeenCalledWith(
			expect.stringContaining('Failed to load latest version')
		);
		expect(commit).not.toHaveBeenCalled();
	});

	it('toggles setResolving around the fetch', () => {
		const setResolving = vi.fn();
		service.resolve(baseConfig({ setResolving }));
		expect(setResolving).toHaveBeenCalledWith(true);
		expect(setResolving).toHaveBeenCalledWith(false);
	});
});
