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
import { Component, signal } from '@angular/core';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { ArtifactSearchComponent } from './artifact-search/artifact-search.component';

@Component({
	selector: 'osee-artifact-search-panel',
	standalone: true,
	imports: [MatExpansionModule, MatIconModule, ArtifactSearchComponent],
	templateUrl: './artifact-search-panel.component.html',
})
export class ArtifactSearchPanelComponent {
	panelOpen = signal(false);
	togglePanel() {
		this.panelOpen.update((val) => !val);
	}
}
