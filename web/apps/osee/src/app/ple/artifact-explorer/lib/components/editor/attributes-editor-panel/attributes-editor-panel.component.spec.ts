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
import { AttributesEditorPanelComponent } from './attributes-editor-panel.component';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { tab } from '../../../types/artifact-explorer';
import { artifactWithRelationsMock } from '@osee/artifact-with-relations/testing';
import { artifactWithRelations } from '@osee/artifact-with-relations/types';
import {
	HttpResourceRef,
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';
import { signal } from '@angular/core';

describe('AttributesEditorPanelComponent', () => {
	let component: AttributesEditorPanelComponent;
	let fixture: ComponentFixture<AttributesEditorPanelComponent>;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [AttributesEditorPanelComponent, BrowserAnimationsModule],
			providers: [
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
			],
		});

		// tab input
		const tabMock: tab = {
			tabId: '111',
			tabType: 'Artifact',
			tabTitle: '',
			artifact: artifactWithRelationsMock,
			branchId: '789',
			branchName: 'Some branch',
			viewId: '0',
		};

		// Minimal stub of the parent's shared resource: the component reads
		// artifactResource().value(); seed it with the mock artifact.
		const artifactResourceStub = {
			value: signal<artifactWithRelations | undefined>(
				artifactWithRelationsMock
			),
		} as unknown as HttpResourceRef<artifactWithRelations | undefined>;

		fixture = TestBed.createComponent(AttributesEditorPanelComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('tab', tabMock);
		fixture.componentRef.setInput('artifactResource', artifactResourceStub);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
