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
import { CommonModule } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterLink } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { CrossReferenceService } from '@osee/messaging/shared';
import {
	CrossReferenceServiceMock,
	dialogRef,
	MessagingControlsMockComponent,
} from '@osee/messaging/shared/testing';

import { CrossReferenceComponent } from './cross-reference.component';

describe('CrossReferenceComponent', () => {
	let component: CrossReferenceComponent;
	let fixture: ComponentFixture<CrossReferenceComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(CrossReferenceComponent, {
			set: {
				imports: [
					CommonModule,
					FormsModule,
					MessagingControlsMockComponent,
					MatButtonModule,
					MatFormFieldModule,
					MatIconModule,
					MatSelectModule,
					MatTableModule,
					MatTooltipModule,
					RouterLink,
				],
			},
		})
			.configureTestingModule({
				imports: [
					CrossReferenceComponent,
					RouterTestingModule,
					FormsModule,
					MatButtonModule,
					MatFormFieldModule,
					MatDialogModule,
					MatSelectModule,
					MatTableModule,
					NoopAnimationsModule,
				],
				providers: [
					{
						provide: CrossReferenceService,
						useValue: CrossReferenceServiceMock,
					},
					{ provide: MatDialogRef, useValue: dialogRef },
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(CrossReferenceComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
