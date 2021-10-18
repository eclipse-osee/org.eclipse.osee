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
import { ConvertSubMessageTitlesToStringPipe } from './convert-sub-message-titles-to-string.pipe';

describe('ConvertSubMessageTitlesToStringPipe', () => {
  it('create an instance', () => {
    const pipe = new ConvertSubMessageTitlesToStringPipe();
    expect(pipe).toBeTruthy();
  });
  it('should return value back if not in list', () => {
    const pipe = new ConvertSubMessageTitlesToStringPipe();
    expect(pipe.transform('abcdef')).toEqual('abcdef')
  })

  it('should return proper value if in list', () => {
    const pipe = new ConvertSubMessageTitlesToStringPipe();
    expect(pipe.transform('applicability')).toEqual('Applicability')
  })
});
