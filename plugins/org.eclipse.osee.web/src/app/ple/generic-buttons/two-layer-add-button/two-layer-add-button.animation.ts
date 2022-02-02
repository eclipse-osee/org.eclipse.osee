/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { animate, AUTO_STYLE, keyframes, state, style, transition, trigger } from "@angular/animations";

export const addButtonIconTransition = trigger('addButton', [
    state('closed', style({ transform: 'rotate(0)' })),
    state('open', style({ transform: 'rotate(45deg)' })),
    transition('open <=> closed', animate('{{time}}ms {{delay}}ms cubic-bezier(0.42, 0.0, 0.58, 1)')),
])
export const slidingAddButtonAnim = trigger('slidingAddButton', [
    state('hidden',style({transform: 'translate3d(0, {{hiddenYTranslation}}, 0)',display:'block'}),{params:{hiddenYTranslation:'90px'}}),
    state('visible', style({transform: 'translate3d(0, {{visibleYTranslation}}, 0)',display:'block'}),{params:{visibleYTranslation:'-90px'}}),
    transition(
        'hidden=>*',
        animate('{{time}}ms {{delay}}ms',
            keyframes([
                style({ visibility:'hidden',transform: 'translate3d(0, {{hiddenYTranslation}}, 0)', easing: 'ease', offset: 0 }),
                style({ visibility:'visible',transform: 'translate3d(0, {{visibleYTranslation}}, 0)', easing: 'ease', offset: 1 },)
            ])
        )
    ),
    transition(
        'visible=>*',
        animate('{{time}}ms {{delay}}ms',
            keyframes([
                style({ visibility:'visible',transform: 'translate3d(0, {{visibleYTranslation}}, 0)', easing: 'ease', offset: 0 }),
                style({ visibility:'hidden',transform: 'translate3d(0, {{hiddenYTranslation}}, 0)', easing: 'ease', offset: 1 },)
            ])
        )
    )
])

export const addButtonHoverIconTransition =trigger('addHoverButton', [
    state('closed', style({ width:AUTO_STYLE,visibility:'visible',display:'inline-block' })),
    state('open', style({ width:0,visibility:'hidden' })),
    transition('closed=>open', animate('{{time}}ms {{delay}}ms', keyframes([style({ visibility: 'visible',width:AUTO_STYLE, easing: 'ease', offset: 0 }), style({ visibility:'hidden',width:0, easing: 'ease', offset: 1 })]))),
    transition('open=>closed', animate('{{time}}ms {{delay}}ms', keyframes([style({ visibility:'hidden',width:0, easing: 'ease', offset: 0 }), style({ visibility: 'visible',width:AUTO_STYLE, easing: 'ease', offset: 1 })]))),
])