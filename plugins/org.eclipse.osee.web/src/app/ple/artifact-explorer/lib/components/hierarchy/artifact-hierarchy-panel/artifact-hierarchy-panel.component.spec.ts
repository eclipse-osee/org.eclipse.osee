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

import { ArtifactHierarchyPanelComponent } from './artifact-hierarchy-panel.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { BranchPickerStub } from '@osee/shared/components/testing';
import {
	BranchPickerComponent,
	ViewSelectorComponent,
} from '@osee/shared/components';
import { ViewSelectorMockComponent } from '@osee/messaging/shared/testing';
import { ArtifactHierarchyPathService } from '../../../services/artifact-hierarchy-path.service';
import { artifactHierarchyPathServiceMock } from '../../../testing/artifact-hierarchy-path.service.mock';

describe('ArtifactHierarchyPanelComponent', () => {
	let component: ArtifactHierarchyPanelComponent;
	let fixture: ComponentFixture<ArtifactHierarchyPanelComponent>;

	beforeEach(() => {
		TestBed.overrideComponent(ArtifactHierarchyPanelComponent, {
			add: {
				imports: [BranchPickerStub, ViewSelectorMockComponent],
			},
			remove: {
				imports: [BranchPickerComponent, ViewSelectorComponent],
			},
		}).configureTestingModule({
			imports: [ArtifactHierarchyPanelComponent, NoopAnimationsModule],
			providers: [
				{
					provide: ArtifactHierarchyPathService,
					useValue: artifactHierarchyPathServiceMock,
				},
			],
		});
		fixture = TestBed.createComponent(ArtifactHierarchyPanelComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
