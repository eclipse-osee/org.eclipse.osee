/*********************************************************************
 * Copyright (c) 2024 Boeing
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

import { AdvancedSearchFormComponent } from './advanced-search-form.component';
import { ArtifactUiService } from '@osee/shared/services';
import { artifactUiServiceMock } from '@osee/shared/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('AdvancedSearchFormComponent', () => {
	let component: AdvancedSearchFormComponent;
	let fixture: ComponentFixture<AdvancedSearchFormComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [AdvancedSearchFormComponent, NoopAnimationsModule],
			providers: [
				{
					provide: ArtifactUiService,
					useValue: artifactUiServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(AdvancedSearchFormComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
