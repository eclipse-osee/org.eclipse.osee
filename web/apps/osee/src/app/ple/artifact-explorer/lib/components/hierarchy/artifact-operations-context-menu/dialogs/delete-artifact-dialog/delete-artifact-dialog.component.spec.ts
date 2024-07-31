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

import { DeleteArtifactDialogComponent } from './delete-artifact-dialog.component';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { operationTypeMock } from '../../../../../testing/artifact-explorer.data.mock';
import { artifactWithRelationsMock } from '@osee/artifact-with-relations/testing';

describe('DeleteArtifactDialogComponent', () => {
	let component: DeleteArtifactDialogComponent;
	let fixture: ComponentFixture<DeleteArtifactDialogComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [DeleteArtifactDialogComponent],
			providers: [
				{ provide: MatDialogRef, useValue: {} },
				{
					provide: MAT_DIALOG_DATA,
					useValue: {
						artifact: artifactWithRelationsMock,
						operationType: operationTypeMock,
					},
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(DeleteArtifactDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
