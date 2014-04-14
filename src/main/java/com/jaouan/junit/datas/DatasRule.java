package com.jaouan.junit.datas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.rules.ExternalResource;

import com.google.gson.Gson;

/**
 * 
 * Datas JUnit rule.
 * 
 * @author Maxence Jaouan
 * 
 */
public class DatasRule extends ExternalResource {

  /**
   * File Extension - JSon.
   */
  private static final String FILEEXTENSION_JSON = ".json";

  /**
   * JSon token - Comma.
   */
  private static final Object JSONTOKEN_COMMA = ",";

  /**
   * JSon token - Array begin.
   */
  private static final String JSONTOKEN_ARRAY_BEGIN = "[";

  /**
   * JSon token - Array end.
   */
  private static final String JSONTOKEN_ARRAY_END = "]";

  /**
   * Gson.
   */
  private static final Gson gson = new Gson();

  /**
   * Test class instance.
   */
  private final Object testClassInstance;

  /**
   * Files' objects cache.
   */
  private static Map<String, Object> fileValuesCache = new HashMap<String, Object>();

  /**
   * DatasRule's contructor.
   * 
   * @param testClassInstance
   *          Test class instance.
   */
  public DatasRule(final Object testClassInstance) {
    if (null == testClassInstance) {
      throw new IllegalArgumentException("Test class instance cannot be null.");
    }
    this.testClassInstance = testClassInstance;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void before() throws Throwable {
    super.before();

    // - Inject datas.
    injectDatas();
  }

  /**
   * Inject datas. Synchronized is required for multi-threaded unit test.
   */
  public synchronized void injectDatas() throws DatasRuleException {
    // - Scan each class' fields for @Datas annotation.
    final Field[] fields = this.testClassInstance.getClass().getFields();
    for (final Field field : fields) {
      if (field.isAnnotationPresent(Datas.class)) {

        // - Throw exception if a primitive is annotated with @Datas.
        if (field.getType().isPrimitive() || null != field.getType().getComponentType() && field.getType().getComponentType().isPrimitive()) {
          throw new DatasRuleException("Primitives cannot be annotated with @Datas. Please use primitive's object instead.");
        }

        // - Retrieve @Datas annotation.
        final Datas datas = field.getAnnotation(Datas.class);

        // - If field already injected (not null), skip injection.
        try {
          if (null != field.get(testClassInstance) && datas.once()) {
            continue;
          }
        } catch (final Exception exception) {
          throw new DatasRuleException("Error while getting value of field \"" + field.getName() + "\".", exception);
        }

        // - Retrieve field's type as array.
        Class<?> typeArrayOfField = null;
        if (field.getType().isArray()) {
          typeArrayOfField = field.getType();
        } else {
          typeArrayOfField = Array.newInstance(field.getType(), 0).getClass();
        }

        // - Read file data as type array of field to set it as field's value.
        Object[] fieldValue = (Object[]) this.readFile(datas.value(), typeArrayOfField);

        // - Shuffle array if requested.
        if (datas.shuffle()) {
          DatasRule.shuffle(fieldValue);
        }

        // - If field is an array.
        if (field.getType().isArray()) {
          // - If count equals 0, not limit. Else limit array length with count's value.
          final long datasCount = datas.count();
          if (datasCount > 0) {
            // - Dynamically create a well typed array.
            final Object[] limitedFieldValueArray = (Object[]) Array.newInstance(field.getType().getComponentType(), (int) datasCount);

            // - Copy items one by one.
            for (int i = 0; i < datasCount; i++) {
              limitedFieldValueArray[i] = fieldValue[i];
            }

            // - Remember field value to set.
            fieldValue = limitedFieldValueArray;
          }
        }

        // - Set field value.
        try {
          DatasRule.setFieldValue(field, this.testClassInstance, fieldValue);
        } catch (final Exception exception) {
          throw new DatasRuleException("Error while setting value of field \"" + field.getName() + "\".", exception);
        }

      }
    }
  }

  /**
   * Read file.
   * 
   * @param filePath
   *          File path.
   * @param classOfE
   *          Class of file content.
   * @return File content's object.
   */
  @SuppressWarnings("unchecked")
  private <E> E readFile(final String filePath, final Class<E> classOfE) throws DatasRuleException {
    E object = null;

    // - If file already read, get object in cache.
    if (DatasRule.fileValuesCache.containsKey(filePath)) {
      final Object[] cachedObject = (Object[]) DatasRule.fileValuesCache.get(filePath);
      object = (E) cachedObject.clone();
    }
    // - Else read file.
    else {
      // - Stream file from resource.
      final InputStream inputStream = this.getClass().getResourceAsStream("/" + filePath);
      if (null == inputStream) {
        throw new DatasRuleException("File \"" + filePath + "\" not found in resource folder.");
      }

      final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()));

      // - If file is a JSon file, read it content as JSon.
      if (filePath.toLowerCase().endsWith(FILEEXTENSION_JSON)) {
        try {
          object = gson.fromJson(bufferedReader, classOfE);
        } catch (final Throwable cause) {
          throw new DatasRuleException("Error while deserializing JSon file \"" + filePath + "\".", cause);
        }
      }
      // - Else.
      else {
        // - Read file line by line and format content to JSon array.
        final StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        try {
          while ((line = bufferedReader.readLine()) != null) {
            // - Skip empty line.
            if (!line.trim().isEmpty()) {
              stringBuilder.append(line).append(DatasRule.JSONTOKEN_COMMA);
            }
          }
        } catch (final IOException exception) {
          throw new DatasRuleException("Error while reading file \"" + filePath + "\".", exception);
        }
        final String fileString = stringBuilder.toString();
        final String finalString = DatasRule.JSONTOKEN_ARRAY_BEGIN + fileString.substring(0, fileString.length() - 1) + DatasRule.JSONTOKEN_ARRAY_END;

        // - Read JSon formatted content.
        try {
          object = gson.fromJson(finalString, classOfE);
        } catch (final Throwable cause) {
          throw new DatasRuleException("Error while deserializing file \"" + filePath + "\".", cause);
        }
      }

      // - Close streams.
      try {
        bufferedReader.close();
        inputStream.close();
      } catch (final IOException exception) {
        throw new DatasRuleException("Error while closing stream of file \"" + filePath + "\".", exception);
      }

      // - Cache file's object.
      DatasRule.fileValuesCache.put(filePath, object);
    }

    // - Return file's object.
    return object;
  }

  /**
   * Set field value.
   * 
   * @param field
   *          Field.
   * @param object
   *          Object that contains field to inject.
   * @param arrayValue
   *          Array value.
   */
  private static void setFieldValue(final Field field, final Object object, final Object[] arrayValue) throws IllegalArgumentException, IllegalAccessException {
    // - Set field accessible if not.
    if (!field.isAccessible()) {
      field.setAccessible(true);
    }

    // - If field if an array, set array value to field's value.
    if (field.getType().isArray()) {
      field.set(object, arrayValue.clone());
    }
    // - Else, set first array item to field's value.
    else {
      field.set(object, arrayValue[0]);
    }
  }

  /**
   * Shuffle an array.
   * 
   * @param Array
   *          to shuffle.
   */
  private static void shuffle(final Object[] array) {
    // - Swap array's items randomly.
    final Random random = new Random();
    final int arrayLength = array.length;
    for (int i = 0; i < arrayLength; i++) {
      final int randomIndex = random.nextInt(array.length);
      final Object tmp = array[i];
      array[i] = array[randomIndex];
      array[randomIndex] = tmp;
    }
  }

}
