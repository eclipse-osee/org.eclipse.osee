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
import { NgClass } from '@angular/common';
import { Component, OnInit, input, signal } from '@angular/core';
import {
	MatExpansionPanel,
	MatExpansionPanelHeader,
	MatExpansionPanelTitle,
} from '@angular/material/expansion';
import { ExpandIconComponent } from '@osee/shared/components';

@Component({
	selector: 'osee-artifact-explorer-expansion-panel',
	standalone: true,
	imports: [
		MatExpansionPanel,
		MatExpansionPanelHeader,
		MatExpansionPanelTitle,
		NgClass,
		ExpandIconComponent,
	],
	templateUrl: './artifact-explorer-expansion-panel.component.html',
})
export class ArtifactExplorerExpansionPanelComponent implements OnInit {
	openDefault = input(false);
	title = input('');
	panelOpen = signal(false);

	ngOnInit(): void {
		this.panelOpen.set(this.openDefault());
	}

	// panel open/close state handling
	togglePanel() {
		this.panelOpen.set(!this.panelOpen());
	}
}
