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
import { ArtifactExplorerExpansionPanelComponent } from './artifact-explorer-expansion-panel.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('ArtifactExplorerExpansionPanelComponent', () => {
	let component: ArtifactExplorerExpansionPanelComponent;
	let fixture: ComponentFixture<ArtifactExplorerExpansionPanelComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				ArtifactExplorerExpansionPanelComponent,
				NoopAnimationsModule,
			],
		}).compileComponents();

		fixture = TestBed.createComponent(
			ArtifactExplorerExpansionPanelComponent
		);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
