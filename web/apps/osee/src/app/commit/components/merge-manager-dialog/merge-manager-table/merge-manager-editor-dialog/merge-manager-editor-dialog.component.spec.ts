/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { MergeManagerEditorDialogComponent } from './merge-manager-editor-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { mergeDataMock } from '@osee/commit/testing';

describe('MergeManagerEditorDialogComponent', () => {
	let component: MergeManagerEditorDialogComponent;
	let fixture: ComponentFixture<MergeManagerEditorDialogComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [MergeManagerEditorDialogComponent, NoopAnimationsModule],
			providers: [
				{
					provide: MAT_DIALOG_DATA,
					useValue: mergeDataMock[0],
				},
				{ provide: MatDialogRef, useValue: {} },
			],
		}).compileComponents();

		fixture = TestBed.createComponent(MergeManagerEditorDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
