/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { preferencesUiServiceMock } from 'src/app/ple/messaging/shared/services/ui/preferences-ui-service.mock';
import { PreferencesUIService } from 'src/app/ple/messaging/shared/services/ui/preferences-ui.service';

import { EnumLiteralsFieldComponent } from './enum-literals-field.component';

describe('EnumLiteralFieldComponent', () => {
	let component: EnumLiteralsFieldComponent;
	let fixture: ComponentFixture<EnumLiteralsFieldComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [EnumLiteralsFieldComponent],
			providers: [
				{
					provide: PreferencesUIService,
					useValue: preferencesUiServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(EnumLiteralsFieldComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
