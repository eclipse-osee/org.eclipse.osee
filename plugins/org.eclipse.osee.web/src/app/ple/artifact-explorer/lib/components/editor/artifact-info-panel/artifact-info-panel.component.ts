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
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatExpansionModule } from '@angular/material/expansion';
import { BehaviorSubject } from 'rxjs';
import { tab } from '../../../types/artifact-explorer.data';

@Component({
	selector: 'osee-artifact-info-panel',
	standalone: true,
	imports: [CommonModule, MatExpansionModule],
	templateUrl: './artifact-info-panel.component.html',
})
export class ArtifactInfoPanelComponent {
	@Input() tab!: tab;

	// panel open/close state handling
	panelOpen = new BehaviorSubject<boolean>(false);
	togglePanel() {
		this.panelOpen.next(!this.panelOpen.value);
	}
}
