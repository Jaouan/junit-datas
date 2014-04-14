package com.jaouan.junit.datas;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;

/**
 * Unit test for DatasRule.
 */
public class DatasRuleTest {

  @Rule
  public DatasRule datasRule = new DatasRule(this);

  @Datas("datas.json")
  public Integer[] datasIntegerArray;

  @Datas(value = "datas.json", count = 1)
  public Integer[] datasIntegerLimitedArray;

  @Datas("datas.json")
  public Integer datasInteger;

  @Datas(value = "datas.json", once = true)
  public Integer datasIntegerInjectedOnce;

  @Datas("datasStr.json")
  public String datasString;

  @Datas("datasStr.json")
  public String[] datasStringArray;

  @Datas("datasModel.json")
  public Model[] datasModelArray;

  @Datas("datasModel.json")
  public Model datasModel;

  @Datas("datas.txt")
  public Model datasModelTxt;

  @Datas("datas.txt")
  public Model[] datasModelArrayTxt;

  @Datas(value = "datasBig.txt", once = true, shuffle = true)
  public Model[] datasBig;

  @Test
  public void testDatasIntegerArray() {
    assertNotNull(this.datasIntegerArray);
    assertEquals(3, this.datasIntegerArray.length);
  }

  @Test
  public void testDatasIntegerLimitedArray() {
    assertNotNull(this.datasIntegerLimitedArray);
    assertEquals(1, this.datasIntegerLimitedArray.length);
  }

  @Test
  public void testDatasInteger() throws DatasRuleException {
    assertNotNull(this.datasInteger);

    boolean valueChanged = false;
    final Integer beforeReinject = this.datasInteger;
    for (int i = 0; i < 20; i++) {
      datasRule.injectDatas();
      if (beforeReinject.equals(datasInteger)) {
        valueChanged = true;
        break;
      }
    }
    assertTrue(valueChanged);
  }

  @Test
  public void testDatasIntegerOnce() throws DatasRuleException {
    assertNotNull(this.datasInteger);

    final Integer beforeReinject = this.datasIntegerInjectedOnce;
    for (int i = 0; i < 20; i++) {
      datasRule.injectDatas();
      assertEquals(beforeReinject, datasIntegerInjectedOnce);
    }
  }

  @Test
  public void testDatasString() {
    assertNotNull(this.datasString);
  }

  @Test
  public void testDatasStringArray() {
    assertNotNull(this.datasStringArray);
  }

  @Test
  public void testDatasModel() {
    assertNotNull(this.datasModel);
  }

  @Test
  public void testDatasModelArray() {
    assertNotNull(this.datasModelArray);
  }

  @Test
  public void testDatasModelTxt() {
    assertNotNull(this.datasModelTxt);
  }

  @Test
  public void testDatasModelArrayTxt() {
    assertNotNull(this.datasModelArrayTxt);
    assertEquals(2, this.datasModelArrayTxt.length);
  }

  @Test
  public void testDatasBig() {
    assertNotNull(this.datasBig);
    assertEquals(123120, this.datasBig.length);
  }

}
