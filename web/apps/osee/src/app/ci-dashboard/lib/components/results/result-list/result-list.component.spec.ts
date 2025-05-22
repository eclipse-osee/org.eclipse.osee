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
import { ResultListComponent } from './result-list.component';
import { CommonModule } from '@angular/common';
import { CiDetailsListService } from '../../../services/ci-details-list.service';
import { ciDetailsServiceMock } from '../../../testing/ci-details.service.mock';

describe('ResultListComponent', () => {
	let component: ResultListComponent;
	let fixture: ComponentFixture<ResultListComponent>;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [CommonModule, ResultListComponent],
			providers: [
				{
					provide: CiDetailsListService,
					useValue: ciDetailsServiceMock,
				},
			],
		});
		fixture = TestBed.createComponent(ResultListComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
