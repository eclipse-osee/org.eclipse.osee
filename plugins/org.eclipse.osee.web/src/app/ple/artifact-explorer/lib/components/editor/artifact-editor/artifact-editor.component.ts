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
import { CommonModule } from '@angular/common';
import { Component, OnChanges, Input, SimpleChanges } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { tab } from '../../../types/artifact-explorer.data';
import { ArtifactInfoPanelComponent } from '../artifact-info-panel/artifact-info-panel.component';
import { AttributesEditorPanelComponent } from '../attributes-editor-panel/attributes-editor-panel.component';
import { RelationsEditorPanelComponent } from '../relations-editor-panel/relations-editor-panel.component';

@Component({
	selector: 'osee-artifact-editor',
	standalone: true,
	imports: [
		CommonModule,
		RelationsEditorPanelComponent,
		ArtifactInfoPanelComponent,
		AttributesEditorPanelComponent,
	],
	templateUrl: './artifact-editor.component.html',
})
export class ArtifactEditorComponent implements OnChanges {
	@Input() tab!: tab;
	private _tab = new BehaviorSubject<tab>({
		artifact: {
			name: '',
			id: '0',
			typeId: '',
			typeName: '',
			attributes: [],
			editable: false,
		},
		branchId: '',
		viewId: '',
	});

	ngOnChanges(changes: SimpleChanges): void {
		if (
			changes.tab !== undefined &&
			changes.tab.previousValue !== changes.tab.currentValue &&
			changes.tab.currentValue !== undefined
		) {
			this._tab.next(changes.tab.currentValue);
		}
	}

	constructor() {}
}
