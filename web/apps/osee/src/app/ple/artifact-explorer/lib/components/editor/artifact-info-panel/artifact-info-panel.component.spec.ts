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
import { ArtifactInfoPanelComponent } from './artifact-info-panel.component';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { tab } from '../../../types/artifact-explorer';
import { artifactWithRelationsMock } from '@osee/artifact-with-relations/testing';
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';

describe('ArtifactInfoPanelComponent', () => {
	let component: ArtifactInfoPanelComponent;
	let fixture: ComponentFixture<ArtifactInfoPanelComponent>;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [ArtifactInfoPanelComponent, BrowserAnimationsModule],
			providers: [
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
			],
		});

		// tab input
		const tabMock: tab = {
			tabId: '222',
			tabType: 'Artifact',
			tabTitle: '',
			artifact: artifactWithRelationsMock,
			branchId: '789',
			branchName: 'Some branch',
			viewId: '0',
		};

		fixture = TestBed.createComponent(ArtifactInfoPanelComponent);
		component = fixture.componentInstance;
		component.tab = tabMock;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
