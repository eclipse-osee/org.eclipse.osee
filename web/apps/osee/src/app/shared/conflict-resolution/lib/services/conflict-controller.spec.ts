/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 *
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
import { DestroyRef } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Subject, of } from 'rxjs';
import {
	ConflictController,
	conflictChangeNotification,
	conflictControllerConfig,
} from './conflict-controller';
import { ConflictResolutionService } from './conflict-resolution.service';

describe('ConflictController', () => {
	let resolveSpy: ReturnType<typeof vi.fn>;
	let serviceStub: Pick<ConflictResolutionService, 'resolve' | 'controller'>;
	let changes$: Subject<conflictChangeNotification>;
	let hasUnsavedChanges: boolean;
	let refresh: ReturnType<typeof vi.fn>;
	let clearLocalState: ReturnType<typeof vi.fn>;

	function build(): ConflictController {
		changes$ = new Subject();
		hasUnsavedChanges = false;
		refresh = vi.fn();
		clearLocalState = vi.fn();
		resolveSpy = vi.fn();

		const config: conflictControllerConfig = {
			changes: changes$,
			hasUnsavedChanges: () => hasUnsavedChanges,
			entityName: () => 'WF 1',
			entityId: () => '100',
			baseAttrs: () => [],
			fetchServerAttrs: () => of([]),
			pendingValues: () => new Map(),
			commit: () => of({ staleGammas: [] }),
			refresh: refresh as unknown as conflictControllerConfig['refresh'],
			clearLocalState:
				clearLocalState as unknown as conflictControllerConfig['clearLocalState'],
			onError: vi.fn() as unknown as conflictControllerConfig['onError'],
		};

		// Use the real controller with a stubbed service (spy on resolve). Construct inside an
		// injection context so takeUntilDestroyed gets a real DestroyRef.
		return TestBed.runInInjectionContext(() => {
			const destroyRef = TestBed.inject(DestroyRef);
			serviceStub = {
				resolve:
					resolveSpy as unknown as ConflictResolutionService['resolve'],
				controller: (c, d) =>
					new ConflictController(
						serviceStub as ConflictResolutionService,
						c,
						d
					),
			};
			return new ConflictController(
				serviceStub as ConflictResolutionService,
				config,
				destroyRef
			);
		});
	}

	beforeEach(() => {
		TestBed.configureTestingModule({});
	});

	function remote(
		changeTypes: string[],
		isLocal = false
	): conflictChangeNotification {
		return { changeTypes, isLocal };
	}

	it('starts not conflicted', () => {
		const controller = build();
		expect(controller.conflicted()).toBe(false);
	});

	it('flags a conflict on a remote attribute change while dirty', () => {
		const controller = build();
		hasUnsavedChanges = true;
		changes$.next(remote(['attribute_modified']));
		expect(controller.conflicted()).toBe(true);
	});

	it('does not flag when there are no unsaved changes', () => {
		const controller = build();
		hasUnsavedChanges = false;
		changes$.next(remote(['attribute_modified']));
		expect(controller.conflicted()).toBe(false);
	});

	it('does not flag on the tab own local change', () => {
		const controller = build();
		hasUnsavedChanges = true;
		changes$.next(remote(['attribute_modified'], true));
		expect(controller.conflicted()).toBe(false);
	});

	it('does not flag on a non-attribute change', () => {
		const controller = build();
		hasUnsavedChanges = true;
		changes$.next(remote(['relation_added']));
		expect(controller.conflicted()).toBe(false);
	});

	it('discard clears the conflict flag and refreshes', () => {
		const controller = build();
		hasUnsavedChanges = true;
		changes$.next(remote(['attribute_modified']));
		expect(controller.conflicted()).toBe(true);

		controller.discard();

		expect(clearLocalState).toHaveBeenCalled();
		expect(refresh).toHaveBeenCalled();
		expect(controller.conflicted()).toBe(false);
	});

	it('resolve delegates to the service and clears the flag via clearLocalState', () => {
		const controller = build();
		hasUnsavedChanges = true;
		changes$.next(remote(['attribute_modified']));

		controller.resolve();

		expect(resolveSpy).toHaveBeenCalledTimes(1);
		// The controller wires clearLocalState to also clear its conflict flag.
		const passedConfig = resolveSpy.mock.calls[0][0];
		passedConfig.clearLocalState();
		expect(clearLocalState).toHaveBeenCalled();
		expect(controller.conflicted()).toBe(false);
	});

	it('clearConflict clears the flag without discarding edits', () => {
		const controller = build();
		hasUnsavedChanges = true;
		changes$.next(remote(['attribute_modified']));

		controller.clearConflict();

		expect(controller.conflicted()).toBe(false);
		expect(clearLocalState).not.toHaveBeenCalled();
		expect(refresh).not.toHaveBeenCalled();
	});
});
