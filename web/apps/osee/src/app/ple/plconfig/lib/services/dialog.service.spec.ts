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
import { TestBed } from '@angular/core/testing';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { of } from 'rxjs';
import { TestScheduler } from 'rxjs/testing';
import { plCurrentBranchServiceMock } from '../testing/mockPlCurrentBranchService.mock';
import { XResultData } from '@osee/shared/types';
import { CfgGroupDialog } from '../types/pl-config-cfggroups';
import { PLEditConfigData } from '../types/pl-edit-config-data';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { CurrentBranchInfoService } from '@osee/shared/services';
import { MockXResultData, testBranchInfo } from '@osee/shared/testing';
import { modifyFeature, PLEditFeatureData } from '../types/pl-config-features';
import { DialogService } from './dialog.service';
import { PlConfigCurrentBranchService } from './pl-config-current-branch.service';

describe('DialogService', () => {
	let service: DialogService;
	let scheduler: TestScheduler;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [MatDialogModule],
			providers: [
				provideNoopAnimations(),
				{
					provide: PlConfigCurrentBranchService,
					useValue: plCurrentBranchServiceMock,
				},
				{
					provide: CurrentBranchInfoService,
					useValue: { currentBranch: of(testBranchInfo) },
				},
			],
		});
		service = TestBed.inject(DialogService);
	});

	beforeEach(
		() =>
			(scheduler = new TestScheduler((actual, expected) => {
				expect(actual).toEqual(expected);
			}))
	);

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should open the config menu in config mode', () => {
		scheduler.run(({ expectObservable }) => {
			const dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of<PLEditConfigData>({
					editable: true,
					currentBranch: '',
					currentConfig: {
						id: '-1',
						name: '',
						description: '',
						hasFeatureApplicabilities: false,
						groups: [],
					},
					copyFrom: {
						id: '-1',
						name: '',
						description: '',
						hasFeatureApplicabilities: false,
					},
					group: [{ id: '-1', name: '', description: '' }],
					productApplicabilities: [],
				}),
				close: null,
			});
			const _dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			const expectedValues: { a: XResultData } = { a: MockXResultData };
			expectObservable(
				service.openEditConfigDialog('Product D', true)
			).toBe('(a|)', expectedValues);
		});
	});
	it('should open the config menu in group mode mode', () => {
		scheduler.run(({ expectObservable }) => {
			const dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of<CfgGroupDialog>({
					editable: true,
					configGroup: {
						name: 'abGroup',
						id: '123',
						description: '',
						views: [
							{
								id: '123',
								name: 'Product A',
								description: '',
								hasFeatureApplicabilities: false,
							},
						],
						configurations: [],
					},
				}),
				close: null,
			});
			const _dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			const expectedValues: { a: XResultData } = { a: MockXResultData };
			expectObservable(
				service.openEditConfigGroupDialog('abGroup', true)
			).toBe('(a|)', expectedValues);
		});
	});

	it('should open the feature menu', () => {
		scheduler.run(({ expectObservable }) => {
			const dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of<PLEditFeatureData>({
					editable: true,
					feature: new modifyFeature(),
					currentBranch: '',
				}),
				close: null,
			});
			const _dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			const expectedValues: { a: XResultData } = { a: MockXResultData };
			expectObservable(service.displayFeatureDialog('')).toBe(
				'(a|)',
				expectedValues
			);
		});
	});
});
