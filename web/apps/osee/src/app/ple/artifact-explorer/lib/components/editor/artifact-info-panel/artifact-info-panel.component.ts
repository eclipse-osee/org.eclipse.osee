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
import { BehaviorSubject } from 'rxjs';
import { artifactTab } from '../../../types/artifact-explorer';
import { ExpansionPanelComponent } from '@osee/shared/components';

@Component({
	selector: 'osee-artifact-info-panel',
	imports: [ExpansionPanelComponent],
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
