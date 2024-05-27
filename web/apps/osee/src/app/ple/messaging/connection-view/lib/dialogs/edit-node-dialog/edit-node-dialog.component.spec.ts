/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogModule,
	MatDialogRef,
} from '@angular/material/dialog';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

import { AsyncPipe, NgFor } from '@angular/common';
import { MockNewNodeFormComponent } from '@osee/messaging/connection-view/testing';
import { dialogRef, nodesMock } from '@osee/messaging/shared/testing';
import type { nodeData } from '@osee/messaging/shared/types';
import { EditNodeDialogComponent } from './edit-node-dialog.component';

describe('EditNodeDialogComponent', () => {
	let component: EditNodeDialogComponent;
	let fixture: ComponentFixture<EditNodeDialogComponent>;
	const dialogData: nodeData = nodesMock[0];

	beforeEach(async () => {
		await TestBed.overrideComponent(EditNodeDialogComponent, {
			set: {
				imports: [
					MatDialogModule,
					MatButtonModule,
					AsyncPipe,
					NgFor,
					FormsModule,
					MockNewNodeFormComponent,
				],
			},
		})
			.configureTestingModule({
				imports: [EditNodeDialogComponent],
				providers: [
					provideNoopAnimations(),
					{ provide: MatDialogRef, useValue: dialogRef },
					{ provide: MAT_DIALOG_DATA, useValue: dialogData },
				],
			})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(EditNodeDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
