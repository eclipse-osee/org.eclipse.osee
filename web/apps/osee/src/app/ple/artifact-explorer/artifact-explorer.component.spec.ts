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
import { provideRouter } from '@angular/router';
import { ArtifactExplorerComponent } from './artifact-explorer.component';
import { ArtifactExplorerSidebarMockComponent } from './lib/components/hierarchy/artifact-explorer-sidebar/artifact-explorer-sidebar.component.mock';
import { ArtifactExplorerSidebarComponent } from './lib/components/hierarchy/artifact-explorer-sidebar/artifact-explorer-sidebar.component';
import { ArtifactExplorerPreferencesHttpService } from './lib/services/artifact-explorer-preferences-http.service';
import { artifactExplorerPreferencesHttpServiceMock } from './lib/testing/artifact-explorer-preferences-http.service.mock';
import { UserDataAccountService } from '@osee/auth';
import { userDataAccountServiceMock } from '@osee/auth/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { CurrentBranchInfoService } from '@osee/shared/services';
import { testBranchInfo } from '@osee/shared/testing';
import { of } from 'rxjs';

describe('ArtifactExplorerComponent', () => {
	let component: ArtifactExplorerComponent;
	let fixture: ComponentFixture<ArtifactExplorerComponent>;

	beforeEach(() => {
		TestBed.overrideComponent(ArtifactExplorerComponent, {
			add: {
				imports: [ArtifactExplorerSidebarMockComponent],
			},
			remove: {
				imports: [ArtifactExplorerSidebarComponent],
			},
		}).configureTestingModule({
			imports: [ArtifactExplorerComponent],
			providers: [
				provideRouter([]),
				{
					provide: ArtifactExplorerPreferencesHttpService,
					useValue: artifactExplorerPreferencesHttpServiceMock,
				},
				{
					provide: UserDataAccountService,
					useValue: userDataAccountServiceMock,
				},
				{
					provide: CurrentBranchInfoService,
					useValue: { currentBranch: of(testBranchInfo) },
				},
				provideNoopAnimations(),
			],
		});
		fixture = TestBed.createComponent(ArtifactExplorerComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
