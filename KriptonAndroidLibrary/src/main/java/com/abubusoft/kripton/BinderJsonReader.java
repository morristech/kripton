package com.abubusoft.kripton;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import com.abubusoft.kripton.exception.MappingException;
import com.abubusoft.kripton.exception.ReaderException;

public interface BinderJsonReader extends BinderReader {

	<E> List<E> readList(Class<E> type, String input) throws ReaderException;

	<E> List<E> readList(Class<E> type, InputStream source) throws ReaderException;

	<E> List<E> readList(Class<E> type, Reader source) throws ReaderException, MappingException;

	<E> List<E> readList(List<E> list, Class<E> type, String input) throws ReaderException;

	<E> List<E> readList(List<E> list, Class<E> type, InputStream source) throws ReaderException;

	<E> List<E> readList(List<E> list, Class<E> type, Reader source) throws ReaderException, MappingException;
}