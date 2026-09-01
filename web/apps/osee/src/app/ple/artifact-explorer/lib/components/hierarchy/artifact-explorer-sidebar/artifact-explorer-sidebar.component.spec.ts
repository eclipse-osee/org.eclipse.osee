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

import { ArtifactExplorerSidebarComponent } from './artifact-explorer-sidebar.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import {
	BranchPickerStub,
	MockCurrentViewSelectorComponent,
} from '@osee/shared/components/testing';
import { ArtifactHierarchyPathService } from '../../../services/artifact-hierarchy-path.service';
import { artifactHierarchyPathServiceMock } from '../../../testing/artifact-hierarchy-path.service.mock';
import { CommonModule } from '@angular/common';
import { MatExpansionModule } from '@angular/material/expansion';
import { ArtifactHierarchyComponent } from '../artifact-hierarchy/artifact-hierarchy.component';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { MatIconModule } from '@angular/material/icon';
import { ArtifactHierarchyOptionsMockComponent } from '../artifact-hierarchy-options/artifact-hierarchy-oprions.component.mock';
import { ArtifactSearchMockComponent } from '../artifact-search-panel/artifact-search/artifact-search.component.mock';
import { CurrentBranchInfoService } from '@osee/shared/services';
import {
	BranchManagementStub,
	CurrentActionDropdownMockComponent,
	actionServiceMock,
	createActionServiceMock,
	currentActionServiceMock,
} from '@osee/configuration-management/testing';
import {
	ActionService,
	CreateActionService,
	CurrentActionService,
} from '@osee/configuration-management/services';
import { ExpansionPanelComponent } from '@osee/shared/components';
import { CurrentBranchInfoServiceMock } from '@osee/shared/testing';

describe('ArtifactExplorerSidebarComponent', () => {
	let component: ArtifactExplorerSidebarComponent;
	let fixture: ComponentFixture<ArtifactExplorerSidebarComponent>;

	beforeEach(() => {
		TestBed.overrideComponent(ArtifactExplorerSidebarComponent, {
			set: {
				imports: [
					CommonModule,
					MatExpansionModule,
					ArtifactHierarchyComponent,
					DragDropModule,
					ArtifactHierarchyOptionsMockComponent,
					MatIconModule,
					ArtifactSearchMockComponent,
					CurrentActionDropdownMockComponent,
					BranchPickerStub,
					MockCurrentViewSelectorComponent,
					ExpansionPanelComponent,
					BranchManagementStub,
				],
			},
		}).configureTestingModule({
			imports: [ArtifactExplorerSidebarComponent, NoopAnimationsModule],
			providers: [
				{
					provide: ArtifactHierarchyPathService,
					useValue: artifactHierarchyPathServiceMock,
				},
				{
					provide: CurrentBranchInfoService,
					useClass: CurrentBranchInfoServiceMock,
				},
				{
					provide: CurrentActionService,
					useValue: currentActionServiceMock,
				},
				{
					provide: CreateActionService,
					useValue: createActionServiceMock,
				},
				{ provide: ActionService, useValue: actionServiceMock },
			],
		});
		fixture = TestBed.createComponent(ArtifactExplorerSidebarComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('activeSection', 'hierarchy');
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
