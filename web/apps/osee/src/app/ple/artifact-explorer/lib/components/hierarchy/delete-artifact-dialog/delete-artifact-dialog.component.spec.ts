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
import { artifactContextMenuOptionMock } from '../../../testing/artifact-explorer.data.mock';
import { artifactMock } from '@osee/shared/testing';

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
						artifact: artifactMock,
						option: artifactContextMenuOptionMock,
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
