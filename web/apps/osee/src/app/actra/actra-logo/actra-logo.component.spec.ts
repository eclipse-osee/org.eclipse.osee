/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import ActraLogoComponent from './actra-logo.component';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

describe('ActraLogoComponent', () => {
	let component: ActraLogoComponent;
	let fixture: ComponentFixture<ActraLogoComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ActraLogoComponent],
			providers: [
				{
					provide: ActivatedRoute,
					useValue: {
						queryParamMap: of(new Map<string, string>()),
					},
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(ActraLogoComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
