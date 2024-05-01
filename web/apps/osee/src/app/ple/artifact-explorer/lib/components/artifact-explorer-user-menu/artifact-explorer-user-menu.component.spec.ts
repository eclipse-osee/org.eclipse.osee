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
import ArtifactExplorerUserMenuComponent from './artifact-explorer-user-menu.component';
import { ArtifactExplorerPreferencesHttpService } from '../../services/artifact-explorer-preferences-http.service';
import { artifactExplorerPreferencesHttpServiceMock } from '../../testing/artifact-explorer-preferences-http.service.mock';
import { UserDataAccountService } from '@osee/auth';
import { userDataAccountServiceMock } from '@osee/auth/testing';

describe('ArtifactExplorerUserMenuComponent', () => {
	let component: ArtifactExplorerUserMenuComponent;
	let fixture: ComponentFixture<ArtifactExplorerUserMenuComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ArtifactExplorerUserMenuComponent],
			providers: [
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

		fixture = TestBed.createComponent(ArtifactExplorerUserMenuComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
