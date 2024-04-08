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
import { ArtifactExplorerSettingsDialogComponent } from './artifact-explorer-settings-dialog.component';
import { ArtifactExplorerPreferencesHttpService } from '../../../services/artifact-explorer-preferences-http.service';
import { artifactExplorerPreferencesHttpServiceMock } from '../../../testing/artifact-explorer-preferences-http.service.mock';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UserDataAccountService } from '@osee/auth';
import { userDataAccountServiceMock } from '@osee/auth/testing';

describe('ArtifactExplorerSettingsDialogComponent', () => {
	let component: ArtifactExplorerSettingsDialogComponent;
	let fixture: ComponentFixture<ArtifactExplorerSettingsDialogComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ArtifactExplorerSettingsDialogComponent],
			providers: [
				{
					provide: MAT_DIALOG_DATA,
					useValue: { artifactExplorerPanelLocation: false },
				},
				{
					provide: ArtifactExplorerPreferencesHttpService,
					useValue: artifactExplorerPreferencesHttpServiceMock,
				},
				{
					provide: UserDataAccountService,
					useValue: userDataAccountServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(
			ArtifactExplorerSettingsDialogComponent
		);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
