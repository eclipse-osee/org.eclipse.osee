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
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AsciidocRoutingModule } from './asciidoc-routing.module';
import { AsciidocEditorComponent } from './asciidoc-editor/asciidoc-editor.component';
import { ResizableSplitPaneCodeComponent } from './resizable-split-pane-code/resizable-split-pane-code.component';

import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { FormsModule } from '@angular/forms';
import { MarkdownModule } from 'ngx-markdown';
import { MatDividerModule } from '@angular/material/divider';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { BranchPickerComponent } from '@osee/shared/components';

@NgModule({
	declarations: [AsciidocEditorComponent, ResizableSplitPaneCodeComponent],
	imports: [
		CommonModule,
		AsciidocRoutingModule,
		MatInputModule,
		MatFormFieldModule,
		FormsModule,
		MatDividerModule,
		MatButtonModule,
		MatIconModule,
		MarkdownModule.forChild(),
		BranchPickerComponent,
	],
})
export class AsciidocModule {}
