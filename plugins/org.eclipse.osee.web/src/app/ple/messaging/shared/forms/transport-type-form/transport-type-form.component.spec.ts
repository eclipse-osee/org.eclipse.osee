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
import { NgFor, AsyncPipe } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MockMessageTypeDropdownComponent } from '@osee/messaging/shared/dropdowns/testing';
import { MockApplicabilitySelectorComponent } from '@osee/shared/components/testing';

import { TransportTypeFormComponent } from './transport-type-form.component';

describe('TransportTypeFormComponent', () => {
	let component: TransportTypeFormComponent;
	let fixture: ComponentFixture<TransportTypeFormComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(TransportTypeFormComponent, {
			set: {
				imports: [
					FormsModule,
					NgFor,
					AsyncPipe,
					MatFormFieldModule,
					MatButtonModule,
					MatInputModule,
					MatSelectModule,
					MatSlideToggleModule,
					MatDialogModule,
					MockMessageTypeDropdownComponent,
					MockApplicabilitySelectorComponent,
				],
			},
		})
			.configureTestingModule({
				imports: [NoopAnimationsModule, TransportTypeFormComponent],
			})
			.compileComponents();

		fixture = TestBed.createComponent(TransportTypeFormComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
