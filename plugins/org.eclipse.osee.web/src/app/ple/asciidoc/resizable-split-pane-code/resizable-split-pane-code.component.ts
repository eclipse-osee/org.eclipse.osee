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
import { TextFieldModule } from '@angular/cdk/text-field';
import { AsyncPipe } from '@angular/common';
import {
	AfterViewInit,
	Component,
	ElementRef,
	OnDestroy,
	Renderer2,
	ViewChild,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MarkdownModule } from 'ngx-markdown';
import { BehaviorSubject } from 'rxjs';
import { debounceTime, distinctUntilChanged, tap } from 'rxjs/operators';
import { CurrentTextEditorService } from '../services/current-text-editor.service';
import { INSERTIONS } from '../types/constants/insertions';

@Component({
	selector: 'osee-resizable-split-pane-code',
	templateUrl: './resizable-split-pane-code.component.html',
	styleUrls: ['./resizable-split-pane-code.component.sass'],
	standalone: true,
	imports: [
		FormsModule,
		AsyncPipe,
		MatInputModule,
		MatButtonModule,
		MatIconModule,
		MatFormFieldModule,
		TextFieldModule,
		MatDividerModule,
		MarkdownModule,
	],
})
export class ResizableSplitPaneCodeComponent
	implements OnDestroy, AfterViewInit
{
	constructor(
		private renderer2: Renderer2,
		private curTxtEdit: CurrentTextEditorService
	) {}

	private _update = this.curTxtEdit.updateArtifact;

	// actually just realized, we can probably remove that tap, and just use that observable to populate both the markdown pane and the html pane
	// REMEMBER TO GET RID OF SUBSCRIPTION IN ON DESTROY!!!!!!!!
	mdContent$ = this.curTxtEdit.mdContent
		.pipe(tap((response) => this.updateText(response)))
		.subscribe();

	userInputUpdate$ = this.curTxtEdit.userInput;

	mdRightinput$ = new BehaviorSubject<string>('');

	htmlRightInput$ = this.userInputUpdate$.pipe(
		debounceTime(500),
		distinctUntilChanged()
	);

	@ViewChild('resizer') resizerRef!: ElementRef;
	@ViewChild('container__left') container__leftRef!: ElementRef;
	@ViewChild('container__right') container__rightRef!: ElementRef;
	@ViewChild('body') bodyRef!: ElementRef;

	private unlistenSlideMouseDown!: () => void;
	private unlistenSlideMouseMoving!: () => void;
	private unlistenSlideMouseUp!: () => void;

	private oldCursorX!: number;
	private oldLeftWidth!: number;

	ngAfterViewInit(): void {
		const resizerEl = this.resizerRef.nativeElement;

		const container__leftEL = this.container__leftRef.nativeElement;
		const container__rightEL = this.container__rightRef.nativeElement;
		const bodyEl = this.bodyRef.nativeElement;

		this.unlistenSlideMouseDown = this.renderer2.listen(
			resizerEl,
			'mousedown',
			(event) => {
				this.oldCursorX = event.clientX;
				this.oldLeftWidth = container__leftEL.offsetWidth;

				// prevent cursor style flickering
				this.renderer2.setStyle(resizerEl, 'cursor', 'col-resize');
				this.renderer2.setStyle(bodyEl, 'cursor', 'col-resize');

				// prevent pointer and select (highlighting words etc.)
				this.renderer2.setStyle(
					container__leftEL,
					'userSelect',
					'none'
				);
				this.renderer2.setStyle(
					container__leftEL,
					'pointerEvents',
					'none'
				);

				this.renderer2.setStyle(
					container__rightEL,
					'userSelect',
					'none'
				);
				this.renderer2.setStyle(
					container__rightEL,
					'pointerEvents',
					'none'
				);

				this.unlistenSlideMouseMoving = this.renderer2.listen(
					'document',
					'mousemove',
					(event) => {
						const dx = event.clientX - this.oldCursorX;

						const newLeftWidth =
							((this.oldLeftWidth + dx) * 100) /
							resizerEl.parentNode.offsetWidth;

						this.renderer2.setStyle(
							container__leftEL,
							'width',
							newLeftWidth + '%'
						);
					}
				);

				this.unlistenSlideMouseUp = this.renderer2.listen(
					'document',
					'mouseup',
					() => {
						this.renderer2.removeStyle(resizerEl, 'cursor');
						this.renderer2.removeStyle(bodyEl, 'cursor');

						this.renderer2.removeStyle(
							container__leftEL,
							'userSelect'
						);
						this.renderer2.removeStyle(
							container__leftEL,
							'pointerEvents'
						);

						this.renderer2.removeStyle(
							container__rightEL,
							'userSelect'
						);
						this.renderer2.removeStyle(
							container__rightEL,
							'pointerEvents'
						);

						this.unlistenSlideMouseMoving();
						this.unlistenSlideMouseUp();
					}
				);
			}
		);
	}

	ngOnDestroy(): void {
		this.unlistenSlideMouseDown();

		this.mdContent$.unsubscribe();
	}

	updateText(value: string) {
		this.curTxtEdit.userInputValue = value;
		this.mdRightinput$.next(value);
	}

	makeInsertion(insertionKey: string) {
		const insertedText = INSERTIONS.get(insertionKey);
		const previousValue = this.mdRightinput$.value;

		const newValue = previousValue + insertedText;

		this.mdRightinput$.next(newValue);
		this.updateText(newValue);
	}

	save() {
		this._update.subscribe();
	}
}
