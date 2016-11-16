package kripton70.typeconverters;

import com.fasterxml.jackson.core.JsonToken;

import kripton70.core.BinderSerializer;
import kripton70.persistence.JacksonParser;

public class LongConverter implements TypeConverter<Long> {

	@Override
	public Long parse(JacksonParser parser) {
		if (parser.getCurrentToken() == JsonToken.VALUE_NULL) {
			return null;
		} else if (parser.onlyText){
			return Long.valueOf(parser.getText());
		} else {
			return parser.getLongValue();
		}
	}

	@Override
	public void serialize(BinderSerializer generator, boolean writeFieldNameForObject, String fieldName, Long value) {
		if (writeFieldNameForObject)
		generator.writeFieldName(fieldName);
		generator.writeNumber(value);
	}

}
