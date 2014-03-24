package org.eclipse.osee.ote.core.log.record.json;

import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.module.SimpleModule;
import org.eclipse.osee.ote.core.log.record.TestRecord;

public class LogRecordModule extends SimpleModule {

	public LogRecordModule() {
		super("LogRecordModule", new Version(0, 0, 1, null));
	}

	@Override
	public void setupModule(SetupContext context) {
		context.setMixInAnnotations(LogRecord.class, MixIn.class);
	}

	abstract class MixIn {
		MixIn(@JsonProperty("Level") Level level, @JsonProperty("Message") String message) {
		};

		@JsonProperty
		abstract public Level getLevel();
		
		@JsonProperty
		abstract public String getMessage();
	}
}
