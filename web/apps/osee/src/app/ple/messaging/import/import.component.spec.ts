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
import { NgIf, AsyncPipe, NgFor } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MessagingControlsMockComponent } from '@osee/messaging/shared/testing';
import { ImportService } from '@osee/messaging/import';
import { importServiceMock } from '@osee/messaging/import/testing';
import { ImportComponent } from './import.component';
import { MessagingControlsComponent } from '@osee/messaging/shared/main-content';
import { provideRouter } from '@angular/router';
import { MockTransportTypeDropdownComponent } from '@osee/messaging/transports/dropdown/testing';
import { TransportTypeDropdownComponent } from '@osee/messaging/transports/dropdown';

describe('ImportComponent', () => {
	let component: ImportComponent;
	let fixture: ComponentFixture<ImportComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(ImportComponent, {
			add: {
				imports: [
					NgIf,
					NgFor,
					AsyncPipe,
					MatButtonModule,
					MatSelectModule,
					MessagingControlsMockComponent,
					MockTransportTypeDropdownComponent,
				],
			},
			remove: {
				imports: [
					MessagingControlsComponent,
					TransportTypeDropdownComponent,
				],
			},
		})
			.configureTestingModule({
				imports: [
					MatButtonModule,
					MatFormFieldModule,
					MatSelectModule,
					NoopAnimationsModule,
					ImportComponent,
				],
				providers: [
					{ provide: ImportService, useValue: importServiceMock },
					provideRouter([]),
				],
				declarations: [],
			})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ImportComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
