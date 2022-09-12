# BetterCoercion Readme

BetterCoercion is a library for LuaJ that improves the coercion mechanics of LuaJ, making it easier to integrate LuaJ into a Java application.

## License

BetterCoercion is released under the GNU Lesser General Public License Version 3. <br>A copy of the GNU Lesser General Public
License Version 3 can be found in the COPYING & COPYING.LESSER files.<br>

## Dependency
Maven:
````
<dependency>
  <groupId>io.github.alexanderschuetz97</groupId>
  <artifactId>BetterCoercion</artifactId>
  <version>0.1</version>
</dependency>
````

## Features
### From Lua:

| routine                                                  | return value | description                                                                                                                                                                                                                                                                                                                                                                                                |
|----------------------------------------------------------|--------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `java.bindClass(string)`                                 | `userdata`   | Same behavior as LuaJavaLib equivalent                                                                                                                                                                                                                                                                                                                                                                     |
| `java.new(string)`                                       | `userdata`   | Same behavior as LuaJavaLib equivalent                                                                                                                                                                                                                                                                                                                                                                     |
| `java.loadLib(string, string)`                           | `varies`     | Same behavior as LuaJavaLib equivalent                                                                                                                                                                                                                                                                                                                                                                     |
| `java.createProxy(string..., table)`                     | `userdata`   | Same behavior as LuaJavaLib equivalent                                                                                                                                                                                                                                                                                                                                                                     |
| `java.toBigInteger(string/number/userdata)`              | `userdata `  | Turns parameter into a BigInteger for Strings the value must be acceptable by the BigInteger constructor. Userdata must be instance of java.lang.Number                                                                                                                                                                                                                                                    |
| `java.toBigDecimal(string/number/userdata)`              | `userdata`   | Turns parameter into a BigDecimal for Strings the value must be acceptable by the BigDecimal constructor. Userdata must be instance of java.lang.Number                                                                                                                                                                                                                                                    |
| `java.newArray(string/Class, number...)`                 | `userdata`   | Makes a java array userdata. string/class must be the arrays component type. number... is array size per array dimension.                                                                                                                                                                                                                                                                                  |
| `java.toArray(string/Class, table)`                      | `userdata`   | Create a java array based on the table                                                                                                                                                                                                                                                                                                                                                                     |
| `java.newCollection(Class/ParameterizedType, [class])`   | `userdata`   | Create a new Collection. By default component type is Object, a second class can be specified to enable type checking for the Collection.                                                                                                                                                                                                                                                                  |
| `java.newMap(Class/ParameterizedType, [Class], [Class])` | `userdata`   | Create a new Map. By default key and value type is Object. More parameters can be specified to enable type checking for the Map.                                                                                                                                                                                                                                                                           |
| `java.bindGenericType(Class, table/Class..., )`          | `userdata`   | Create a new ParameterizedType that can be used to create a Map or Collection with defined generic Type parameters.                                                                                                                                                                                                                                                                                        |
| `java.bindTableInstance(userdata, [Class]..., )`         | `table`      | Create a table of functions that contains all non static methods of the userdata. Each function is bound to the supplied instance and can be called without the instance as the first parameter. This removes the need of useing the ':' operator. Any provided as 2nd parameter should be interfaces. If any interface  is provided the table will only contain methods found in any of the interfaces.   |
| `java.bindUserdataInstance(userdata, [Class]..., )`      | `userdata`   | Create a userdata that reveals all non static methods of the userdata as functions. Each function is bound to the supplied instance and can be called without the instance as the first parameter. This removes the need of useing the ':' operator. Any provided as 2nd parameter should be interfaces. If any interface  is provided the table will only contain methods found in any of the interfaces. |
#### Reflection
Each userdata object created by BetterCoercion can be reflected to allow for fast and painless access to internal variables.
To  reflect a userdata simply index it by using the a '?' prefix. Example to get the private field "blah" from a userdata would be:
````
value = userdata["?fblah"]
userdata["?fblah"] = newValue
````
In order to get a member from the parent class of the userdata simply append a ? for each time you wish to go up another level
example to access the field blah declared in the parent of the parent of the userdata class:
````
value = userdata["???fblah"]
userdata["???fblah"] = newValue
````
Available tokens after ?:

| token              | description                             | example                                                                                  |
|--------------------|-----------------------------------------|------------------------------------------------------------------------------------------|
| ?f                 | access field                            | `u["?fblah"]`                                                                            |
| ?m                 | access method                           | `u["?mblub"]`                                                                            |
| ?m<name>;SIGNATURE | access method by parameter signature    | `u["?mblub;Ljava/lang/String;JI"]` for method `private Object blub(String, long, int);`  |
| ?c                 | get the class of the userdata           | `u["?c"]`                                                                                |
| ?ct                | get the type of class in userdata array | `u["?ct"]`                                                                               |


Each userdata object created by BetterCoercion can  be iterated over as if it were a normal lua table using the 'in pairs(userdata)'
pattern to get all fields and methods exposed by the userdata.


#### Collections and Maps
Java  Collections, Java Iterators  and Java  Maps can be iterated over by using the standard lua 'in pairs(userdata)' pattern.
In addition to that Collections and Maps overload the lua '#' operator allowing for faster access to their size() methods.

#### BigDecimal and BigInteger
Each userdata from BigDecimal or BigInteger has all the mathematical and comparison operators overloaded.
You dont need to (but still can!) do: 
````
bd = bd:add(bd2)
````
Now you have the option to do
````
bd = bd + bd2
````
This should allow you to simplify code that deals with BigDecimals and BigIntegers


### From Java:
- Register custom coercion/conversion handlers for your own types.
- Implement Modules/Services/Beans in Java for use by Lua without the requirement to extend from LuaValue or wrap every call into a VarArgFunction
- Coerce byte[] and char[] to actual mutable arrays instead coercing them to LuaString
- Support for coercing LuaValue/Varargs internal types. You can now freely mix and match in between letting userdata take care of all, some and no coercion even within the same class.

## Examples

### Basic Example:

In Java:
````
Globals globals = JsePlatform.standardGlobals();
 //Default lib name is 'java'
globals.load(new BetterCoercionLib());

//We can however tell it to overwrite the default LuaJavaLib as we provide the same functions and more.
globals.load(new BetterCoercionLib("luajava")); 
//.... (Standart LuaJ from this point)
globals.load(new InputStreamReader(new FileInputStream("test.lua")), "test.lua").call();
````
In test.lua:
````
local java = require('luajava')
local JOptionPane = java.bindClass("javax.swing.JOptionPane")
JOptionPane:showConfirmDialog(nil, "Hello World")

java = require('java')
JOptionPane = java.bindClass("javax.swing.JOptionPane")
JOptionPane:showConfirmDialog(nil, "Hello World 2")
````

### Implementing a Module/Service/Bean for Lua in Java
Service Interface:
````
public interface MyService {

    String makeString(float input);
    
    int[] makeIntArray(int input)

    void doSpecial(Varargs args);
}
````

Service Implementation:
````
public interface MyServiceImpl implements MyService {

    public String makeString(float input) {
        return "My float is: " + String.valueOf(input);
    }
    
    public int[] makeIntArray(int input) {
        int[] result = new int[input];
        for (int i = 0; i < result.length;  i++) {
            result[i] = i*2;
        }
        
        return result;
    }

    public void doSpecial(Varargs args)  {
        System.out.println(args.arg1());
    }
}
````

In Java:
````
Globals globals = JsePlatform.standardGlobals();
globals.load(LuaType.bindInstanceAsModule("MyService", MyServiceImpl.class, MyService.class));
//.... (Standart LuaJ from this point)
globals.load(new InputStreamReader(new FileInputStream("test.lua")), "test.lua").call();
````

In test.lua:
````
local MyService = require('MyService')
print(MyService.makeString(23.4))
print(MyService.makeIntArray(5)[3])
print(MyService.doSpecial({}, 4, "Mep"))
````
