import { DisplayTruncatedStringWithFieldOverflowPipe } from './display-truncated-string-with-field-overflow.pipe';

describe('DisplayTruncatedStringWithFieldOverflowPipe', () => {
  it('create an instance', () => {
    const pipe = new DisplayTruncatedStringWithFieldOverflowPipe();
    expect(pipe).toBeTruthy();
  });

  it('create a string of length 15+3', () => {
    const pipe = new DisplayTruncatedStringWithFieldOverflowPipe();
    const result=pipe.transform("ldaj;fjasdjflkdajgddlagj;aljgdlfjalkejriopetopdoghapohgkldnvz,fjg",...[15])
    expect(result.length).toEqual(18);
  });

  it('create a string of length 10+3', () => {
    const pipe = new DisplayTruncatedStringWithFieldOverflowPipe();
    const result=pipe.transform("ldaj;fjasdjflkdajgddlagj;aljgdlfjalkejriopetopdoghapohgkldnvz,fjg")
    expect(result.length).toEqual(13);
  });

  it('create a string of length 11', () => {
    const pipe = new DisplayTruncatedStringWithFieldOverflowPipe();
    const result=pipe.transform("Hello World",15)
    expect(result.length).toEqual(11);
  });
});
