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

import { ArtifactHierarchyRelationsComponent } from './artifact-hierarchy-relations.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('ArtifactHierarchyRelationsComponent', () => {
	let component: ArtifactHierarchyRelationsComponent;
	let fixture: ComponentFixture<ArtifactHierarchyRelationsComponent>;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [
				ArtifactHierarchyRelationsComponent,
				HttpClientTestingModule,
			],
		});
		fixture = TestBed.createComponent(ArtifactHierarchyRelationsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
