# junit-datas
JUnit-Datas is a JUnit rule that allows you to inject datas from resources files.


# Usage
```
public class ExampleTest {

  @Rule
  public DatasRule datasRule = new DatasRule(this);
  
  // Load full JSon formatted file from resources and inject it in field. Assuming that JSon file extension is ".json".
  // Datas will be injected for each test.
  @Datas("datasA.json")
  public Integer[] datasIntegerArray;

  // Limit array length, shuffle values and inject field once time instead of for each test.
  @Datas(value = "datasB.json", count = 50, once = true, shuffle = true)
  public Integer[] datasIntegerArrayB;

  // Non-array field will be injected with first array value (or random if shuffled).
  @Datas("datasB.json")
  public Integer datasInteger;

  // Java POJO and all others objects are compatibles. Primitive are not supported yet.
  @Datas("datasC.json")
  public Model[] datasModelArray;

  // Load non full JSon file. One JSon object by line.
  @Datas("datasD.txt")
  public Model[] datasModelArrayB;

}
```

# Files example
## Full JSon file
Full JSon file's extension must be ".json".
Content must be a JSon array.

Here some examples :

Integer array.
```
[1,2,3]
```

String array.
```
['A','B','C']
```

Bean array.
```
[
{a:1, b:'Good'}
]
```


## Non full JSon file
Non full JSon file's extension must be anything else.
Each line is an array value and must be JSon formatted.

Here some examples :

Integer array.
```
1
2
3
```

For String array.
```
'A'
'B'
'C'
```

Bean array.
```
{a:1, b:'abc'}
{a:2, b:'abc'}
```

# Multi-threaded unit test integration
This library can be used with performances tests FrameWorks as ContiPerf.
Datas can be injected manually by calling "injectDatas()" method.
```
public class ExamplePerfTest {

  @Rule
  public ContiPerfRule contiPerfRule = new ContiPerfRule();

  @Rule
  public DatasRule datasRules = new DatasRule(this);

  @Datas(value = "datas.txt", count = 5, shuffle = true)
  public Integer[] array;

  @Test
  @PerfTest(invocations = 100, threads = 20)
  @Required(median = 20)
  public void test() throws DatasRuleException {
    // Synchronized block may be required because of with multi-threaded test.
    synchronized (datasRules) {
      // Required because of ContiPerf multiple invocations.
      datasRules.injectDatas();

      // ... Clone fields to local variables here ...
    }
    
    // ...
  }
}
```