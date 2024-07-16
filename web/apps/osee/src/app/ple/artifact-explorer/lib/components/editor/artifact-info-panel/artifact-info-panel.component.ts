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
import { NgClass } from '@angular/common';
import { Component, Input } from '@angular/core';
import { ExpandIconComponent } from '@osee/shared/components';
import { BehaviorSubject } from 'rxjs';
import { artifactTab } from '../../../types/artifact-explorer';
import { ArtifactExplorerExpansionPanelComponent } from '../../shared/artifact-explorer-expansion-panel/artifact-explorer-expansion-panel.component';

@Component({
	selector: 'osee-artifact-info-panel',
	standalone: true,
	imports: [
		NgClass,
		ArtifactExplorerExpansionPanelComponent,
		ExpandIconComponent,
	],
	templateUrl: './artifact-info-panel.component.html',
})
export class ArtifactInfoPanelComponent {
	@Input() tab!: artifactTab;

	// panel open/close state handling
	panelOpen = new BehaviorSubject<boolean>(false);
	togglePanel() {
		this.panelOpen.next(!this.panelOpen.value);
	}
}
