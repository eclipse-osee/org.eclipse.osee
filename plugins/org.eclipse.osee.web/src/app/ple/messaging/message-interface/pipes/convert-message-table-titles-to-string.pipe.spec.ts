import { ConvertMessageTableTitlesToStringPipe } from './convert-message-table-titles-to-string.pipe';

describe('ConvertMessageTableTitlesToStringPipe', () => {
  it('create an instance', () => {
    const pipe = new ConvertMessageTableTitlesToStringPipe();
    expect(pipe).toBeTruthy();
  });
  it('should return value back if not in list', () => {
    const pipe = new ConvertMessageTableTitlesToStringPipe();
    expect(pipe.transform('abcdef')).toEqual('abcdef')
  })
});
