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
import { MockApplicabilityDropdownComponent } from '@osee/applicability/applicability-dropdown/testing';

import { TransportTypeFormComponent } from './transport-type-form.component';
import { MockMessageTypeDropdownComponent } from '@osee/messaging/message-type/message-type-dropdown/testing';

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
					MockApplicabilityDropdownComponent,
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
