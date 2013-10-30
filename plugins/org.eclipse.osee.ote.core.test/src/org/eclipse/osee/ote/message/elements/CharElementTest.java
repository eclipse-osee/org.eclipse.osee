/*
 * Created on Oct 29, 2013
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.message.elements;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.elements.CharElement;
import org.eclipse.osee.ote.message.interfaces.ITestEnvironmentMessageSystemAccessor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CharElementTest {

   private class TestMessage extends Message<ITestEnvironmentMessageSystemAccessor, MessageData, TestMessage> {
      public TestMessage(String name, int defaultByteSize, int defaultOffset, boolean isScheduled, int phase, double rate) {
         super(name, defaultByteSize, defaultOffset, isScheduled, phase, rate);
      }
   }

   @Mock
   private TestMessage message;
   @Mock
   private MessageData msgData;
   private final byte[] bytes = new byte[5];
   private final MemoryResource memoryResource = new MemoryResource(bytes, 0, bytes.length);

   @Before
   public void before() {
      MockitoAnnotations.initMocks(this);
      when(message.getData()).thenReturn(bytes);
      when(message.getName()).thenReturn("MSG");
      when(msgData.getMem()).thenReturn(memoryResource);
   }

   @Test
   public void parseAndSetWithByteAlignedElementTest() throws Exception {
      CharElement sut = new CharElement(message, "TEST", msgData, 0, 0, 7);
      assertEquals("Start with all zeros in the byte array", "[0, 0, 0, 0, 0]",
         Arrays.toString(memoryResource.getData()));
      sut.parseAndSet(null, " ");
      assertEquals("getValue should return space character", ' ', sut.getValue().charValue());
      sut.parseAndSet(null, "A");
      assertEquals("getValue should return 'A'", 'A', sut.getValue().charValue());
      sut.parseAndSet(null, "");
      assertEquals("getValue should return null character", '\0', sut.getValue().charValue());
      sut.parseAndSet(null, "FOO");
      assertEquals("Decimal ASCII values for FOO", "[70, 79, 79, 0, 0]", Arrays.toString(memoryResource.getData()));
      assertEquals("FOO should be returned by the getString method", "FOO", sut.getString(null, 5));
   }

   @Test
   public void parseAndSetWithNonByteAlignedElementTest() throws Exception {
      CharElement sut = new CharElement(message, "TEST", msgData, 0, 1, 7);
      assertEquals("Start with all zeros in the byte array", "[0, 0, 0, 0, 0]",
         Arrays.toString(memoryResource.getData()));
      Arrays.fill(bytes, (byte) 0xFF);
      sut.parseAndSet(null, " ");
      assertEquals("getValue should return space character", ' ', sut.getValue().charValue());
      sut.parseAndSet(null, "A");
      assertEquals("getValue should return 'A'", 'A', sut.getValue().charValue());
      sut.parseAndSet(null, "");
      assertEquals("getValue should return null character", '\0', sut.getValue().charValue());
      sut.parseAndSet(null, "FOO");
      assertEquals("Unfortunately we tromple the spare bit data", "[70, 79, 79, -1, -1]",
         Arrays.toString(memoryResource.getData()));
   }

   @Test(expected = IllegalArgumentException.class)
   public void parseAndSetOverflowExceptionTest() throws Exception {
      CharElement sut = new CharElement(message, "TEST", msgData, 0, 0, 7);
      sut.parseAndSet(null, "TOOLONG");
   }
}
