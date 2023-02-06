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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { TypesService, EnumsService } from '@osee/messaging/shared/services';
import {
	typesServiceMock,
	enumsServiceMock,
} from '@osee/messaging/shared/testing';

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
