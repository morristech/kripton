package kripton70.core;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import kripton70.contexts.BinderContext;

public class BinderParser {

	public BinderParser(BinderContext context, JsonParser parser, BinderType supportedFormat)
	{
		this.parser=parser;
		this.onlyText=supportedFormat.onlyText;
	}
	
	public JsonParser parser;
	
	public boolean onlyText;

	public boolean isOnlyText() {
		return onlyText;
	}

	public JsonToken getCurrentToken() {
		return parser.getCurrentToken();
	}

	public JsonToken nextToken() throws IOException {
		return parser.nextToken();		
	}

	public void skipChildren() throws IOException {
		parser.skipChildren();		
	}

	public String getCurrentName() throws IOException {
		return parser.getCurrentName();
	}

	public String getText() throws IOException {
		return parser.getText();
	}

	public Boolean getBooleanValue() throws IOException {
		return parser.getBooleanValue();
	}

	public Byte getByteValue() throws IOException {
		return parser.getByteValue();
	}

	public Double getDoubleValue() throws IOException {
		return parser.getDoubleValue();
	}

	public Float getFloatValue() throws IOException {
		return parser.getFloatValue();
	}

	public Integer getIntValue() throws IOException {
		return parser.getIntValue();
	}

	public Long getLongValue() throws IOException {
		return parser.getLongValue();
	}

	public Short getShortValue() throws IOException {
		return parser.getShortValue();
	}
}