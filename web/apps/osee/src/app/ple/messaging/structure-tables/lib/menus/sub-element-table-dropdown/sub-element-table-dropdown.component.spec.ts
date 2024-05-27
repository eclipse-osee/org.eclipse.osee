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
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { RouterLink, provideRouter } from '@angular/router';

import { SubElementTableDropdownComponent } from './sub-element-table-dropdown.component';

import { MatDividerModule } from '@angular/material/divider';
import { applicabilitySentinel } from '@osee/applicability/types';
import { AttributeToValuePipe } from '@osee/attributes/pipes';
import { CurrentStructureService } from '@osee/messaging/shared/services';
import {
	CurrentStateServiceMock,
	elementTableDropdownServiceMock,
	elementsMock,
} from '@osee/messaging/shared/testing';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import { ElementTableDropdownService } from '../../services/element-table-dropdown.service';

describe('SubElementTableDropdownComponent', () => {
	let component: SubElementTableDropdownComponent;
	let fixture: ComponentFixture<SubElementTableDropdownComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(SubElementTableDropdownComponent, {
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
				imports: [SubElementTableDropdownComponent],
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

		fixture = TestBed.createComponent(SubElementTableDropdownComponent);
		component = fixture.componentInstance;
		component.structure = {
			id: '-1',
			gammaId: '-1',
			name: {
				id: '-1',
				typeId: '1152921504606847088',
				gammaId: '-1',
				value: '',
			},
			nameAbbrev: {
				id: '-1',
				typeId: '8355308043647703563',
				gammaId: '-1',
				value: '',
			},
			description: {
				id: '-1',
				typeId: '1152921504606847090',
				gammaId: '-1',
				value: '',
			},
			interfaceMaxSimultaneity: {
				id: '-1',
				typeId: '2455059983007225756',
				gammaId: '-1',
				value: '',
			},
			interfaceMinSimultaneity: {
				id: '-1',
				typeId: '2455059983007225755',
				gammaId: '-1',
				value: '',
			},
			interfaceTaskFileType: {
				id: '-1',
				typeId: '2455059983007225760',
				gammaId: '-1',
				value: 0,
			},
			interfaceStructureCategory: {
				id: '-1',
				typeId: '2455059983007225764',
				gammaId: '-1',
				value: '',
			},
			applicability: applicabilitySentinel,
			elements: [],
		};
		component.element = elementsMock[0];
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
