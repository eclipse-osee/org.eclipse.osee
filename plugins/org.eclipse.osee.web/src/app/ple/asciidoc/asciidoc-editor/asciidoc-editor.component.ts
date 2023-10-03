/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { BranchPickerComponent } from '@osee/shared/components';
import { tap } from 'rxjs';
import { ResizableSplitPaneCodeComponent } from '../resizable-split-pane-code/resizable-split-pane-code.component';
import { TextEditorUiService } from '../services/text-editor-ui.service';

@Component({
	selector: 'osee-asciidoc-editor',
	templateUrl: './asciidoc-editor.component.html',
	styles: [
		':host{ height: 94vh; min-height: calc(94vh - 10%); max-height: 94vh; width: 100vw; min-width: calc(100vw - 10%); display: inline-block;}',
	],
	standalone: true,
	imports: [BranchPickerComponent, ResizableSplitPaneCodeComponent],
})
export class AsciidocEditorComponent implements OnInit {
	constructor(
		private route: ActivatedRoute,
		private txtEdServe: TextEditorUiService
	) {}

	ngOnInit(): void {
		// uiserve list of behavior subjects
		// param map (observable, try to keep all things in obs format) map of parameters
		// tap accesses value of var to do side effect, but not a transformation on the var, use value to do something but not change it
		// have to subscribe in this case, ok because observable dies when route dies, do not have to manage
		this.route.paramMap
			.pipe(
				tap((paramMap) => {
					// default to empty if undefined (obs), aka string or empty
					this.txtEdServe.typeValue =
						(paramMap.get('branchType') as
							| 'working'
							| 'baseline'
							| '') || '';
					// named all setters as value, id is obs, id value is setter
					this.txtEdServe.idValue = paramMap.get('branchId') || '';

					this.txtEdServe.artifactIdValue =
						paramMap.get('artifactId') || '';
				})
			)
			.subscribe();
	}
}
export default AsciidocEditorComponent;
