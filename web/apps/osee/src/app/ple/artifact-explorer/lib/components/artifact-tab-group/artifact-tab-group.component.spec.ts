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

import { ArtifactTabGroupComponent } from './artifact-tab-group.component';
import { CurrentBranchInfoService } from '@osee/shared/services';
import { testBranchInfo } from '@osee/shared/testing';
import { of } from 'rxjs';

describe('ArtifactTabGroupComponent', () => {
	let component: ArtifactTabGroupComponent;
	let fixture: ComponentFixture<ArtifactTabGroupComponent>;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [ArtifactTabGroupComponent],
			providers: [
				{
					provide: CurrentBranchInfoService,
					useValue: { currentBranch: of(testBranchInfo) },
				},
			],
		});
		fixture = TestBed.createComponent(ArtifactTabGroupComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
