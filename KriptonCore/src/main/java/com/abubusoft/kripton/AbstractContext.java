package com.abubusoft.kripton;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.abubusoft.kripton.exception.KriptonRuntimeException;
import com.abubusoft.kripton.exception.NoSuchMapperException;
import com.abubusoft.kripton.persistence.ParserWrapper;
import com.abubusoft.kripton.persistence.SerializerWrapper;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.io.SegmentedStringWriter;
import com.fasterxml.jackson.core.util.BufferRecycler;

public abstract class AbstractContext implements BinderContext {
	
	@SuppressWarnings("rawtypes")
	static final Map<Class, BinderMapper> OBJECT_MAPPERS = new ConcurrentHashMap<>();
	
	static final ThreadLocal<BufferRecycler> buffer=new ThreadLocal<BufferRecycler>() {
		
	};

	@SuppressWarnings("unchecked")
	public static <E, M extends BinderMapper<E>> M getMapper(Class<E> cls) {
		M mapper = (M) OBJECT_MAPPERS.get(cls);
		if (mapper == null) {
			// The only way the mapper wouldn't already be loaded into
			// OBJECT_MAPPERS is if it was compiled separately, but let's handle
			// it anyway
			String beanClassName = cls.getName();
			String mapperClassName = cls.getName() + KriptonBinder.MAPPER_CLASS_SUFFIX;
			try {
				Class<E> mapperClass = (Class<E>) Class.forName(mapperClassName);
				mapper = (M) mapperClass.newInstance();
				// mapper.
				OBJECT_MAPPERS.put(cls, mapper);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new KriptonRuntimeException(
						String.format("Class '%s' does not exist. Does '%s' have @BindType annotation?", mapperClassName,
								beanClassName));
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return mapper;
	}
	
	/**
	 * Returns a JsonMapper for a given class that has been annotated
	 * with @JsonObject.
	 *
	 * @param cls
	 *            The class for which the JsonMapper should be fetched.
	 */
	public static <T, M extends BinderMapper<T>> M mapperFor(Class<T> cls) throws NoSuchMapperException {
		M mapper = getMapper(cls);

		if (mapper == null) {
			throw new NoSuchMapperException(cls);
		} else {
			return mapper;
		}
	}

	public static <E, M extends BinderMapper<E>> M mapperFor(ParameterizedType type) throws NoSuchMapperException {
		@SuppressWarnings("unchecked")
		M mapper = getMapper((Class<E>) type.getActualTypeArguments()[0]);
		if (mapper == null) {
			throw new NoSuchMapperException(type.getRawType());
		} else {
			return mapper;
		}
	}

	@Override
	public <E> E parse(byte[] source, Class<E> objectClazz) {
		try (ParserWrapper parserWrapper = createParser(source)) {
			E result = mapperFor(objectClazz).parse(this, parserWrapper);
			return result;
		} catch(Exception e)
		{
			e.printStackTrace();
			throw new KriptonRuntimeException(e);
		}
	}

	@Override
	public <E> E parse(File source, Class<E> objectClazz) {
		try (ParserWrapper parserWrapper = createParser(source)) {
			E result = mapperFor(objectClazz).parse(this, parserWrapper);
			return result;
		} catch(Exception e)
		{
			e.printStackTrace();
			throw new KriptonRuntimeException(e);
		}
	}

	@Override
	public <E> E parse(InputStream source, Class<E> objectClazz) {
		try (ParserWrapper parserWrapper = createParser(source)) {
			E result = mapperFor(objectClazz).parse(this, parserWrapper);
			return result;
		} catch(Exception e)
		{
			e.printStackTrace();
			throw new KriptonRuntimeException(e);
		}
	}

	@Override
	public <E> E parse(Reader source, Class<E> objectClazz) {
		try (ParserWrapper parserWrapper = createParser(source)) {
			E result = mapperFor(objectClazz).parse(this, parserWrapper);
			return result;
		} catch(Exception e)
		{
			e.printStackTrace();
			throw new KriptonRuntimeException(e);
		}
	}

	@Override
	public <E> E parse(String source, Class<E> objectClazz) {
		try (ParserWrapper parserWrapper = createParser(source)) {
			E result = mapperFor(objectClazz).parse(this, parserWrapper);
			return result;
		} catch(Exception e)
		{
			e.printStackTrace();
			throw new KriptonRuntimeException(e);
		}
	}

	@Override
	public <L extends Collection<E>, E> L parseCollection(L collection, Class<E> type, byte[] source) {
		if (collection == null || type == null)
			return null;

		try (ParserWrapper parserWrapper = createParser(source)) {
			L result = mapperFor(type).parseCollection(this, parserWrapper, collection);
			return result;
		} catch(Exception e)
		{
			e.printStackTrace();
			throw new KriptonRuntimeException(e);
		}
	}

	@Override
	public <L extends Collection<E>, E> L parseCollection(L collection, Class<E> type, InputStream source) {
		if (collection == null || type == null)
			return null;

		try (ParserWrapper parserWrapper = createParser(source)) {
			L result = mapperFor(type).parseCollection(this, parserWrapper, collection);
			return result;
		} catch(Exception e)
		{
			e.printStackTrace();
			throw new KriptonRuntimeException(e);
		}
	}

	@Override
	public <L extends Collection<E>, E> L parseCollection(L collection, Class<E> type, Reader source) {
		if (collection == null || type == null)
			return null;

		try (ParserWrapper parserWrapper = createParser(source)) {
			L result = mapperFor(type).parseCollection(this, parserWrapper, collection);
			return result;
		} catch(Exception e)
		{
			e.printStackTrace();
			throw new KriptonRuntimeException(e);
		}
	}

	@Override
	public <L extends Collection<E>, E> L parseCollection(L collection, Class<E> type, String source) {
		if (collection == null || type == null)
			return null;

		try (ParserWrapper parserWrapper = createParser(source)) {
			L result = mapperFor(type).parseCollection(this, parserWrapper, collection);
			return result;
		} catch(Exception e)
		{
			e.printStackTrace();
			throw new KriptonRuntimeException(e);
		}
	}

	@Override
	public <E> List<E> parseList(Class<E> type, byte[] source) {
		return parseCollection(new ArrayList<E>(), type, source);
	}

	@Override
	public <E> List<E> parseList(Class<E> type, InputStream source) {
		return parseCollection(new ArrayList<E>(), type, source);
	}

	@Override
	public <E> List<E> parseList(Class<E> type, Reader source) {
		return parseCollection(new ArrayList<E>(), type, source);
	}

	@Override
	public <E> List<E> parseList(Class<E> type, String source) {
		return parseCollection(new ArrayList<E>(), type, source);
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public <E> String serialize(E object) {		
		if (object == null)
			return null;

		SegmentedStringWriter source=new SegmentedStringWriter(buffer.get());
		try (SerializerWrapper serializer = createSerializer(source)) {
			mapperFor((Class<E>) object.getClass()).serialize(this, serializer, object);				
		} catch(Exception e)
		{
			e.printStackTrace();
			throw new KriptonRuntimeException(e);
		}
		
		return source.getAndClear();

	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> void serialize(E object, File source) {
		if (object == null)
			return;

		try (SerializerWrapper serializer = createSerializer(source)) {
			mapperFor((Class<E>) object.getClass()).serialize(this, serializer, object);
		} catch(Exception e)
		{
			e.printStackTrace();
			throw new KriptonRuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> void serialize(E object, OutputStream source) {
		if (object == null)
			return;

		try (SerializerWrapper serializer = createSerializer(source)) {
			mapperFor((Class<E>) object.getClass()).serialize(this, serializer, object);
		} catch(Exception e)
		{
			e.printStackTrace();
			throw new KriptonRuntimeException(e);
		}
	}

	@Override
	public <E> String serializeCollection(Collection<E> collection, Class<E> objectClazz) {
		if (collection == null)
			return null;

		SegmentedStringWriter source=new SegmentedStringWriter(buffer.get());
		try (SerializerWrapper serializer = createSerializer(source)) {
			mapperFor((Class<E>) objectClazz).serializeCollection(this, serializer, collection);			
		} catch(Exception e)
		{
			e.printStackTrace();
			throw new KriptonRuntimeException(e);
		}
		return source.getAndClear();
	}

	@Override
	public <E> void serializeCollection(Collection<E> collection, Class<E> objectClazz, File source) {
		if (collection == null)
			return;

		try (SerializerWrapper serializer = createSerializer(source)) {
			mapperFor(objectClazz).serializeCollection(this, serializer, collection);
		} catch(Exception e)
		{
			e.printStackTrace();
			throw new KriptonRuntimeException(e);
		}
	}

	@Override
	public <E> void serializeCollection(Collection<E> collection, Class<E> objectClazz, OutputStream source) {
		if (collection == null)
			return;

		try (SerializerWrapper serializer = createSerializer(source)) {
			mapperFor(objectClazz).serializeCollection(this, serializer, collection);
		} catch(Exception e)
		{
			e.printStackTrace();
			throw new KriptonRuntimeException(e);
		}
	}
	
	public abstract ParserWrapper createParser(byte[] data);

	public abstract ParserWrapper createParser(File file);

	public abstract ParserWrapper createParser(InputStream in);

	public abstract ParserWrapper createParser(Reader reader);

	public abstract ParserWrapper createParser(String content);

	public abstract SerializerWrapper createSerializer(File file);

	public abstract SerializerWrapper createSerializer(File file, JsonEncoding encoding);

	public abstract SerializerWrapper createSerializer(OutputStream out);

	public abstract SerializerWrapper createSerializer(OutputStream out, JsonEncoding encoding);

	public abstract SerializerWrapper createSerializer(Writer writer);

}
