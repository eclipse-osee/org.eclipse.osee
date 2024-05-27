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
import { FormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';

import { CdkDrag, CdkDragHandle, CdkDropList } from '@angular/cdk/drag-drop';
import { AsyncPipe, CommonModule, NgClass, NgFor, NgIf } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { RouterLink } from '@angular/router';
import { MockPersistedApplicabilityDropdownComponent } from '@osee/applicability/persisted-applicability-dropdown/testing';
import { MockPersistedBooleanAttributeToggleComponent } from '@osee/attributes/persisted-boolean-attribute-toggle/testing';
import { MockPersistedNumberAttributeInputComponent } from '@osee/attributes/persisted-number-attribute-input/testing';
import { MockPersistedStringAttributeInputComponent } from '@osee/attributes/persisted-string-attribute-input/testing';
import { MockPersistedMessagePeriodicityDropdownComponent } from '@osee/messaging/message-periodicity/persisted-message-periodicity-dropdown/testing';
import { MockEditMessageNodesFieldComponent } from '@osee/messaging/message-tables/testing';
import { MockPersistedMessageTypeDropdownComponent } from '@osee/messaging/message-type/persisted-message-type-dropdown/testing';
import { MockPersistedPublisherNodeDropdownComponent } from '@osee/messaging/nodes/persisted-publisher-node-dropdown/testing';
import { MockPersistedSubscriberNodeDropdownComponent } from '@osee/messaging/nodes/persisted-subscriber-node-dropdown/testing';
import { MockPersistedRateDropdownComponent } from '@osee/messaging/rate/persisted-rate-dropdown/testing';
import {
	CurrentMessagesService,
	EditAuthService,
	MessageUiService,
} from '@osee/messaging/shared/services';
import {
	CurrentMessageServiceMock,
	editAuthServiceMock,
	MessagingControlsMockComponent,
} from '@osee/messaging/shared/testing';
import { MockSingleDiffComponent } from '@osee/shared/testing';
import { HighlightFilteredTextDirective } from '@osee/shared/utils';
import { AddMessageDialogComponent } from '../../dialogs/add-message-dialog/add-message-dialog.component';
import { MessageMenuComponent } from '../../menus/message-menu/message-menu.component';
import { MessageImpactsValidatorDirective } from '../../message-impacts-validator.directive';
import { MockAddMessageDialogComponent } from '../../testing/add-message-dialog.component.mock';
import { MockEditMessageFieldComponent } from '../../testing/edit-message-field.component.mock';
import { MockSubMessageTableComponent } from '../../testing/sub-message-table.component.mock';
import { MessageTableComponent } from './message-table.component';

describe('MessageTableComponent', () => {
	let component: MessageTableComponent;
	let uiService: MessageUiService;
	let fixture: ComponentFixture<MessageTableComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(MessageTableComponent, {
			set: {
				imports: [
					NgIf,
					AsyncPipe,
					RouterLink,
					FormsModule,
					NgFor,
					NgClass,
					CdkDrag,
					CdkDragHandle,
					CdkDropList,
					MatButtonModule,
					MatIconModule,
					MatFormFieldModule,
					MatTableModule,
					MatInputModule,
					MatTooltipModule,
					MatMenuModule,
					MatDialogModule,
					MatPaginatorModule,
					MessagingControlsMockComponent,
					MockSubMessageTableComponent,
					MockAddMessageDialogComponent,
					MockEditMessageFieldComponent,
					MockEditMessageNodesFieldComponent,
					MockSingleDiffComponent,
					HighlightFilteredTextDirective,
					MessageMenuComponent,
					MockPersistedPublisherNodeDropdownComponent,
					MockPersistedSubscriberNodeDropdownComponent,
					MockPersistedBooleanAttributeToggleComponent,
					MockPersistedStringAttributeInputComponent,
					MockPersistedNumberAttributeInputComponent,
					MockPersistedApplicabilityDropdownComponent,
					MockPersistedMessageTypeDropdownComponent,
					MockPersistedMessagePeriodicityDropdownComponent,
					MockPersistedRateDropdownComponent,
					FormsModule,
					MessageImpactsValidatorDirective,
				],
				providers: [
					{
						provide: CurrentMessagesService,
						useValue: CurrentMessageServiceMock,
					},
					{ provide: EditAuthService, useValue: editAuthServiceMock },
				],
			},
		})
			.configureTestingModule({
				imports: [
					CommonModule,
					FormsModule,
					CdkDrag,
					CdkDragHandle,
					CdkDropList,
					MatFormFieldModule,
					MatInputModule,
					MatIconModule,
					MatSelectModule,
					MatTableModule,
					MatSlideToggleModule,
					MatButtonModule,
					MatSidenavModule,
					NoopAnimationsModule,
					MatTooltipModule,
					MatMenuModule,
					MatDialogModule,
					MessagingControlsMockComponent,
					RouterTestingModule.withRoutes([
						{ path: 'diff', component: MessageTableComponent },
						{
							path: 'diffOpen',
							component: MockSingleDiffComponent,
							outlet: 'rightSideNav',
						},
					]),
					MockSingleDiffComponent,
					HighlightFilteredTextDirective,
					AddMessageDialogComponent,
					MockEditMessageFieldComponent,
					MockEditMessageNodesFieldComponent,
					MessageTableComponent,
					MockSubMessageTableComponent,
					MockAddMessageDialogComponent,
				],
				declarations: [],
				providers: [
					{
						provide: CurrentMessagesService,
						useValue: CurrentMessageServiceMock,
					},
					{ provide: EditAuthService, useValue: editAuthServiceMock },
				],
			})
			.compileComponents();
		uiService = TestBed.inject(MessageUiService);
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(MessageTableComponent);
		uiService.BranchIdString = '10';
		component = fixture.componentInstance;
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

	// it('should filter the sub level table', async () => {
	// don't know how to test yet
	//   let form = await loader.getHarness(MatFormFieldHarness);
	//   let input = await form.getControl(MatInputHarness);
	//   await input?.setValue('sub message: Hello');
	// })

	// xdescribe('menu testing', () => {
	// 	let mEvent: MouseEvent;
	// 	beforeEach(() => {
	// 		mEvent = document.createEvent('MouseEvent');
	// 	});

	// 	it('should open the menu and dismiss a description', async () => {
	// 		component.openMenu(mEvent, messagesMock[0], '', '');
	// 		await fixture.whenStable();
	// 		let menu = await loader.getHarness(MatMenuHarness);
	// 		let spy = spyOn(
	// 			component,
	// 			'openDescriptionDialog'
	// 		).and.callThrough();
	// 		let dialogRefSpy = jasmine.createSpyObj({
	// 			afterClosed: of('ok'),
	// 			close: null,
	// 		});
	// 		let dialogSpy = spyOn(
	// 			TestBed.inject(MatDialog),
	// 			'open'
	// 		).and.returnValue(dialogRefSpy);
	// 		let serviceSpy = spyOn(
	// 			TestBed.inject(CurrentMessagesService),
	// 			'partialUpdateMessage'
	// 		).and.callThrough();
	// 		await menu.clickItem({ text: new RegExp('Open Description') });
	// 		expect(spy).toHaveBeenCalled();
	// 		expect(serviceSpy).toHaveBeenCalled();
	// 	});

	// 	it('should open the menu and edit a description', async () => {
	// 		component.openMenu(mEvent, messagesMock[0], '', '');
	// 		await fixture.whenStable();
	// 		let menu = await loader.getHarness(MatMenuHarness);
	// 		let spy = spyOn(
	// 			component,
	// 			'openDescriptionDialog'
	// 		).and.callThrough();
	// 		let dialogRefSpy = jasmine.createSpyObj({
	// 			afterClosed: of({
	// 				original: 'abcdef',
	// 				type: 'description',
	// 				return: 'jkl',
	// 			}),
	// 			close: null,
	// 		});
	// 		let dialogSpy = spyOn(
	// 			TestBed.inject(MatDialog),
	// 			'open'
	// 		).and.returnValue(dialogRefSpy);
	// 		let serviceSpy = spyOn(
	// 			TestBed.inject(CurrentMessagesService),
	// 			'partialUpdateMessage'
	// 		).and.callThrough();
	// 		await menu.clickItem({ text: new RegExp('Open Description') });
	// 		expect(spy).toHaveBeenCalled();
	// 		expect(serviceSpy).toHaveBeenCalled();
	// 	});

	// 	it('should open the menu and open the sidenav for diffs', async () => {
	// 		component.openMenu(mEvent, messagesMock[0], 'field', 'name');
	// 		await fixture.whenStable();
	// 		let menu = await loader.getHarness(MatMenuHarness);
	// 		expect(menu).toBeDefined();
	// 		let spy = spyOn(component, 'viewDiff').and.callThrough();
	// 		await menu.clickItem({ text: new RegExp('View Diff') });
	// 		expect(spy).toHaveBeenCalled();
	// 	});

	// 	it('should open a dialog and remove a message', async () => {
	// 		component.openMenu(mEvent, messagesMock[0], '', '');
	// 		await fixture.whenStable();
	// 		let menu = await loader.getHarness(MatMenuHarness);
	// 		let spy = spyOn(component, 'removeMessage').and.callThrough();
	// 		let dialogRefSpy = jasmine.createSpyObj({
	// 			afterClosed: of('ok'),
	// 			close: null,
	// 		});
	// 		let dialogSpy = spyOn(
	// 			TestBed.inject(MatDialog),
	// 			'open'
	// 		).and.returnValue(dialogRefSpy);
	// 		let serviceSpy = spyOn(
	// 			TestBed.inject(CurrentMessagesService),
	// 			'removeMessage'
	// 		).and.callThrough();
	// 		await menu.clickItem({
	// 			text: new RegExp('Remove message from connection'),
	// 		});
	// 		expect(spy).toHaveBeenCalled();
	// 		expect(serviceSpy).toHaveBeenCalled();
	// 	});

	// 	it('should open a dialog and delete a message', async () => {
	// 		component.openMenu(mEvent, messagesMock[1], '', '');
	// 		await fixture.whenStable();
	// 		let menu = await loader.getHarness(MatMenuHarness);
	// 		let spy = spyOn(component, 'deleteMessage').and.callThrough();
	// 		let dialogRefSpy = jasmine.createSpyObj({
	// 			afterClosed: of('ok'),
	// 			close: null,
	// 		});
	// 		let dialogSpy = spyOn(
	// 			TestBed.inject(MatDialog),
	// 			'open'
	// 		).and.returnValue(dialogRefSpy);
	// 		let serviceSpy = spyOn(
	// 			TestBed.inject(CurrentMessagesService),
	// 			'deleteMessage'
	// 		).and.callThrough();
	// 		await menu.clickItem({
	// 			text: new RegExp('Delete message globally'),
	// 		});
	// 		expect(spy).toHaveBeenCalled();
	// 		expect(serviceSpy).toHaveBeenCalled();
	// 	});
	// 	afterEach(() => {
	// 		component.matMenuTrigger().closeMenu();
	// 	});
	// });
});
