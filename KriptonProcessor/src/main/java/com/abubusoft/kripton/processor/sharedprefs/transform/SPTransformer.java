/*******************************************************************************
 * Copyright 2015, 2016 Francesco Benincasa.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.abubusoft.kripton.processor.sharedprefs.transform;

import static com.abubusoft.kripton.processor.core.reflect.TypeUtility.typeName;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Time;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import javax.lang.model.type.TypeMirror;

import com.abubusoft.kripton.processor.core.ModelType;
import com.abubusoft.kripton.processor.core.reflect.TypeUtility;
import com.abubusoft.kripton.processor.sharedprefs.model.PrefProperty;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

/**
 * Transformer for java primitive types and frequently used java types
 * 
 * @author bulldog
 *
 */
public abstract class SPTransformer {

	/**
	 * cache for transform
	 */
	private static final Map<TypeName, SPTransform> cache = new ConcurrentHashMap<TypeName, SPTransform>();


	/**
	 * Register custom transformable for a Java primitive type or a frequently
	 * used Java type.
	 * 
	 * @param type
	 *            a Java primitive type or a frequently used Java type.
	 * @param transform
	 *            a class implementing @see
	 *            org.abubu.elio.binder.transform.Transformable interface.
	 */
	public static void register(TypeName type, SPTransform transform) {
		cache.put(type, transform);
	}

	/**
	 * Get transformer for type
	 * 
	 * @return transform
	 */
	public static SPTransform lookup(PrefProperty property) {
		TypeMirror typeMirror=property.getElement().asType();
		
		TypeName typeName;

		if (typeMirror instanceof ModelType) {
			typeName = ((ModelType) typeMirror).getName();
		} else {
			typeName = typeName(typeMirror);
		}

		return lookup(typeName);
	}

	/**
	 * Get transformer for type
	 * 
	 * @param typeName
	 * @return transform
	 */
	public static SPTransform lookup(TypeName typeName) {
		SPTransform transform = cache.get(typeName);

		if (transform != null) {
			return transform;
		}

		transform = getTransform(typeName);
		if (transform != null) {
			cache.put(typeName, transform);
		}

		return transform;
	}

	private static SPTransform getTransform(TypeName typeName) {				
		if (typeName.isPrimitive()) {
			return getPrimitiveTransform(typeName);
		}

		if (typeName instanceof ArrayTypeName) {
			ArrayTypeName typeNameArray = (ArrayTypeName) typeName;
			TypeName componentTypeName = typeNameArray.componentType;

			if (TypeUtility.isSameType(componentTypeName, Byte.TYPE.toString())) {
				return new ByteArrayTransform();
			} else { 
				return new ArrayTransform(componentTypeName, componentTypeName.isPrimitive());
			} 
		} else if (typeName instanceof ParameterizedTypeName) {
			ParameterizedTypeName parameterizedTypeName = (ParameterizedTypeName) typeName;
			if (TypeUtility.isList(parameterizedTypeName)) {
				return new ListTransformation(parameterizedTypeName);
			}
		}

		String name = typeName.toString();

		if (name.startsWith("java.lang")) {
			return getLanguageTransform(typeName);
		}

		if (name.startsWith("java.util")) {
			return getUtilTransform(typeName);
		}

		if (name.startsWith("java.math")) {
			return getMathTransform(typeName);
		}

		if (name.startsWith("java.net")) {
			return getNetTransform(typeName);
		}

		if (name.startsWith("java.sql")) {
			return getSqlTransform(typeName);
		}
				
		return new BindObjectTransform();
	}

	private static SPTransform getSqlTransform(TypeName typeName) {
		if (Time.class.getName().equals(typeName.toString())) {
			return new TimeTransform();
		}

		return null;
	}

	private static SPTransform getNetTransform(TypeName typeName) {
		if (URL.class.getName().equals(typeName.toString())) {
			return new UrlTransform();
		}

		return null;
	}

	private static SPTransform getMathTransform(TypeName typeName) {
		if (BigDecimal.class.getName().equals(typeName.toString())) {
			return new BigDecimalTransform();
		} else if (BigInteger.class.getName().equals(typeName.toString())) {
			return new BigIntegerTransform();
		}

		return null;
	}

	/**
	 * Get Java primitive type Transformable
	 * 
	 * @param type
	 * @return
	 */
	private static SPTransform getPrimitiveTransform(TypeName type) {

		if (Integer.TYPE.toString().equals(type.toString())) {
			return new IntegerTransform(false);
		}
		if (Boolean.TYPE.toString().equals(type.toString())) {
			return new BooleanTransform(false);
		}
		if (Long.TYPE.toString().equals(type.toString())) {
			return new LongTransform(false);
		}
		if (Double.TYPE.toString().equals(type.toString())) {
			return new DoubleTransform(false);
		}
		if (Float.TYPE.toString().equals(type.toString())) {
			return new FloatTransform(false);
		}
		if (Short.TYPE.toString().equals(type.toString())) {
			return new ShortTransform(false);
		}
		if (Byte.TYPE.toString().equals(type.toString())) {
			return new ByteTransform(false);
		}
		if (Character.TYPE.toString().equals(type.toString())) {
			return new CharacterTransform(false);
		}
		return null;
	}

	/**
	 * Get Java primitive wrapping type Transformable
	 * 
	 * @param type
	 * @return
	 */
	private static SPTransform getLanguageTransform(TypeName type) {
		String typeName = type.toString();
		
		if (Integer.class.getCanonicalName().equals(typeName)) {
			return new IntegerTransform(true);
		}
		if (Boolean.class.getCanonicalName().equals(typeName)) {
			return new BooleanTransform(true);
		}
		if (Long.class.getCanonicalName().equals(typeName)) {
			return new LongTransform(true);
		}
		if (Double.class.getCanonicalName().equals(typeName)) {
			return new DoubleTransform(true);
		}
		if (Float.class.getCanonicalName().equals(typeName)) {
			return new FloatTransform(true);
		}
		if (Short.class.getCanonicalName().equals(typeName)) {
			return new ShortTransform(true);
		}
		if (Byte.class.getCanonicalName().equals(typeName)) {
			return new ByteTransform(true);
		}
		if (Character.class.getCanonicalName().equals(typeName)) {
			return new CharacterTransform(true);
		}
		if (String.class.getCanonicalName().equals(typeName)) {
			return new StringTransform();
		}
		return null;
	}

	/**
	 * Get java.util type Transformable
	 * 
	 * @param type
	 * @return
	 */

	private static SPTransform getUtilTransform(TypeName type) {
		String typeName = type.toString();

		// Integer.class.getCanonicalName().equals(typeName)
		if (Date.class.getCanonicalName().equals(typeName)) {
			return new DateTransform();
		}
		if (Locale.class.getCanonicalName().equals(typeName)) {
			return new LocaleTransform();
		}
		if (Currency.class.getCanonicalName().equals(typeName)) {
			return new CurrencyTransform();
		}
		if (Calendar.class.getCanonicalName().equals(typeName)) {
			return new CalendarTransform();
		}
		if (TimeZone.class.getCanonicalName().equals(typeName)) {
			return new TimeZoneTransform();
		}
		return null;
	}



}
