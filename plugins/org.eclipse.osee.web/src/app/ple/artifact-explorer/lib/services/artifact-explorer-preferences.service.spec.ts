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
import { TestBed } from '@angular/core/testing';
import { ArtifactExplorerPreferencesService } from './artifact-explorer-preferences.service';
import { ArtifactExplorerPreferencesHttpService } from './artifact-explorer-preferences-http.service';
import { artifactExplorerPreferencesHttpServiceMock } from '../testing/artifact-explorer-preferences-http.service.mock';
import { UserDataAccountService } from '@osee/auth';
import { userDataAccountServiceMock } from '@osee/auth/testing';

describe('UserPreferencesService', () => {
	let service: ArtifactExplorerPreferencesService;

	beforeEach(() => {
		TestBed.configureTestingModule({
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
		});
		service = TestBed.inject(ArtifactExplorerPreferencesService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
