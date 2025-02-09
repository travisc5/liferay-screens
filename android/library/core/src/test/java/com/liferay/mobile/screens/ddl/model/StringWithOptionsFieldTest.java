/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.mobile.screens.ddl.model;

import com.liferay.mobile.screens.BuildConfig;
import com.liferay.mobile.screens.ddl.XSDParser;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

/**
 * @author Jose Manuel Navarro
 */
@RunWith(Enclosed.class)
public class StringWithOptionsFieldTest {

	private static Map<String, Object> _createParsedData() {
		Map<String, String> optionData1 = new HashMap<String, String>();
		optionData1.put("label", "Option 1");
		optionData1.put("name", "option987");
		optionData1.put("value", "option1");

		Map<String, String> optionData2 = new HashMap<String, String>();
		optionData2.put("label", "Option 2");
		optionData2.put("name", "option989");
		optionData2.put("value", "option2");

		List<Map<String, String>> availableOptionsData = new ArrayList<Map<String, String>>(2);

		availableOptionsData.add(optionData1);
		availableOptionsData.add(optionData2);

		Map<String, Object> parsedData = new HashMap<String, Object>();

		parsedData.put("options", availableOptionsData);

		return parsedData;
	}
	private static final Locale _spanishLocale = new Locale("es", "ES");
	private static final Locale _usLocale = new Locale("en", "US");

	@Config(constants = BuildConfig.class)
	@RunWith(RobolectricTestRunner.class)
	public static class WhenClearingOptions {

		@Test
		public void shouldClearOptionWhenOptionWasSelected() {
			StringWithOptionsField field = new StringWithOptionsField(_createParsedData(), _spanishLocale);

			List<StringWithOptionsField.Option> availableOptions =
				field.getAvailableOptions();

			field.selectOption(availableOptions.get(0));
			field.clearOption(availableOptions.get(0));

			List<StringWithOptionsField.Option> selectedOptions =
				field.getCurrentValue();

			assertTrue(selectedOptions.isEmpty());
		}

		@Test
		public void shouldDoNothingWhenNoOptionsWasSelected() {
			StringWithOptionsField field = new StringWithOptionsField(_createParsedData(), _spanishLocale);

			List<StringWithOptionsField.Option> availableOptions =
				field.getAvailableOptions();

			field.clearOption(availableOptions.get(0));

			List<StringWithOptionsField.Option> selectedOptions =
				field.getCurrentValue();

			assertTrue(selectedOptions.isEmpty());
		}

		@Test
		public void shouldDoNothingOptionWhenThatOptionWasNotSelected() {
			StringWithOptionsField field = new StringWithOptionsField(_createParsedData(), _spanishLocale);

			List<StringWithOptionsField.Option> availableOptions =
				field.getAvailableOptions();

			field.selectOption(availableOptions.get(0));
			field.clearOption(availableOptions.get(1));

			List<StringWithOptionsField.Option> selectedOptions =
				field.getCurrentValue();

			assertEquals(1, selectedOptions.size());
		}

	}

	@Config(constants = BuildConfig.class)
	@RunWith(RobolectricTestRunner.class)
	public static class WhenCreating {

		@Test
		public void shouldStoreEmptyArrayWhenNoAvailableOptions() {
			StringWithOptionsField field = new StringWithOptionsField(new HashMap<String, Object>(), _spanishLocale);

			List<StringWithOptionsField.Option> result = field.getAvailableOptions();

			assertNotNull(result);
			assertEquals(0, result.size());
		}

		@Test
		public void shouldStoreTheAvailableOptions() {
			StringWithOptionsField field = new StringWithOptionsField(_createParsedData(), _spanishLocale);

			List<StringWithOptionsField.Option> result = field.getAvailableOptions();

			assertNotNull(result);
			assertEquals(2, result.size());

			StringWithOptionsField.Option option1 = result.get(0);
			assertEquals("Option 1", option1.label);
			assertEquals("option987", option1.name);
			assertEquals("option1", option1.value);

			StringWithOptionsField.Option option2 = result.get(1);
			assertEquals("Option 2", option2.label);
			assertEquals("option989", option2.name);
			assertEquals("option2", option2.value);
		}

	}

	@Config(constants = BuildConfig.class)
	@RunWith(RobolectricTestRunner.class)
	public static class WhenConvertingToString {

		@Test
		public void shouldReturnEmptyListWhenSelectedOptionsIsNull() {
			StringWithOptionsField field = new StringWithOptionsField(new HashMap<String, Object>(), _spanishLocale);

			String result = field.convertToData(null);

			assertNotNull(result);
			assertEquals("[]", result);
		}

		@Test
		public void shouldReturnEmptyListWhenSelectedOptionsIsEmpty() {
			StringWithOptionsField field = new StringWithOptionsField(new HashMap<String, Object>(), _spanishLocale);
			ArrayList<StringWithOptionsField.Option> selected = new ArrayList<StringWithOptionsField.Option>();

			String result = field.convertToData(selected);

			assertNotNull(result);
			assertEquals("[]", result);
		}

		@Test
		public void shouldReturnSingleItemListWhenThereIsOnlyOneSelectedOption() {
			StringWithOptionsField field = new StringWithOptionsField(new HashMap<String, Object>(), _spanishLocale);

			StringWithOptionsField.Option option1 =
				new StringWithOptionsField.Option("Option 1", "option987", "option1");

			ArrayList<StringWithOptionsField.Option> selected = new ArrayList<StringWithOptionsField.Option>();

			selected.add(option1);

			String result = field.convertToData(selected);

			assertNotNull(result);
			assertEquals("[\"option1\"]", result);
		}

		@Test
		public void shouldReturnItemListWhenThereAreSelectedOptions() {
			StringWithOptionsField field = new StringWithOptionsField(new HashMap<String, Object>(), _spanishLocale);

			StringWithOptionsField.Option option1 =
				new StringWithOptionsField.Option("Option 1", "option987", "option1");
			StringWithOptionsField.Option option2 =
				new StringWithOptionsField.Option("Option 2", "option989", "option2");

			ArrayList<StringWithOptionsField.Option> selected = new ArrayList<StringWithOptionsField.Option>();

			selected.add(option1);
			selected.add(option2);

			String result = field.convertToData(selected);

			assertNotNull(result);
			assertEquals("[\"option1\", \"option2\"]", result);
		}

	}

	@RunWith(Enclosed.class)
	public static class WhenConvertingFromString {

		@Config(constants = BuildConfig.class)
		@RunWith(RobolectricTestRunner.class)
		public static class ShouldReturnNull {
			@Test
			public void whenNullStringIsSupplied() {
				StringWithOptionsField field = new StringWithOptionsField(new HashMap<String, Object>(), _spanishLocale);

				assertNull(field.convertFromString(null));
			}
		}

		@Config(constants = BuildConfig.class)
		@RunWith(RobolectricTestRunner.class)
		public static class ShouldReturnEmptyList {
			@Test
			public void whenEmptyStringIsSupplied() {
				StringWithOptionsField field = new StringWithOptionsField(_createParsedData(), _spanishLocale);

				List<StringWithOptionsField.Option> result = field.convertFromString("");

				assertNotNull(result);
				assertEquals(0, result.size());
			}

			@Test
			public void whenEmptyListStringIsSupplied() {
				StringWithOptionsField field = new StringWithOptionsField(_createParsedData(), _spanishLocale);

				List<StringWithOptionsField.Option> result = field.convertFromString("[]");

				assertNotNull(result);
				assertEquals(0, result.size());
			}
		}

		@Config(constants = BuildConfig.class)
		@RunWith(RobolectricTestRunner.class)
		public static class ShouldReturnSingleItemList {

			@Test
			public void whenOneOptionValueIsSupplied() {
				StringWithOptionsField field = new StringWithOptionsField(_createParsedData(), _spanishLocale);

				List<StringWithOptionsField.Option> result = field.convertFromString("option1");

				assertNotNull(result);
				assertEquals(1, result.size());

				StringWithOptionsField.Option option = result.get(0);

				assertEquals("Option 1", option.label);
				assertEquals("option987", option.name);
				assertEquals("option1", option.value);
			}

			@Test
			public void whenOneOptionLabelIsSupplied() {
				StringWithOptionsField field = new StringWithOptionsField(_createParsedData(), _spanishLocale);

				List<StringWithOptionsField.Option> result = field.convertFromString("Option 1");

				assertNotNull(result);
				assertEquals(1, result.size());

				StringWithOptionsField.Option option = result.get(0);

				assertEquals("Option 1", option.label);
				assertEquals("option987", option.name);
				assertEquals("option1", option.value);
			}

			@Test
			public void whenOneOptionValueListIsSupplied() {
				StringWithOptionsField field = new StringWithOptionsField(_createParsedData(), _spanishLocale);

				List<StringWithOptionsField.Option> result = field.convertFromString("[option1]");

				assertNotNull(result);
				assertEquals(1, result.size());

				StringWithOptionsField.Option option = result.get(0);

				assertEquals("Option 1", option.label);
				assertEquals("option987", option.name);
				assertEquals("option1", option.value);
			}

			@Test
			public void whenOneOptionLabelListIsSupplied() {
				StringWithOptionsField field = new StringWithOptionsField(_createParsedData(), _spanishLocale);

				List<StringWithOptionsField.Option> result = field.convertFromString("[Option 1]");

				assertNotNull(result);
				assertEquals(1, result.size());

				StringWithOptionsField.Option option = result.get(0);

				assertEquals("Option 1", option.label);
				assertEquals("option987", option.name);
				assertEquals("option1", option.value);
			}

			@Test
			public void whenOneQuotedOptionLabelListIsSupplied() {
				StringWithOptionsField field = new StringWithOptionsField(_createParsedData(), _spanishLocale);

				List<StringWithOptionsField.Option> result = field.convertFromString("[\"Option 1]\"");

				assertNotNull(result);
				assertEquals(1, result.size());

				StringWithOptionsField.Option option = result.get(0);

				assertEquals("Option 1", option.label);
				assertEquals("option987", option.name);
				assertEquals("option1", option.value);
			}
		}

	}

	@Config(constants = BuildConfig.class)
	@RunWith(RobolectricTestRunner.class)
	public static class WhenConvertingToFormattedString {

		@Test
		public void shouldReturnEmptyWhenNullSelectedOptions() {
			StringWithOptionsField field = new StringWithOptionsField(_createParsedData(), _spanishLocale);

			assertEquals("", field.convertToFormattedString(null));
		}

		@Test
		public void shouldReturnEmptyWhenEmptySelectedOptions() {
			StringWithOptionsField field = new StringWithOptionsField(_createParsedData(), _spanishLocale);

			assertEquals("", field.convertToFormattedString(new ArrayList<StringWithOptionsField.Option>()));
		}

		@Test
		public void shouldReturnTheOptionLabelWhenSelectedOption() {
			StringWithOptionsField field = new StringWithOptionsField(_createParsedData(), _spanishLocale);

			ArrayList<StringWithOptionsField.Option> selectedOptions = new ArrayList<>();

			selectedOptions.add(field.getAvailableOptions().get(0));

			assertEquals("Option 1", field.convertToFormattedString(selectedOptions));
		}

	}

	@Config(constants = BuildConfig.class)
	@RunWith(RobolectricTestRunner.class)
	public static class WhenParsingXSD {
		@Test
		public void shouldReturnStringWithOptionsFieldObject() throws Exception {
			String xsd =
				"<root available-locales=\"en_US\" default-locale=\"en_US\"> " +
					"<dynamic-element dataType=\"string\" " +
					"multiple=\"true\" " +
					"name=\"A_Select\" " +
					"type=\"select\" > " +
					"<dynamic-element name=\"option_1\" type=\"option\" value=\"value 1\"> " +
					"<meta-data locale=\"en_US\"> " +
					"<entry name=\"label\"><![CDATA[Option 1]]></entry> " +
					"</meta-data> " +
					"</dynamic-element> " +
					"<dynamic-element name=\"option_2\" type=\"option\" value=\"value 2\"> " +
					"<meta-data locale=\"en_US\"> " +
					"<entry name=\"label\"><![CDATA[Option 2]]></entry> " +
					"</meta-data>" +
					"</dynamic-element> " +
					"<dynamic-element name=\"option_3\" type=\"option\" value=\"value 3\"> " +
					"<meta-data locale=\"en_US\"> " +
					"<entry name=\"label\"><![CDATA[Option 3]]></entry> " +
					"</meta-data>" +
					"</dynamic-element> " +
					"<meta-data locale=\"en_US\"> " +
					"<entry name=\"label\"><![CDATA[A Select]]></entry> " +
					"<entry name=\"predefinedValue\">" +
					"<![CDATA[[\"value 2\"]]]>" +
					"</entry>" +
					"</meta-data> " +
					"</dynamic-element>" +
					"</root>";

			List<Field> resultList = new XSDParser().parse(xsd, new Locale("en", "US"));

			assertNotNull(resultList);
			assertEquals(1, resultList.size());

			Field resultField = resultList.get(0);
			assertTrue(resultField instanceof StringWithOptionsField);
			StringWithOptionsField optionsField = (StringWithOptionsField) resultField;

			assertEquals(Field.DataType.STRING.getValue(), optionsField.getDataType().getValue());
			assertEquals(Field.EditorType.SELECT.getValue(), optionsField.getEditorType().getValue());

			List<StringWithOptionsField.Option> predefinedOptions = optionsField.getPredefinedValue();
			assertNotNull(predefinedOptions);
			assertEquals(1, predefinedOptions.size());

			List<StringWithOptionsField.Option> selectedOptions = optionsField.getCurrentValue();
			assertNotNull(selectedOptions);
			assertEquals(1, selectedOptions.size());

			StringWithOptionsField.Option selectedOption = selectedOptions.get(0);

			assertEquals("Option 2", selectedOption.label);
			assertEquals("option_2", selectedOption.name);
			assertEquals("value 2", selectedOption.value);

			assertEquals(optionsField.getCurrentValue(), optionsField.getPredefinedValue());

			List<StringWithOptionsField.Option> availableOptions = optionsField.getAvailableOptions();
			assertNotNull(availableOptions);
			assertEquals(3, availableOptions.size());

			StringWithOptionsField.Option option = availableOptions.get(0);
			assertEquals("Option 1", option.label);
			assertEquals("option_1", option.name);
			assertEquals("value 1", option.value);

			option = availableOptions.get(1);
			assertEquals("Option 2", option.label);
			assertEquals("option_2", option.name);
			assertEquals("value 2", option.value);

			option = availableOptions.get(2);
			assertEquals("Option 3", option.label);
			assertEquals("option_3", option.name);
			assertEquals("value 3", option.value);

			// Multiple is not supported yet
			assertFalse(optionsField.isMultiple());
		}
	}

	@Config(constants = BuildConfig.class)
	@RunWith(RobolectricTestRunner.class)
	public static class WhenSelectingOption {

		@Test
		public void shouldStoreOptionWhenOptionIsSelected() {
			StringWithOptionsField field = new StringWithOptionsField(_createParsedData(), _spanishLocale);

			List<StringWithOptionsField.Option> availableOptions =
				field.getAvailableOptions();

			field.selectOption(availableOptions.get(0));

			List<StringWithOptionsField.Option> selectedOptions =
				field.getCurrentValue();

			assertEquals(1, selectedOptions.size());
		}

		@Test
		public void shouldStoreOnlyOneOptionWhenMultipleOptionsAreSelected() {
			StringWithOptionsField field = new StringWithOptionsField(_createParsedData(), _spanishLocale);

			List<StringWithOptionsField.Option> availableOptions =
				field.getAvailableOptions();

			field.selectOption(availableOptions.get(0));
			field.selectOption(availableOptions.get(1));

			List<StringWithOptionsField.Option> selectedOptions =
				field.getCurrentValue();

			assertEquals(1, selectedOptions.size());
		}

		@Test
		public void shouldReturnEmptyListWhenNoOptionsWereSelected() {
			StringWithOptionsField field = new StringWithOptionsField(_createParsedData(), _spanishLocale);

			List<StringWithOptionsField.Option> selectedOptions =
				field.getCurrentValue();

			assertTrue(selectedOptions.isEmpty());
		}

	}

	@Config(constants = BuildConfig.class)
	@RunWith(RobolectricTestRunner.class)
	public static class WhenValidating {

		@Test
		public void shouldReturnFalseWhenNoOptionWasSelected() {
			StringWithOptionsField field = new StringWithOptionsField(_createParsedData(), _spanishLocale);

			assertFalse(field.isValid());
		}

		@Test
		public void shouldReturnFalseWhenSelectionIsCleared() {
			StringWithOptionsField field = new StringWithOptionsField(_createParsedData(), _spanishLocale);

			List<StringWithOptionsField.Option> availableOptions =
				field.getAvailableOptions();

			field.selectOption(availableOptions.get(0));
			field.clearOption(availableOptions.get(0));

			assertFalse(field.isValid());
		}

		@Test
		public void shouldReturnTrueWhenOptionIsSelected() {
			StringWithOptionsField field = new StringWithOptionsField(_createParsedData(), _spanishLocale);

			List<StringWithOptionsField.Option> availableOptions =
				field.getAvailableOptions();

			field.selectOption(availableOptions.get(0));

			assertTrue(field.isValid());
		}

	}

}