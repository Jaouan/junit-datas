# junit-datas
JUnit rule that allows you to inject datas from resources files.


# Usage
```
public class DatasRuleTest {

  @Rule
  public DatasRule datasRule = new DatasRule(this);
  
  // Load full JSon formatted file and inject it in field. Assuming that JSon file extension is ".json".
  // Datas will be injected for each test.
  @Datas("datasA.json")
  public Integer[] datasIntegerArray;

  // Limit array length, shuffle values and inject field once time instead of for each test.
  @Datas(value = "datasB.json", count = 50, once = true, shuffle = true)
  public Integer datasIntegerInjectedOnce;

  // Java POJO and all others objects are compatible. Primitive are not supported yet.
  @Datas("datasC.json")
  public Model[] datasModelArrayTxt;

  // Load non full JSon file. One JSon object by line.
  @Datas("datasD.txt")
  public Model[] datasBig;

}
  
```
