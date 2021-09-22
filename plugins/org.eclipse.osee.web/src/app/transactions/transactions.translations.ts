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
const transformMatrix: any = {
  interfaceMinSimultaneity: 'Interface Minimum Simultaneity',
  interfaceMaxSimultaneity: 'Interface Maximum Simultaneity',
  transportType: 'Interface Transport Type',
  interfacePlatformType2sComplement: 'Interface Platform Type 2sComplement',
  interfacePlatformTypeValidRangeDescription:
    'Interface Platform Type Valid Range Desc',
};
export class TransactionTranslations {
  transform(value: string) {
    if (this.contains(value)) {
      return transformMatrix[value];
    }
    return value;
  }
  contains(value: string) {
    if (transformMatrix[value] !== undefined && transformMatrix[value] !== null) {
      return true;
    }
    return false;
  }
}
