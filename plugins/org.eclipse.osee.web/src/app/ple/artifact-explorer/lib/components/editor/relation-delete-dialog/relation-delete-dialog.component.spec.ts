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
import { RelationDeleteDialogComponent } from './relation-delete-dialog.component';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';

describe('RelationDeleteDialogComponent', () => {
	let component: RelationDeleteDialogComponent;
	let fixture: ComponentFixture<RelationDeleteDialogComponent>;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [RelationDeleteDialogComponent, MatDialogModule],
			providers: [
				{
					provide: MAT_DIALOG_DATA,
					useValue: {},
				},
			],
		});
		fixture = TestBed.createComponent(RelationDeleteDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
