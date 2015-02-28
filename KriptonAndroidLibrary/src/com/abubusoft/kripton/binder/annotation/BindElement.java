package com.abubusoft.kripton.binder.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * This annotation maps a POJO field to an XML/JSON element
 * 
 * @author bulldog
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BindElement {

	/**
	 * The name of the XML/JSON element
	 * 
	 * @return name
	 */
	public String name() default "";

	/**
	 * <dl>
	 * <dt>JSON</dt>
	 * <dd>non viene utilizzato</dd>
	 * <dt>XML</dt>
	 * <dd>Viene utilizzato per le liste da rendere persistente: se definito con
	 * una stringa diversa da "" il nome viene utilizzato per incapsulare i vari
	 * elementi.</dd>
	 * </dl>
	 * 
	 * @return
	 */
	public String elementName() default "";

	/**
	 * <dl>
	 * <dt>JSON</dt>
	 * <dd>non viene utilizzato</dd>
	 * <dt>XML</dt>
	 * <dd>Indicates if the string content of the field should be put in a CDATA
	 * container or not.</dd>
	 * </dl>
	 * 
	 * @return true or false
	 */
	public boolean data() default false;
	
}
