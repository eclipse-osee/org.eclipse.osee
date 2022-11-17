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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatTableHarness } from '@angular/material/table/testing';
import { MatFormFieldHarness } from '@angular/material/form-field/testing';
import { MatInputHarness } from '@angular/material/input/testing';
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { OseeStringUtilsDirectivesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-directives/osee-string-utils-directives.module';
import { OseeStringUtilsPipesModule } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-pipes/osee-string-utils-pipes.module';
import { ConvertMessageTableTitlesToStringPipe } from '../../pipes/convert-message-table-titles-to-string.pipe';

import { MessageTableComponent } from './message-table.component';
import { CurrentMessagesService } from '../../services/current-messages.service';
import { message } from '../../types/messages';
import { of } from 'rxjs';
import { MatMenuModule } from '@angular/material/menu';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { EditMessageFieldComponentMock } from '../../mocks/components/EditMessageField.mock';
import { SubMessageTableComponentMock } from '../../mocks/components/SubMessageTable.mock';
import { EditAuthService } from '../../../shared/services/edit-auth-service.service';
import { editAuthServiceMock } from '../../../connection-view/mocks/EditAuthService.mock';
import { EnumsService } from '../../../shared/services/http/enums.service';
import { enumsServiceMock } from '../../../shared/mocks/EnumsService.mock';
import { CommonModule } from '@angular/common';
import { AddMessageDialogComponentMock } from '../../mocks/components/AddMessageDialog.mock';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { AddMessageDialogComponent } from './add-message-dialog/add-message-dialog.component';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MessageUiService } from '../../services/ui.service';
import { CurrentMessageServiceMock } from '../../mocks/services/CurrentMessageService.mock';
import { messagesMock } from '../../mocks/ReturnObjects/messages.mock';
import { MatMenuHarness } from '@angular/material/menu/testing';
import { MimSingleDiffDummy } from 'src/app/ple/diff-views/mocks/mim-single-diff.mock';
import { MatSidenavModule } from '@angular/material/sidenav';
import { HighlightFilteredTextDirective } from 'src/app/osee-utils/osee-string-utils/osee-string-utils-directives/highlight-filtered-text.directive';
import { MatIconModule } from '@angular/material/icon';
import { GenericButtonsModule } from 'src/app/ple/generic-buttons/generic-buttons.module';
import { TwoLayerAddButtonHarness } from 'src/app/ple/generic-buttons/two-layer-add-button/two-layer-add-button.harness';
import { AddSubMessageDialog } from '../../types/AddSubMessageDialog';
import { ActionDropdownStub } from '../../../../../shared-components/components/action-state-button/action-drop-down/action-drop-down.mock.component';
import { BranchUndoButtonTestingModule } from '../../../../../shared-components/components/branch-undo-button/branch.undo-button.testing.module';
import { transactionResultMock } from 'src/app/transactions/transaction.mock';

let loader: HarnessLoader;

describe('MessageTableComponent', () => {
	let component: MessageTableComponent;
	let uiService: MessageUiService;
	let fixture: ComponentFixture<MessageTableComponent>;
	let expectedData: message[] = [
		{
			id: '10',
			name: 'name',
			description: 'description',
			interfaceMessageRate: '50Hz',
			interfaceMessageNumber: '0',
			interfaceMessagePeriodicity: '1Hz',
			interfaceMessageWriteAccess: true,
			interfaceMessageType: 'Connection',
			initiatingNode: {
				id: '1',
				name: 'Node 1',
			},
			subMessages: [
				{
					id: '5',
					name: 'sub message name',
					description: '',
					interfaceSubMessageNumber: '0',
					applicability: {
						id: '1',
						name: 'Base',
					},
				},
			],
			applicability: {
				id: '1',
				name: 'Base',
			},
		},
	];

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				CommonModule,
				FormsModule,
				MatFormFieldModule,
				MatInputModule,
				MatIconModule,
				MatSelectModule,
				MatTableModule,
				MatSlideToggleModule,
				MatButtonModule,
				MatSidenavModule,
				OseeStringUtilsDirectivesModule,
				OseeStringUtilsPipesModule,
				NoopAnimationsModule,
				MatTooltipModule,
				MatMenuModule,
				MatDialogModule,
				GenericButtonsModule,
				BranchUndoButtonTestingModule,
				RouterTestingModule.withRoutes([
					{ path: 'diff', component: MessageTableComponent },
					{
						path: 'diffOpen',
						component: MimSingleDiffDummy,
						outlet: 'rightSideNav',
					},
				]),
			],
			declarations: [
				MessageTableComponent,
				ConvertMessageTableTitlesToStringPipe,
				SubMessageTableComponentMock,
				EditMessageFieldComponentMock,
				AddMessageDialogComponentMock,
				AddMessageDialogComponent,
				MimSingleDiffDummy,
				HighlightFilteredTextDirective,
				ActionDropdownStub,
			],
			providers: [
				{
					provide: CurrentMessagesService,
					useValue: CurrentMessageServiceMock,
				},
				{ provide: EditAuthService, useValue: editAuthServiceMock },
				{ provide: EnumsService, useValue: enumsServiceMock },
			],
		}).compileComponents();
		uiService = TestBed.inject(MessageUiService);
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(MessageTableComponent);
		uiService.BranchIdString = '10';
		component = fixture.componentInstance;
		loader = TestbedHarnessEnvironment.loader(fixture);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	/**
	 * TBD need to figure out a better mocking solution for this
	 */
	// it('should expand a row and hide a row on click', async () => {
	//   expect(component).toBeTruthy();
	//   const expandRow = spyOn(component, 'expandRow').and.callThrough();
	//   const hideRow = spyOn(component, 'hideRow').and.callThrough();
	//   let table = await loader.getHarness(MatTableHarness);
	//   let buttons = await table.getAllHarnesses(MatButtonHarness);
	//   await buttons[0].click();
	//   await expect(expandRow).toHaveBeenCalled();
	//   fixture.detectChanges();
	//   await fixture.whenStable();
	//   let hiddenButtons = await table.getAllHarnesses(MatButtonHarness);
	//   await hiddenButtons[0].click();
	//   expect(hideRow).toHaveBeenCalled();
	// });

	// it('should fail to hide random element', () => {
	//   component.hideRow({id:'1'} as message);
	//   expect(component.expandedElement.getValue().indexOf({id:'1'} as message)).toEqual(-1);
	// })

	it('should filter the top level table', async () => {
		let spy = spyOn(component, 'applyFilter').and.callThrough();
		let form = await loader.getHarness(MatFormFieldHarness);
		let input = await form.getControl(MatInputHarness);
		await input?.focus();
		await input?.setValue('Hello');
		expect(spy).toHaveBeenCalled();
	});

	it('should open the create new message dialog', async () => {
		let dialogRefSpy = jasmine.createSpyObj({
			afterClosed: of({
				name: '',
				description: '',
				interfaceMessageNumber: '',
				interfaceMessagePeriodicity: '',
				interfaceMessageRate: '',
				interfaceMessageType: '',
				interfaceMessageWriteAccess: '',
			}),
			close: null,
		});
		let dialogSpy = spyOn(
			TestBed.inject(MatDialog),
			'open'
		).and.returnValue(dialogRefSpy);
		let spy = spyOn(component, 'openNewMessageDialog').and.callThrough();
		let addmenu = await loader.getHarness(TwoLayerAddButtonHarness);
		await addmenu.toggleOpen();
		expect(await addmenu.isOpen()).toBeTruthy();
		await addmenu.clickFirstOption();
		expect(spy).toHaveBeenCalled();
	});

	it('should open the create new sub message dialog', async () => {
		let dialogRefSpy = jasmine.createSpyObj({
			afterClosed: of<AddSubMessageDialog>({
				id: '2',
				name: 'blah',
				subMessage: {
					id: '5',
					name: 'abcdef',
					description: 'qwerty',
					interfaceSubMessageNumber: '12345',
				},
			}),
			close: null,
		});
		let dialogSpy = spyOn(
			TestBed.inject(MatDialog),
			'open'
		).and.returnValue(dialogRefSpy);
		let spy = spyOn(component, 'createNewSubMessage').and.callThrough();
		component.rowChange(
			{ id: '1', name: 'dummy element' } as message,
			true
		);
		let addmenu = await loader.getHarness(TwoLayerAddButtonHarness);
		await addmenu.toggleOpen();
		expect(await addmenu.isOpen()).toBeTruthy();
		await addmenu.clickItem({
			text: 'description Add submessage to dummy element',
		});
		expect(spy).toHaveBeenCalled();
	});

	it('should open the create new sub message dialog and relate', async () => {
		let dialogRefSpy = jasmine.createSpyObj({
			afterClosed: of<AddSubMessageDialog>({
				id: '2',
				name: 'blah',
				subMessage: {
					name: 'abcdef',
					description: 'qwerty',
					interfaceSubMessageNumber: '12345',
				},
			}),
			close: null,
		});
		let dialogSpy = spyOn(
			TestBed.inject(MatDialog),
			'open'
		).and.returnValue(dialogRefSpy);
		let spy = spyOn(component, 'createNewSubMessage').and.callThrough();
		component.rowChange(
			{ id: '1', name: 'dummy element' } as message,
			true
		);
		let addmenu = await loader.getHarness(TwoLayerAddButtonHarness);
		await addmenu.toggleOpen();
		expect(await addmenu.isOpen()).toBeTruthy();
		await addmenu.clickItem({
			text: 'description Add submessage to dummy element',
		});
		expect(spy).toHaveBeenCalled();
	});
	// it('should filter the sub level table', async () => {
	// don't know how to test yet
	//   let form = await loader.getHarness(MatFormFieldHarness);
	//   let input = await form.getControl(MatInputHarness);
	//   await input?.setValue('sub message: Hello');
	// })

	describe('menu testing', () => {
		let mEvent: MouseEvent;
		beforeEach(() => {
			mEvent = document.createEvent('MouseEvent');
		});

		it('should open the menu and dismiss a description', async () => {
			component.openMenu(mEvent, messagesMock[0], '', '');
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
				'partialUpdateMessage'
			).and.callThrough();
			await menu.clickItem({ text: new RegExp('Open Description') });
			expect(spy).toHaveBeenCalled();
			expect(serviceSpy).toHaveBeenCalled();
		});

		it('should open the menu and edit a description', async () => {
			component.openMenu(mEvent, messagesMock[0], '', '');
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
				'partialUpdateMessage'
			).and.callThrough();
			await menu.clickItem({ text: new RegExp('Open Description') });
			expect(spy).toHaveBeenCalled();
			expect(serviceSpy).toHaveBeenCalled();
		});

		it('should open the menu and open the sidenav for diffs', async () => {
			component.openMenu(mEvent, messagesMock[0], 'field', 'name');
			await fixture.whenStable();
			let menu = await loader.getHarness(MatMenuHarness);
			expect(menu).toBeDefined();
			let spy = spyOn(component, 'viewDiff').and.callThrough();
			await menu.clickItem({ text: new RegExp('View Diff') });
			expect(spy).toHaveBeenCalled();
		});

		it('should open a dialog and remove a message', async () => {
			component.openMenu(mEvent, messagesMock[0], '', '');
			await fixture.whenStable();
			let menu = await loader.getHarness(MatMenuHarness);
			let spy = spyOn(component, 'removeMessage').and.callThrough();
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
				'removeMessage'
			).and.callThrough();
			await menu.clickItem({
				text: new RegExp('Remove message from connection'),
			});
			expect(spy).toHaveBeenCalled();
			expect(serviceSpy).toHaveBeenCalled();
		});

		it('should open a dialog and delete a message', async () => {
			component.openMenu(mEvent, messagesMock[1], '', '');
			await fixture.whenStable();
			let menu = await loader.getHarness(MatMenuHarness);
			let spy = spyOn(component, 'deleteMessage').and.callThrough();
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
				'deleteMessage'
			).and.callThrough();
			await menu.clickItem({
				text: new RegExp('Delete message globally'),
			});
			expect(spy).toHaveBeenCalled();
			expect(serviceSpy).toHaveBeenCalled();
		});
		afterEach(() => {
			component.matMenuTrigger.closeMenu();
		});
	});
});
