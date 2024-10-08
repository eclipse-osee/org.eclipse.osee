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
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { SimpleChange } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatMenuHarness } from '@angular/material/menu/testing';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { TestScheduler } from 'rxjs/testing';
import { AddSubMessageDialogComponent } from '../../dialogs/add-sub-message-dialog/add-sub-message-dialog.component';

import { CurrentMessagesService } from '@osee/messaging/shared/services';
import {
	CurrentMessageServiceMock,
	messagesMock,
	subMessagesMock,
} from '@osee/messaging/shared/testing';
import { subMessage } from '@osee/messaging/shared/types';
import { MockSingleDiffComponent } from '@osee/shared/testing';
import { HighlightFilteredTextDirective } from '@osee/shared/utils';
import { MockEditMessageFieldComponent } from '../../testing/edit-sub-message-field.component.mock';
import { SubMessageTableComponent } from './sub-message-table.component';
import { applicabilitySentinel } from '@osee/applicability/types';
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';

describe('SubMessageTableComponent', () => {
	let component: SubMessageTableComponent;
	let fixture: ComponentFixture<SubMessageTableComponent>;
	let loader: HarnessLoader;
	let scheduler: TestScheduler;
	const expectedData: subMessage[] = [
		{
			id: '1' as `${number}`,
			gammaId: '-1' as `${number}`,
			applicability: applicabilitySentinel,
			name: {
				id: '-1',
				typeId: '1152921504606847088',
				gammaId: '-1',
				value: 'Name',
			},
			description: {
				id: '-1',
				typeId: '1152921504606847090',
				gammaId: '-1',
				value: 'description adslkfj;asjfadkljf;lajdfla;jsdfdlkasjf;lkajslfjad;ljfkladjsf;',
			},
			interfaceSubMessageNumber: {
				id: '-1',
				typeId: '2455059983007225769',
				gammaId: '-1',
				value: '0',
			},
		},
		{
			id: '2' as `${number}`,
			gammaId: '-1' as `${number}`,
			applicability: applicabilitySentinel,
			name: {
				id: '-1',
				typeId: '1152921504606847088',
				gammaId: '-1',
				value: 'Name2',
			},
			description: {
				id: '-1',
				typeId: '1152921504606847090',
				gammaId: '-1',
				value: 'description2',
			},
			interfaceSubMessageNumber: {
				id: '-1',
				typeId: '2455059983007225769',
				gammaId: '-1',
				value: '1',
			},
		},
	];

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatTableModule,
				MatIconModule,
				MatTooltipModule,
				MatButtonModule,
				RouterTestingModule.withRoutes([
					{ path: '', component: SubMessageTableComponent },
					{
						path: 'diffOpen',
						component: MockSingleDiffComponent,
						outlet: 'rightSideNav',
					},
				]),
				MatMenuModule,
				MatDialogModule,
				NoopAnimationsModule,
				HighlightFilteredTextDirective,
				AddSubMessageDialogComponent,
				MockEditMessageFieldComponent,
				SubMessageTableComponent,
			],
			providers: [
				{
					provide: CurrentMessagesService,
					useValue: CurrentMessageServiceMock,
				},
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(SubMessageTableComponent);
		component = fixture.componentInstance;
		component.element = {
			id: '5',
			gammaId: '-1',
			name: {
				id: '-1',
				typeId: '1152921504606847088',
				gammaId: '-1',
				value: 'blah',
			},
			description: {
				id: '-1',
				typeId: '1152921504606847090',
				gammaId: '-1',
				value: 'abcdef',
			},
			interfaceMessageNumber: {
				id: '-1',
				typeId: '2455059983007225768',
				gammaId: '-1',
				value: '1234',
			},
			interfaceMessagePeriodicity: {
				id: '-1',
				typeId: '3899709087455064789',
				gammaId: '-1',
				value: 'Aperiodic',
			},
			interfaceMessageRate: {
				id: '-1',
				typeId: '2455059983007225763',
				gammaId: '-1',
				value: '5Hz',
			},
			interfaceMessageType: {
				id: '-1',
				typeId: '2455059983007225770',
				gammaId: '-1',
				value: 'Connection',
			},
			interfaceMessageWriteAccess: {
				id: '-1',
				typeId: '2455059983007225754',
				gammaId: '-1',
				value: true,
			},
			subMessages: [],
			interfaceMessageExclude: {
				id: '-1',
				typeId: '2455059983007225811',
				gammaId: '-1',
				value: false,
			},
			interfaceMessageIoMode: {
				id: '-1',
				typeId: '2455059983007225813',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageModeCode: {
				id: '-1',
				typeId: '2455059983007225810',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageRateVer: {
				id: '-1',
				typeId: '2455059983007225805',
				gammaId: '-1',
				value: '',
			},
			interfaceMessagePriority: {
				id: '-1',
				typeId: '2455059983007225806',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageProtocol: {
				id: '-1',
				typeId: '2455059983007225809',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageRptWordCount: {
				id: '-1',
				typeId: '2455059983007225807',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageRptCmdWord: {
				id: '-1',
				typeId: '2455059983007225808',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageRunBeforeProc: {
				id: '-1',
				typeId: '2455059983007225812',
				gammaId: '-1',
				value: false,
			},
			interfaceMessageVer: {
				id: '-1',
				typeId: '2455059983007225804',
				gammaId: '-1',
				value: '',
			},
			publisherNodes: [
				{
					id: '100',
					gammaId: '-1',
					name: {
						id: '-1',
						typeId: '1152921504606847088',
						gammaId: '-1',
						value: 'Node1',
					},
					description: {
						id: '-1',
						typeId: '1152921504606847090',
						gammaId: '-1',
						value: '',
					},
					applicability: applicabilitySentinel,
					interfaceNodeNumber: {
						id: '-1',
						typeId: '5726596359647826657',
						gammaId: '-1',
						value: '',
					},
					interfaceNodeGroupId: {
						id: '-1',
						typeId: '5726596359647826658',
						gammaId: '-1',
						value: '',
					},
					interfaceNodeBackgroundColor: {
						id: '-1',
						typeId: '5221290120300474048',
						gammaId: '-1',
						value: '',
					},
					interfaceNodeAddress: {
						id: '-1',
						typeId: '5726596359647826656',
						gammaId: '-1',
						value: '',
					},
					interfaceNodeBuildCodeGen: {
						id: '-1',
						typeId: '5806420174793066197',
						gammaId: '-1',
						value: false,
					},
					interfaceNodeCodeGen: {
						id: '-1',
						typeId: '4980834335211418740',
						gammaId: '-1',
						value: false,
					},
					interfaceNodeCodeGenName: {
						id: '-1',
						typeId: '5390401355909179776',
						gammaId: '-1',
						value: '',
					},
					nameAbbrev: {
						id: '-1',
						typeId: '8355308043647703563',
						gammaId: '-1',
						value: '',
					},
					interfaceNodeToolUse: {
						id: '-1',
						typeId: '5863226088234748106',
						gammaId: '-1',
						value: false,
					},
					interfaceNodeType: {
						id: '-1',
						typeId: '6981431177168910500',
						gammaId: '-1',
						value: '',
					},
					notes: {
						id: '-1',
						typeId: '1152921504606847085',
						gammaId: '-1',
						value: '',
					},
				},
			],
			subscriberNodes: [
				{
					id: '101',
					gammaId: '-1',
					name: {
						id: '-1',
						typeId: '1152921504606847088',
						gammaId: '-1',
						value: 'Node2',
					},
					description: {
						id: '-1',
						typeId: '1152921504606847090',
						gammaId: '-1',
						value: '',
					},
					applicability: applicabilitySentinel,
					interfaceNodeNumber: {
						id: '-1',
						typeId: '5726596359647826657',
						gammaId: '-1',
						value: '',
					},
					interfaceNodeGroupId: {
						id: '-1',
						typeId: '5726596359647826658',
						gammaId: '-1',
						value: '',
					},
					interfaceNodeBackgroundColor: {
						id: '-1',
						typeId: '5221290120300474048',
						gammaId: '-1',
						value: '',
					},
					interfaceNodeAddress: {
						id: '-1',
						typeId: '5726596359647826656',
						gammaId: '-1',
						value: '',
					},
					interfaceNodeBuildCodeGen: {
						id: '-1',
						typeId: '5806420174793066197',
						gammaId: '-1',
						value: false,
					},
					interfaceNodeCodeGen: {
						id: '-1',
						typeId: '4980834335211418740',
						gammaId: '-1',
						value: false,
					},
					interfaceNodeCodeGenName: {
						id: '-1',
						typeId: '5390401355909179776',
						gammaId: '-1',
						value: '',
					},
					nameAbbrev: {
						id: '-1',
						typeId: '8355308043647703563',
						gammaId: '-1',
						value: '',
					},
					interfaceNodeToolUse: {
						id: '-1',
						typeId: '5863226088234748106',
						gammaId: '-1',
						value: false,
					},
					interfaceNodeType: {
						id: '-1',
						typeId: '6981431177168910500',
						gammaId: '-1',
						value: '',
					},
					notes: {
						id: '-1',
						typeId: '1152921504606847085',
						gammaId: '-1',
						value: '',
					},
				},
			],
			applicability: applicabilitySentinel,
		};
		component.dataSource = new MatTableDataSource();
		component.data = expectedData;
		component.editMode = true;
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	beforeEach(
		() =>
			(scheduler = new TestScheduler((actual, expected) => {
				expect(actual).toEqual(expected);
			}))
	);

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should update the datasource filter', () => {
		scheduler.run(({ expectObservable }) => {
			component.filter = 'sub message: Name2';
			component.ngOnChanges({
				data: new SimpleChange(component.data, component.data, false),
				filter: new SimpleChange('', component.filter, false),
			});
			const expectedMarble = { a: component.element };
			const expectedObservable = '';
			expectObservable(component.expandRow).toBe(
				expectedObservable,
				expectedMarble
			);
		});
	});

	//same as in structure-pages, menu tests have some difficulty since dialogRefSpy doesn't work in standalone context
	xdescribe('Menu Tests', () => {
		let mEvent: MouseEvent;
		beforeEach(() => {
			mEvent = document.createEvent('MouseEvent');
		});

		it('should open the menu and open the view diff sidenav', async () => {
			component.openMenu(
				mEvent,
				messagesMock[0],
				subMessagesMock[0],
				'string',
				'field',
				'name'
			);
			await fixture.whenStable();
			const menu = await loader.getHarness(MatMenuHarness);
			const spy = spyOn(component, 'viewDiff').and.callThrough();
			await menu.clickItem({ text: new RegExp('View Diff') });
			expect(spy).toHaveBeenCalled();
		});

		it('should open the menu and dismiss a description', async () => {
			component.openMenu(
				mEvent,
				messagesMock[0],
				subMessagesMock[0],
				'string',
				'',
				' '
			);
			await fixture.whenStable();
			const menu = await loader.getHarness(MatMenuHarness);
			const spy = spyOn(
				component,
				'openDescriptionDialog'
			).and.callThrough();
			const dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of('ok'),
				close: null,
			});
			const _dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			const serviceSpy = spyOn(
				TestBed.inject(CurrentMessagesService),
				'partialUpdateSubMessage'
			).and.callThrough();
			await menu.clickItem({ text: new RegExp('Open Description') });
			expect(spy).toHaveBeenCalled();
			expect(serviceSpy).toHaveBeenCalled();
		});

		it('should open the menu and edit a description', async () => {
			component.openMenu(
				mEvent,
				messagesMock[0],
				subMessagesMock[0],
				'string',
				'',
				' '
			);
			await fixture.whenStable();
			const menu = await loader.getHarness(MatMenuHarness);
			const spy = spyOn(
				component,
				'openDescriptionDialog'
			).and.callThrough();
			const dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of({
					original: 'abcdef',
					type: 'description',
					return: 'jkl',
				}),
				close: null,
			});
			const _dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			const serviceSpy = spyOn(
				TestBed.inject(CurrentMessagesService),
				'partialUpdateSubMessage'
			).and.callThrough();
			await menu.clickItem({ text: new RegExp('Open Description') });
			expect(spy).toHaveBeenCalled();
			expect(serviceSpy).toHaveBeenCalled();
		});

		it('should open the menu and remove a sub message', async () => {
			component.openMenu(
				mEvent,
				messagesMock[0],
				subMessagesMock[0],
				'string',
				'',
				' '
			);
			await fixture.whenStable();
			const menu = await loader.getHarness(MatMenuHarness);
			const spy = spyOn(component, 'removeSubMessage').and.callThrough();
			const dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of('ok'),
				close: null,
			});
			const _dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			const serviceSpy = spyOn(
				TestBed.inject(CurrentMessagesService),
				'removeSubMessage'
			).and.callThrough();
			await menu.clickItem({
				text: new RegExp('Remove submsg from message'),
			});
			expect(spy).toHaveBeenCalled();
			expect(serviceSpy).toHaveBeenCalled();
		});

		it('should open the menu and not remove a sub message', async () => {
			component.openMenu(
				mEvent,
				messagesMock[0],
				subMessagesMock[0],
				'string',
				'',
				' '
			);
			await fixture.whenStable();
			const menu = await loader.getHarness(MatMenuHarness);
			const spy = spyOn(component, 'removeSubMessage').and.callThrough();
			const dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of(),
				close: null,
			});
			const _dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			const serviceSpy = spyOn(
				TestBed.inject(CurrentMessagesService),
				'removeSubMessage'
			).and.callThrough();
			await menu.clickItem({
				text: new RegExp('Remove submsg from message'),
			});
			expect(spy).toHaveBeenCalled();
			expect(serviceSpy).not.toHaveBeenCalled();
		});

		it('should open the menu and delete a sub message', async () => {
			component.openMenu(
				mEvent,
				messagesMock[0],
				subMessagesMock[0],
				'string',
				'',
				' '
			);
			await fixture.whenStable();
			const menu = await loader.getHarness(MatMenuHarness);
			const spy = spyOn(component, 'deleteSubMessage').and.callThrough();
			const dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of('ok'),
				close: null,
			});
			const _dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			const serviceSpy = spyOn(
				TestBed.inject(CurrentMessagesService),
				'deleteSubMessage'
			).and.callThrough();
			await menu.clickItem({
				text: new RegExp('Delete submsg globally'),
			});
			expect(spy).toHaveBeenCalled();
			expect(serviceSpy).toHaveBeenCalled();
		});

		it('should open the menu and not delete a sub message', async () => {
			component.openMenu(
				mEvent,
				messagesMock[0],
				subMessagesMock[0],
				'string',
				'',
				' '
			);
			await fixture.whenStable();
			const menu = await loader.getHarness(MatMenuHarness);
			const spy = spyOn(component, 'deleteSubMessage').and.callThrough();
			const dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of(),
				close: null,
			});
			const _dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			const serviceSpy = spyOn(
				TestBed.inject(CurrentMessagesService),
				'deleteSubMessage'
			).and.callThrough();
			await menu.clickItem({
				text: new RegExp('Delete submsg globally'),
			});
			expect(spy).toHaveBeenCalled();
			expect(serviceSpy).not.toHaveBeenCalled();
		});

		afterEach(() => {
			component.matMenuTrigger().closeMenu();
		});
	});
});
