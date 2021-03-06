/*******************************************************************************
 * Copyright 2015, 2017 Francesco Benincasa.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.abubusoft.kripton.processor.bind.transform;


/**
 * Transformer between a string and a Java Boolean object
 * 
 */
class BooleanBindTransform extends AbstractPrimitiveBindTransform {

	public BooleanBindTransform(boolean nullable) {
		super(nullable);
		
		PRIMITIVE_UTILITY_TYPE="Boolean";
		
		XML_TYPE = "Boolean";
		
		if (!nullable)
		{
			DEFAULT_VALUE="(boolean)false";
		}
		
		JSON_TYPE = "Boolean";
		JSON_PARSER_METHOD="getBooleanValue";
		
		XML_ATTRIBUTE_METHOD_PRE="Boolean";
		XML_ATTRIBUTE_METHOD_POST="";
		
	}
}
