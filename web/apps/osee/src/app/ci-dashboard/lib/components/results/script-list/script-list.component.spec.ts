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

import { ScriptListComponent } from './script-list.component';
import { CommonModule } from '@angular/common';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { SplitStringPipe } from '@osee/shared/utils';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { CiDetailsService } from '../../../services/ci-details.service';
import { ciDetailsServiceMock } from '../../../testing/ci-details.service.mock';

describe('ScriptListComponent', () => {
	let component: ScriptListComponent;
	let fixture: ComponentFixture<ScriptListComponent>;

	beforeEach(() => {
		TestBed.overrideComponent(ScriptListComponent, {
			set: {
				imports: [
					CommonModule,
					MatPaginatorModule,
					MatTableModule,
					MatTooltipModule,
					SplitStringPipe,
				],
			},
		}).configureTestingModule({
			imports: [ScriptListComponent],
			providers: [
				{ provide: CiDetailsService, useValue: ciDetailsServiceMock },
				provideNoopAnimations(),
			],
		});
		fixture = TestBed.createComponent(ScriptListComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
