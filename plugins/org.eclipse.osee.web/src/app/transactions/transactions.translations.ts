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
