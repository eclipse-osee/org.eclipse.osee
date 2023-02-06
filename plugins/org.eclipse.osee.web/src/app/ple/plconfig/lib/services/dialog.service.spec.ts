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
import { MockXResultData } from '../../../../testing/XResultData.response.mock';
import { response } from '../../../../types/responses';
import { CfgGroupDialog } from '../types/pl-config-cfggroups';
import { PLEditConfigData } from '../types/pl-edit-config-data';

import { DialogService } from './dialog.service';
import { PlConfigCurrentBranchService } from './pl-config-current-branch.service';
import { modifyFeature, PLEditFeatureData } from '../types/pl-config-features';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('DialogService', () => {
	let service: DialogService;
	let scheduler: TestScheduler;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [MatDialogModule, NoopAnimationsModule],
			providers: [
				{
					provide: PlConfigCurrentBranchService,
					useValue: plCurrentBranchServiceMock,
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
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of<PLEditConfigData>({
					editable: true,
					currentBranch: '',
					currentConfig: {
						id: '',
						name: '',
						description: '',
						hasFeatureApplicabilities: false,
						groups: [],
					},
					copyFrom: {
						id: '',
						name: '',
						description: '',
						hasFeatureApplicabilities: false,
					},
					group: [{ id: '', name: '', description: '' }],
					productApplicabilities: [],
				}),
				close: null,
			});
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			const expectedValues: { a: response } = { a: MockXResultData };
			expectObservable(service.openConfigMenu('Product D', 'true')).toBe(
				'(a|)',
				expectedValues
			);
		});
	});
	it('should open the config menu in group mode mode', () => {
		scheduler.run(({ expectObservable }) => {
			let dialogRefSpy = jasmine.createSpyObj({
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
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			const expectedValues: { a: response } = { a: MockXResultData };
			expectObservable(service.openConfigMenu('abGroup', 'true')).toBe(
				'(a|)',
				expectedValues
			);
		});
	});

	it('should open the feature menu', () => {
		scheduler.run(({ expectObservable }) => {
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of<PLEditFeatureData>({
					editable: true,
					feature: new modifyFeature(),
					currentBranch: '',
				}),
				close: null,
			});
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			const expectedValues: { a: response } = { a: MockXResultData };
			expectObservable(
				service.displayFeatureMenu({
					id: '',
					name: '',
					type: null,
					description: '',
					defaultValue: '',
					values: [],
					valueType: '',
					configurations: [],
					productApplicabilities: [],
					multiValued: false,
					setProductAppStr() {},
					setValueStr() {},
				})
			).toBe('(a|)', expectedValues);
		});
	});
});
