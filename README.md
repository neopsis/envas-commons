### Envas Commons for Niagara 5

Envas Commons is a [Niagara](http://www.tridium.com) module containing useful utilities 
for Niagara development. While developing our [Envas](http://www.envas.com) framework, 
we've found that we need tools that a Niagara framework cannot offer us. 
Envas commons packs some useful open source projects and a lot of our Java code
into a free Niagara module. Envas Commons are licensed under the Apache 2.0 license. 

Envas Commons for Niagara5 (`envasCommons.jar`) are signed with the Neopsis certificate. 
You can download the compiled modules from [project release menu](https://github.com/neopsis/envas-commons/releases).

The project contains two branches, EnvasCommons for Niagara5 are in the branch N5. 
The Niagara5 version of Envas Commons is based on the Niagara EA release. We cannot rule out changes
caused by updates to the Niagara5 framework until the final Niagara5 version will be released.

#### Message Bus

In Niagara you can subscribe for events on BComponent slots. Unfortunately, there is no 
way to pass events between other types of objects. Another limitation is the impossibility
to send asynchronous events. The solution is the **message bus** from
the envas commons module. Message bus can pass events between any Java POJO objects, the
publishers and subscribers do not have to be Baja objects! The message bus is based on 
the great open source bus implementation [MBassador](https://github.com/bennidi/mbassador).
MBassador is a light-weight, high-performance event bus implementing the publish 
subscribe pattern. See the Github project pages for more details about MBassador. 


#### Modern JSON Library

>**Breaking changes in the Niagara5 version**: EnvasCommons for Niagara5 includes the `fastjson2` 
>library. If you have used functions from the old `fastjson` library in your Niagara4 project,
>you will likely need to make minor changes to your code while migrating it to N5. Details can 
>be found in the [Fastjson2](https://github.com/alibaba/fastjson2) GitHub project pages. 

Although Niagara contains **JSON utilities**, we are missing a lot of features implemented
today in all modern JSON handling libraries. We found an open source project [Fastjson version 2]
(https://github.com/alibaba/fastjson2) matching all our needs. Fastjson is a Java library that 
can be used to convert Java Objects into their JSON representation. It can also be used to convert
a JSON string to an equivalent Java object. Fastjson can work with arbitrary Java objects, 
including pre-existing objects that you do not have source-code of.
 
Fast JSON version 2

 * Provides simple toJSONString() and parseObject() methods to convert Java objects to 
   JSON and vice versa
 * Allows pre-existing unmodifiable objects to be converted to and from JSON
 * Supports Java Generics
 * Allows custom representations for objects
 * Support arbitrarily complex objects (with deep inheritance hierarchies and extensive 
   use of generic types)
 * Has a much smaller size compared to similar libraries like Jackson or Gson.
 * Supports the JSON and JSONB Protocols.
 * Supports full parsing and partial parsing.
 * Supports JSON Schema https://alibaba.github.io/fastjson2/JSONSchema/json_schema_en
   
#### Licensing utilities

If you are going to issue a license for your Niagara custom module, you can use Niagara
License Server to maintain your licenses. Unfortunately, Tridium does not offer any kind of
automation when creating a license. To enter a license, you need to do everything by hand,
with a lot of data entries and copy&paste work. It's time-consuming and error-prone. That's 
why we developed our own licensing. Envas licensing uses PKI infrastructure and implements
the code for creating, signing and validation of Envas licenses. Licenses are issued 
as text files in the JSON format. It is up to you how you generate and deploy the licenses.
You can, for example, integrate the Envas licensing into your back office workflow – a customer 
buys your driver on your website, on the payment callback you will generate a license 
that will be automatically sent to the customer or placed on your licensing server.

Envas licensing utilities implement Baja interfaces and are compatible with the original 
Niagara framework. If you wish to move away from Tridium licensing to Envas licensing, 
you need to change only one row of your code getting the license manager. Check the test
project code for more details on how to use the utility. Example of a feature check in Niagara:   

```
try {
   Feature feature = NvLicenseLocalManager.make().getFeature("neopsis", "envas");

   feature.check();
   feature.getb("charts", false);
   ...

} catch (FeatureLicenseExpiredException e) {
  NvLog.error("Unlicensed: neopsis:envas license expired");
 
} catch (FeatureNotLicensedException e) {
  NvLog.error("Unlicensed: neopsis:envas no license found");

} catch (LicenseDatabaseException e) {
   ...
}
```

See the file `LicenseTest.java` in the test part of the project on how to create your own 
signed license file that can replace the Tridium license files.