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
import { ArtifactHierarchyComponent } from './artifact-hierarchy.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BranchPickerStub } from '@osee/shared/components/testing';
import { BranchPickerComponent } from '@osee/shared/components';

describe('ArtifactHierarchyComponent', () => {
	let component: ArtifactHierarchyComponent;
	let fixture: ComponentFixture<ArtifactHierarchyComponent>;

	beforeEach(() => {
		TestBed.overrideComponent(ArtifactHierarchyComponent, {
			add: {
				imports: [BranchPickerStub],
			},
			remove: {
				imports: [BranchPickerComponent],
			},
		}).configureTestingModule({
			imports: [ArtifactHierarchyComponent, HttpClientTestingModule],
		});
		fixture = TestBed.createComponent(ArtifactHierarchyComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
