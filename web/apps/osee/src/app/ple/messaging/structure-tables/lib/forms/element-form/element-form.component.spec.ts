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
 **********************************************************************/ import {
	NgIf,
	NgFor,
	AsyncPipe,
} from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import {
	EnumerationSetService,
	EnumsService,
	TypesService,
} from '@osee/messaging/shared/services';
import {
	CurrentStateServiceMock,
	dialogRef,
	enumerationSetServiceMock,
	enumsServiceMock,
	MockNewTypeFormComponent,
	typesServiceMock,
} from '@osee/messaging/shared/testing';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import {
	MockApplicabilitySelectorComponent,
	MockMatOptionLoadingComponent,
} from '@osee/shared/components/testing';
import { ElementFormComponent } from './element-form.component';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { PlatformTypeQueryComponent } from '../../dialogs/platform-type-query/platform-type-query.component';

describe('ElementFormComponent', () => {
	let component: ElementFormComponent;
	let fixture: ComponentFixture<ElementFormComponent>;

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
					MockApplicabilitySelectorComponent,
					PlatformTypeQueryComponent,
					MockMatOptionLoadingComponent,
					MockNewTypeFormComponent,
				],
				providers: [
					{ provide: TypesService, useValue: typesServiceMock },
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
					{ provide: TypesService, useValue: typesServiceMock },
					{
						provide: STRUCTURE_SERVICE_TOKEN,
						useValue: CurrentStateServiceMock,
					},
					{ provide: EnumsService, useValue: enumsServiceMock },
					{
						provide: EnumerationSetService,
						useValue: enumerationSetServiceMock,
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(ElementFormComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
