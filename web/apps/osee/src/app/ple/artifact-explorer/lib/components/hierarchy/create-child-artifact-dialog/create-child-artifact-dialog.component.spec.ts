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

import { CreateChildArtifactDialogComponent } from './create-child-artifact-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ArtifactExplorerHttpService } from '../../../services/artifact-explorer-http.service';
import { ArtifactExplorerHttpServiceMock } from '../../../testing/artifact-explorer-http.service.mock';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ArtifactService } from '../../../../../../shared/services/ple_aware/http/artifact.service';
import { artifactServiceMock } from '../../../../../../shared/services/ple_aware/http/artifact.service.mock';
import { FormDirective } from '@osee/shared/directives';
import { artifactTypeIconMock } from '@osee/shared/testing';

describe('CreateChildArtifactDialogComponent', () => {
	let component: CreateChildArtifactDialogComponent;
	let fixture: ComponentFixture<CreateChildArtifactDialogComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				CreateChildArtifactDialogComponent,
				BrowserAnimationsModule,
				FormDirective,
			],
			providers: [
				{ provide: MatDialogRef, useValue: {} },
				{
					provide: MAT_DIALOG_DATA,
					useValue: {
						name: '',
						artifactTypeId: '0',
						parentArtifactId: '1111',
						attributes: [],
						option: {
							name: 'create',
							icon: artifactTypeIconMock,
							excludedArtifactTypes: [],
						},
					},
				},
				{
					provide: ArtifactExplorerHttpService,
					useValue: ArtifactExplorerHttpServiceMock,
				},
				{
					provide: ArtifactService,
					useValue: artifactServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(CreateChildArtifactDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
