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
import { MatSnackBar } from '@angular/material/snack-bar';
import { BehaviorSubject, Observable, Subject, of } from 'rxjs';
import { branchChangeEvent } from '@osee/shared/services/network';
import { BranchChangeEventService } from '../event/branch-change-event.service';
import { CurrentBranchInfoService } from '../../httpui/current-branch-info.service';
import { UiService } from '../ui.service';
import { BranchRoutedUIService } from './branch-routed-ui.service';
import { SelectedBranchLifecycleService } from './selected-branch-lifecycle.service';

function event(overrides: Partial<branchChangeEvent>): branchChangeEvent {
	return {
		branchId: '570',
		changeType: 'renamed',
		...overrides,
	} as branchChangeEvent;
}

describe('SelectedBranchLifecycleService', () => {
	let service: SelectedBranchLifecycleService;
	let branchEvents$: Subject<branchChangeEvent>;
	let branchIdSetTo: string[];
	let snackOpen: ReturnType<typeof vi.fn>;

	beforeEach(() => {
		branchEvents$ = new Subject<branchChangeEvent>();
		branchIdSetTo = [];
		snackOpen = vi.fn();

		const branchRouterStub = {
			set branchId(value: string) {
				branchIdSetTo.push(value);
			},
		};

		TestBed.configureTestingModule({
			providers: [
				SelectedBranchLifecycleService,
				{
					provide: UiService,
					useValue: {
						// Selected branch is '570' for the whole test.
						id: new BehaviorSubject<string>('570'),
					},
				},
				{
					provide: BranchChangeEventService,
					useValue: {
						forBranch: (
							_branchId: string
						): Observable<branchChangeEvent> =>
							branchEvents$.asObservable(),
					},
				},
				{
					provide: BranchRoutedUIService,
					useValue: branchRouterStub,
				},
				{
					provide: CurrentBranchInfoService,
					useValue: {
						currentBranch: of({ name: 'My Working Branch' }),
					},
				},
				{ provide: MatSnackBar, useValue: { open: snackOpen } },
			],
		});
		service = TestBed.inject(SelectedBranchLifecycleService);
		service.initialize();
	});

	it('clears the branch selection (not COMMON) when the selected branch is deleted', () => {
		branchEvents$.next(event({ branchId: '570', changeType: 'deleted' }));

		// Cleared: branchId set to '' — NOT the COMMON id.
		expect(branchIdSetTo).toEqual(['']);
		expect(snackOpen).toHaveBeenCalledTimes(1);
		expect(snackOpen.mock.calls[0][0]).toContain('deleted');
		expect(snackOpen.mock.calls[0][0]).toContain('My Working Branch');
	});

	it('clears the branch selection when the selected branch is purged', () => {
		branchEvents$.next(event({ branchId: '570', changeType: 'purged' }));

		expect(branchIdSetTo).toEqual(['']);
		expect(snackOpen.mock.calls[0][0]).toContain('purged');
	});

	it('follows a rebaselined branch to its successor', () => {
		branchEvents$.next(
			event({
				branchId: '570',
				changeType: 'rebaselined',
				newBranchId: '571',
			})
		);

		expect(branchIdSetTo).toEqual(['571']);
	});

	it('ignores unrelated change types on the selected branch', () => {
		branchEvents$.next(event({ branchId: '570', changeType: 'renamed' }));

		expect(branchIdSetTo).toEqual([]);
		expect(snackOpen).not.toHaveBeenCalled();
	});
});
