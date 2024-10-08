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
import { ArtifactEditorComponent } from './artifact-editor.component';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { tab } from '../../../types/artifact-explorer';
import { artifactWithRelationsMock } from '@osee/artifact-with-relations/testing';
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';

describe('ArtifactEditorComponent', () => {
	let component: ArtifactEditorComponent;
	let fixture: ComponentFixture<ArtifactEditorComponent>;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [ArtifactEditorComponent, BrowserAnimationsModule],
			providers: [
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
			],
		});

		// tab input
		const tabMock: tab = {
			tabId: '333',
			tabType: 'Artifact',
			tabTitle: '',
			artifact: artifactWithRelationsMock,
			branchId: '789',
			viewId: '0',
		};

		fixture = TestBed.createComponent(ArtifactEditorComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('tab', tabMock);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
