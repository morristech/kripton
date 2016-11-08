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
package com.abubusoft.kripton.processor.sqlite;

import static com.abubusoft.kripton.processor.core.reflect.TypeUtility.isNullable;
import static com.abubusoft.kripton.processor.core.reflect.TypeUtility.typeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import com.abubusoft.kripton.android.Logger;
import com.abubusoft.kripton.android.annotation.BindSqlDelete;
import com.abubusoft.kripton.android.annotation.BindSqlUpdate;
import com.abubusoft.kripton.common.Pair;
import com.abubusoft.kripton.common.StringUtil;
import com.abubusoft.kripton.processor.core.reflect.TypeUtility;
import com.abubusoft.kripton.processor.exceptions.InvalidMethodSignException;
import com.abubusoft.kripton.processor.exceptions.PropertyNotFoundException;
import com.abubusoft.kripton.processor.sqlite.SqlModifyBuilder.ModifyCodeGenerator;
import com.abubusoft.kripton.processor.sqlite.model.AnnotationAttributeType;
import com.abubusoft.kripton.processor.sqlite.model.SQLDaoDefinition;
import com.abubusoft.kripton.processor.sqlite.model.SQLEntity;
import com.abubusoft.kripton.processor.sqlite.model.SQLProperty;
import com.abubusoft.kripton.processor.sqlite.model.SQLiteModelMethod;
import com.abubusoft.kripton.processor.sqlite.transform.Transformer;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;

import android.content.ContentValues;

public class ModifyRawHelper implements ModifyCodeGenerator {

	public void generate(Elements elementUtils, MethodSpec.Builder methodBuilder, boolean updateMode, SQLiteModelMethod method, TypeName returnType) {
		SQLDaoDefinition daoDefinition = method.getParent();
		SQLEntity entity = daoDefinition.getEntity();

		// separate params used for update bean and params used in whereCondition
		// analyze whereCondition
		String whereCondition = null;

		if (updateMode) {
			whereCondition = method.getAnnotation(BindSqlUpdate.class).getAttribute(AnnotationAttributeType.ATTRIBUTE_WHERE);
		} else {
			whereCondition = method.getAnnotation(BindSqlDelete.class).getAttribute(AnnotationAttributeType.ATTRIBUTE_WHERE);
		}

		Pair<String, List<Pair<String, TypeMirror>>> where = SQLUtility.extractParametersFromString(whereCondition, method, daoDefinition.getColumnNameConverter(), entity);

		// defines which parameter is used like update field and which is used in where condition.
		List<Pair<String, TypeMirror>> methodParams = method.getParameters();
		List<Pair<String, TypeMirror>> updateableParams = new ArrayList<Pair<String, TypeMirror>>();
		List<Pair<String, TypeMirror>> whereParams = new ArrayList<Pair<String, TypeMirror>>();
		
		String name;

		for (Pair<String, TypeMirror> param : methodParams) {
			name=method.findParameterAliasByName(param.value0);
			
			if (where.value1.contains(new Pair<>(name, param.value1))) {
				whereParams.add(param);
			} else {
				updateableParams.add(param);
			}
		}

		if (updateMode) {
			// clear contentValues
			methodBuilder.addCode("$T contentValues=contentValues();\n", ContentValues.class);
			methodBuilder.addCode("contentValues.clear();\n");
			
			for (Pair<String, TypeMirror> item : updateableParams) {
				String resolvedParamName = method.findParameterAliasByName(item.value0);				
				SQLProperty property = entity.get(resolvedParamName);
				if (property == null)
					throw (new PropertyNotFoundException(method, resolvedParamName));

				// check same type
				TypeUtility.checkTypeCompatibility(method, item, property);
				
				if (TypeUtility.isNullable(method, item, property)) {
					methodBuilder.beginControlFlow("if ($L!=null)", item.value0);
				}
				
				// here it needed raw parameter name
				methodBuilder.addCode("contentValues.put($S, ", daoDefinition.getColumnNameConverter().convert(property.getName()));			
				//Transformer.java2ContentValues(methodBuilder, typeName(property.getElement().asType()),null , property);
				Transformer.java2ContentValues(methodBuilder, property.getElement().asType(),item.value0);
				methodBuilder.addCode(");\n");
				
				
				if (TypeUtility.isNullable(method, item, property)) {
					methodBuilder.nextControlFlow("else");
					methodBuilder.addCode("contentValues.putNull($S);\n", daoDefinition.getColumnNameConverter().convert(property.getName()));
					methodBuilder.endControlFlow();
				}
			}

			methodBuilder.addCode("\n");
		} else {
			if (updateableParams.size() > 0) {
				String separator = "";
				StringBuilder buffer = new StringBuilder();
				for (Pair<String, TypeMirror> item : updateableParams) {
					String resolvedParamName = method.findParameterAliasByName(item.value0);
					buffer.append(separator + resolvedParamName);
					separator = ", ";
				}
				// in DELETE can not be updated fields
				if (updateableParams.size() > 1) {
					throw (new InvalidMethodSignException(method, " parameters " + buffer.toString() + " are not used in where conditions"));
				} else {
					throw (new InvalidMethodSignException(method, " parameter " + buffer.toString() + " is not used in where conditions"));
				}
			}
		}

		// build where condition
		generateWhereCondition(methodBuilder,method, where);

		methodBuilder.addCode("\n");
		methodBuilder.addCode("\n");

		// generate javadoc
		String sqlModify = generateJavaDoc(daoDefinition, method, methodBuilder, updateMode, whereCondition, where, methodParams, updateableParams);

		if (updateMode) {
			if (daoDefinition.isLogEnabled()) {
				methodBuilder.addCode("$T.info($T.formatSQL(\"$L\"), (Object[])whereConditions);\n", Logger.class, StringUtil.class, sqlModify);
			}
			methodBuilder.addCode("int result = database().update($S, contentValues, $S, whereConditions);\n", daoDefinition.getEntity().getTableName(), where.value0);
		} else {
			if (daoDefinition.isLogEnabled()) {
				methodBuilder.addCode("$T.info($T.formatSQL(\"$L\"), (Object[])whereConditions);\n", Logger.class, StringUtil.class, sqlModify);
			}
			methodBuilder.addCode("int result = database().delete($S, $S, whereConditions);\n", daoDefinition.getEntity().getTableName(), where.value0);
		}

		// define return value
		if (returnType == TypeName.VOID) {

		} else if (isIn(returnType, Boolean.TYPE, Boolean.class)) {
			if (updateMode) {
				methodBuilder.addJavadoc("\n@return true if record is updated");
			} else {
				methodBuilder.addJavadoc("\n@return true if record is deleted");
			}
			methodBuilder.addCode("return result!=0;\n");
		} else if (isIn(returnType, Long.TYPE, Long.class, Integer.TYPE, Integer.class, Short.TYPE, Short.class)) {
			if (updateMode) {
				methodBuilder.addJavadoc("\n@return number of updated records\n");
			} else {
				methodBuilder.addJavadoc("\n@return number of deleted records\n");
			}
			methodBuilder.addCode("return result;\n");
		} else {
			// more than one listener found
			throw (new InvalidMethodSignException(method, "invalid return type"));
		}

	}

	/**
	 * @param daoDefinition
	 * @param method 
	 * @param methodBuilder
	 * @param updateMode
	 * @param whereCondition
	 * @param where
	 * @param methodParams
	 * @param updateableParams
	 */
	public String generateJavaDoc(SQLDaoDefinition daoDefinition,  SQLiteModelMethod method, MethodSpec.Builder methodBuilder, boolean updateMode, String whereCondition, Pair<String, List<Pair<String, TypeMirror>>> where, List<Pair<String, TypeMirror>> methodParams,
			List<Pair<String, TypeMirror>> updateableParams) {
		String sqlResult;
		StringBuilder buffer = new StringBuilder();
		StringBuilder bufferQuestion = new StringBuilder();

		String separator = "";
		for (Pair<String, TypeMirror> param : updateableParams) {
			String resolvedName=method.findParameterAliasByName(param.value0);
			buffer.append(separator + param.value0 + "=${" + resolvedName  + "}");
			bufferQuestion.append(separator + param.value0 + "='\"+StringUtil.checkSize(contentValues.get(\"" + daoDefinition.getColumnNameConverter().convert(resolvedName) + "\"))+\"'");

			separator = ", ";
		}

		// used for logging
		String whereForLogging = SQLUtility.replaceParametersWithQuestion(whereCondition, "%s");

		if (updateMode) {
			// generate sql query
			sqlResult = String.format("UPDATE %s SET %s WHERE %s", daoDefinition.getEntity().getTableName(), bufferQuestion.toString(), whereForLogging);

			methodBuilder.addJavadoc("<p>Update SQL:</p>\n");
			methodBuilder.addJavadoc("<pre>UPDATE $L SET $L WHERE $L</pre>\n", daoDefinition.getEntity().getTableName(), buffer.toString(), whereCondition);
		} else {
			// generate sql query
			sqlResult = String.format("DELETE %s WHERE %s", daoDefinition.getEntity().getTableName(), whereForLogging);

			methodBuilder.addJavadoc("<p>Delete query:</p>\n");
			methodBuilder.addJavadoc("<pre>DELETE $L WHERE $L</pre>\n", daoDefinition.getEntity().getTableName(), whereCondition);
		}

		if (methodParams.size() > 0) {
			methodBuilder.addJavadoc("\n");
			for (Pair<String, TypeMirror> param : methodParams) {
				methodBuilder.addJavadoc("@param $L", param.value0);
				if (where.value1.contains(param)) {
					methodBuilder.addJavadoc("\n\tused in where condition\n");
				} else {
					methodBuilder.addJavadoc("\n\tused as updated field\n");
				}
			}
		}

		return sqlResult;
	}

	/**
	 * @param methodBuilder
	 * @param method 
	 * @param where
	 */
	public void generateWhereCondition(MethodSpec.Builder methodBuilder, SQLiteModelMethod method, Pair<String, List<Pair<String, TypeMirror>>> where) {
		boolean nullable;
		
		methodBuilder.addCode("String[] whereConditions={");
		String separator = "";
		for (Pair<String, TypeMirror> item : where.value1) {
			String resolvedParamName = method.findParameterNameByAlias(item.value0);
			methodBuilder.addCode(separator);

			nullable = isNullable(typeName(item.value1));

			if (nullable) {
				methodBuilder.addCode("($L==null?null:", resolvedParamName);
			}
						
			// check for string conversion
			TypeUtility.beginStringConversion(methodBuilder, item.value1);				
			Transformer.java2ContentValues(methodBuilder, item.value1, resolvedParamName);
			// check for string conversion
			TypeUtility.endStringConversion(methodBuilder, item.value1);
			
			if (nullable) {
				methodBuilder.addCode(")");
			}

			separator = ", ";
		}
		methodBuilder.addCode("};");
	}

	static boolean isIn(TypeName value, Class<?>... classes) {
		for (Class<?> item : classes) {
			if (value.toString().equals(TypeName.get(item).toString())) {
				return true;
			}
		}

		return false;
	}

}
