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
import {
	NgIf,
	NgFor,
	AsyncPipe,
	TitleCasePipe,
	NgTemplateOutlet,
} from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatOptionModule } from '@angular/material/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MockUnitDropdownComponent } from '@osee/messaging/shared/dropdowns/testing';
import { TypesService, EnumsService } from '@osee/messaging/shared/services';
import {
	typesServiceMock,
	enumsServiceMock,
	MockUniquePlatformTypeAttributesDirective,
} from '@osee/messaging/shared/testing';
import { FirstLetterLowerPipe } from '@osee/shared/utils';

import { NewPlatformTypeFormComponent } from './new-platform-type-form.component';

describe('NewPlatformTypeFormComponent', () => {
	let component: NewPlatformTypeFormComponent;
	let fixture: ComponentFixture<NewPlatformTypeFormComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [NoopAnimationsModule, NewPlatformTypeFormComponent],
		})
			.overrideComponent(NewPlatformTypeFormComponent, {
				set: {
					providers: [
						{ provide: TypesService, useValue: typesServiceMock },
						{ provide: EnumsService, useValue: enumsServiceMock },
					],
					viewProviders: [],
					imports: [
						NgIf,
						NgFor,
						AsyncPipe,
						FormsModule,
						MatFormFieldModule,
						MatOptionModule,
						MatInputModule,
						MatSelectModule,
						TitleCasePipe,
						MockUniquePlatformTypeAttributesDirective,
						FirstLetterLowerPipe,
						NgTemplateOutlet,
						MockUnitDropdownComponent,
					],
				},
			})
			.compileComponents();

		fixture = TestBed.createComponent(NewPlatformTypeFormComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
