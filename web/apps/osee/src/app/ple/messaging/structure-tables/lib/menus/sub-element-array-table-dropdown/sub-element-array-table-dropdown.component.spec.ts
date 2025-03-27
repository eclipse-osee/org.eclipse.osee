/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { AsyncPipe, NgIf } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { provideRouter, RouterLink } from '@angular/router';
import { AttributeToValuePipe } from '@osee/attributes/pipes';
import { CurrentStructureService } from '@osee/messaging/shared/services';
import {
	CurrentStateServiceMock,
	elementsMock,
	elementTableDropdownServiceMock,
} from '@osee/messaging/shared/testing';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import { ElementTableDropdownService } from '../../services/element-table-dropdown.service';
import { SubElementArrayTableDropdownComponent } from './sub-element-array-table-dropdown.component';

describe('SubElementTableDropdownComponent', () => {
	let component: SubElementArrayTableDropdownComponent;
	let fixture: ComponentFixture<SubElementArrayTableDropdownComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(SubElementArrayTableDropdownComponent, {
			set: {
				imports: [
					NgIf,
					AsyncPipe,
					RouterLink,
					MatMenuModule,
					MatIconModule,
					MatDialogModule,
					MatFormFieldModule,
					MatDividerModule,
					AttributeToValuePipe,
				],
				providers: [
					{
						provide: STRUCTURE_SERVICE_TOKEN,
						useValue: CurrentStateServiceMock,
					},
					{
						provide: CurrentStructureService,
						useValue: CurrentStateServiceMock,
					},
					{
						provide: ElementTableDropdownService,
						useValue: elementTableDropdownServiceMock,
					},
				],
			},
		})
			.configureTestingModule({
				imports: [SubElementArrayTableDropdownComponent],
				providers: [
					provideRouter([]),
					{
						provide: STRUCTURE_SERVICE_TOKEN,
						useValue: CurrentStateServiceMock,
					},
					{
						provide: CurrentStructureService,
						useValue: CurrentStateServiceMock,
					},
					{
						provide: ElementTableDropdownService,
						useValue: elementTableDropdownServiceMock,
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(
			SubElementArrayTableDropdownComponent
		);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('element', elementsMock[0]);
		fixture.componentRef.setInput('headerElement', elementsMock[1]);
		fixture.componentRef.setInput('header', 'Name');
		fixture.componentRef.setInput('branchId', '1');
		fixture.componentRef.setInput('branchType', 'working');
		fixture.componentRef.setInput('editMode', true);
		fixture.componentRef.setInput('selectedElements', []);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
