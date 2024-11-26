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
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MockApplicabilityDropdownComponent } from '@osee/applicability/applicability-dropdown/testing';
import {
	CurrentStructureService,
	EnumerationSetService,
	TypesUIService,
} from '@osee/messaging/shared/services';
import {
	CurrentStateServiceMock,
	MockNewTypeFormComponent,
	dialogRef,
	elementsMock,
	enumerationSetServiceMock,
	typesUIServiceMock,
} from '@osee/messaging/shared/testing';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import { MockMatOptionLoadingComponent } from '@osee/shared/components/testing';
import { PlatformTypeQueryComponent } from '../../dialogs/platform-type-query/platform-type-query.component';
import { ElementFormComponent } from './element-form.component';
import { Component, signal, viewChild } from '@angular/core';
import { ElementDialog } from '@osee/messaging/shared/types';
import { MockPlatformTypeDropdownComponent } from '@osee/messaging/types/dropdown/testing';

describe('ElementFormComponent', () => {
	let component: ParentDriverComponent;
	let fixture: ComponentFixture<ParentDriverComponent>;
	@Component({
		selector: 'osee-test-standalone-form',
		imports: [FormsModule, ElementFormComponent],
		template:
			'<form #testForm="ngForm"><osee-element-form [(data)]="data"/></form>',
	})
	class ParentDriverComponent {
		data = signal<ElementDialog>({
			id: '',
			name: '',
			startingElement: elementsMock[0],
			element: elementsMock[0],
			type: elementsMock[0].platformType,
			mode: 'add',
			allowArray: false,
			arrayChild: false,
			createdTypes: [],
		});
		elementFormComponent = viewChild.required(ElementFormComponent);
	}

	beforeEach(async () => {
		await TestBed.overrideComponent(ElementFormComponent, {
			set: {
				imports: [
					NgIf,
					NgFor,
					AsyncPipe,
					FormsModule,
					MatFormFieldModule,
					MatInputModule,
					MatSlideToggleModule,
					MatTooltipModule,
					MatDividerModule,
					MatSelectModule,
					MatIconModule,
					MatDialogModule,
					MatProgressSpinnerModule,
					MockApplicabilityDropdownComponent,
					PlatformTypeQueryComponent,
					MockMatOptionLoadingComponent,
					MockNewTypeFormComponent,
					MockPlatformTypeDropdownComponent,
				],
				providers: [
					{ provide: TypesUIService, useValue: typesUIServiceMock },
					{
						provide: STRUCTURE_SERVICE_TOKEN,
						useValue: CurrentStateServiceMock,
					},
					{ provide: MatDialog, useValue: dialogRef },
				],
			},
		})
			.configureTestingModule({
				imports: [NoopAnimationsModule, ElementFormComponent],
				providers: [
					{ provide: TypesUIService, useValue: typesUIServiceMock },
					{
						provide: CurrentStructureService,
						useValue: CurrentStateServiceMock,
					},
					{
						provide: EnumerationSetService,
						useValue: enumerationSetServiceMock,
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(ParentDriverComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
