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

import { ArtifactSearchComponent } from './artifact-search.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('ArtifactSearchComponent', () => {
	let component: ArtifactSearchComponent;
	let fixture: ComponentFixture<ArtifactSearchComponent>;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [
				ArtifactSearchComponent,
				HttpClientTestingModule,
				BrowserAnimationsModule,
			],
		});
		fixture = TestBed.createComponent(ArtifactSearchComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
