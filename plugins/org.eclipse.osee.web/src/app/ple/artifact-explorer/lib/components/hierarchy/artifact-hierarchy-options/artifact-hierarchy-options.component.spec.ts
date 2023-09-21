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

import { ArtifactHierarchyOptionsComponent } from './artifact-hierarchy-options.component';
import { ActivatedRoute } from '@angular/router';

describe('ArtifactHierarchyOptionsComponent', () => {
	let component: ArtifactHierarchyOptionsComponent;
	let fixture: ComponentFixture<ArtifactHierarchyOptionsComponent>;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [ArtifactHierarchyOptionsComponent],
			providers: [{ provide: ActivatedRoute, useValue: {} }],
		});
		fixture = TestBed.createComponent(ArtifactHierarchyOptionsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
