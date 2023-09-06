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
import { HttpClientTestingModule } from '@angular/common/http/testing';
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

import { SubMessageTableComponent } from './sub-message-table.component';
import { MockEditMessageFieldComponent } from '../../testing/edit-sub-message-field.component.mock';
import { CurrentMessagesService } from '@osee/messaging/shared/services';
import { HighlightFilteredTextDirective } from '@osee/shared/utils';
import {
	CurrentMessageServiceMock,
	messagesMock,
	subMessagesMock,
} from '@osee/messaging/shared/testing';
import { MockSingleDiffComponent } from '@osee/shared/testing';
import { applicabilitySentinel } from '@osee/shared/types/applicability';

describe('SubMessageTableComponent', () => {
	let component: SubMessageTableComponent;
	let fixture: ComponentFixture<SubMessageTableComponent>;
	let loader: HarnessLoader;
	let scheduler: TestScheduler;
	let expectedData = [
		{
			name: 'Name',
			description:
				'description adslkfj;asjfadkljf;lajdfla;jsdfdlkasjf;lkajslfjad;ljfkladjsf;',
			interfaceSubMessageNumber: '0',
			interfaceMessageRate: '1Hz',
		},
		{
			name: 'Name2',
			description: 'description2',
			interfaceSubMessageNumber: '1',
			interfaceMessageRate: '1Hz',
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
				HttpClientTestingModule,
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
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(SubMessageTableComponent);
		component = fixture.componentInstance;
		component.element = {
			id: '5',
			name: 'blah',
			description: 'abcdef',
			interfaceMessageNumber: '1234',
			interfaceMessagePeriodicity: 'Aperiodic',
			interfaceMessageRate: '5Hz',
			interfaceMessageType: 'Connection',
			interfaceMessageWriteAccess: true,
			subMessages: [],
			interfaceMessageExclude: false,
			interfaceMessageIoMode: '',
			interfaceMessageModeCode: '',
			interfaceMessageRateVer: '',
			interfaceMessagePriority: '',
			interfaceMessageProtocol: '',
			interfaceMessageRptWordCount: '',
			interfaceMessageRptCmdWord: '',
			interfaceMessageRunBeforeProc: false,
			interfaceMessageVer: '',
			publisherNodes: [
				{
					id: '100',
					name: 'Node1',
				},
			],
			subscriberNodes: [
				{
					id: '101',
					name: 'Node2',
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
			let menu = await loader.getHarness(MatMenuHarness);
			let spy = spyOn(component, 'viewDiff').and.callThrough();
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
			let menu = await loader.getHarness(MatMenuHarness);
			let spy = spyOn(
				component,
				'openDescriptionDialog'
			).and.callThrough();
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of('ok'),
				close: null,
			});
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			let serviceSpy = spyOn(
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
			let menu = await loader.getHarness(MatMenuHarness);
			let spy = spyOn(
				component,
				'openDescriptionDialog'
			).and.callThrough();
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of({
					original: 'abcdef',
					type: 'description',
					return: 'jkl',
				}),
				close: null,
			});
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			let serviceSpy = spyOn(
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
			let menu = await loader.getHarness(MatMenuHarness);
			let spy = spyOn(component, 'removeSubMessage').and.callThrough();
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of('ok'),
				close: null,
			});
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			let serviceSpy = spyOn(
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
			let menu = await loader.getHarness(MatMenuHarness);
			let spy = spyOn(component, 'removeSubMessage').and.callThrough();
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of(),
				close: null,
			});
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			let serviceSpy = spyOn(
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
			let menu = await loader.getHarness(MatMenuHarness);
			let spy = spyOn(component, 'deleteSubMessage').and.callThrough();
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of('ok'),
				close: null,
			});
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			let serviceSpy = spyOn(
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
			let menu = await loader.getHarness(MatMenuHarness);
			let spy = spyOn(component, 'deleteSubMessage').and.callThrough();
			let dialogRefSpy = jasmine.createSpyObj({
				afterClosed: of(),
				close: null,
			});
			let dialogSpy = spyOn(
				TestBed.inject(MatDialog),
				'open'
			).and.returnValue(dialogRefSpy);
			let serviceSpy = spyOn(
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
			component.matMenuTrigger.closeMenu();
		});
	});
});
