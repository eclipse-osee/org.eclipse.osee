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

import WorldComponent from './world.component';

describe('WorldComponent', () => {
	let component: WorldComponent;
	let fixture: ComponentFixture<WorldComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [WorldComponent],
		}).compileComponents();

		fixture = TestBed.createComponent(WorldComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
