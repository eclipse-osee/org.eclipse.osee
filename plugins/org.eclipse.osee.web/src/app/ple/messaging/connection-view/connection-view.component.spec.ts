/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { RouterTestingModule } from '@angular/router/testing';

import { ConnectionViewComponent } from './connection-view.component';
import { graphServiceMock } from './mocks/CurrentGraphService.mock';
import { CurrentGraphService } from './services/current-graph.service';
import { BaseDummy } from './testing/MockComponents/Base.mock';
import { GraphDummy } from './testing/MockComponents/Graph.mock';

describe('ConnectionViewComponent', () => {
	let component: ConnectionViewComponent;
	let fixture: ComponentFixture<ConnectionViewComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [RouterTestingModule],
			providers: [
				{ provide: CurrentGraphService, useValue: graphServiceMock },
			],
			declarations: [ConnectionViewComponent, BaseDummy, GraphDummy],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(ConnectionViewComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
