### Envas Commons

Envas commons is a [Niagara](http://www.tridium.com) module containing useful utilities 
for Niagara development. While developing our [Envas](http://www.envas.com) framework, 
we've found that we need tools that Niagara framework can not offer us. 
Envas commons packs some usefull open source projects and a lot of our Java code
into a free Niagara module. Envas commons is licensed under Apache 2.0 license. 

You can download the compiled and with Neopsis certificate signed module `envasCommons.jar` 
from project releases menu.

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

Although Niagara contains **JSON utilities**, we are missing a lot of features implemented
today in all modern JSON handling libraries. We found an open source project 
[Fastjson](https://github.com/alibaba/fastjson) matching all our needs. Fastjson is 
a Java library that can be used to convert Java Objects into their JSON representation. 
It can also be used to convert a JSON string to an equivalent Java object. Fastjson can 
work with arbitrary Java objects including pre-existing objects that you do not have
source-code of.
 
Fast JSON 

 * Provides simple toJSONString() and parseObject() methods to convert Java objects to 
   JSON and vice-versa
 * Allows pre-existing unmodifiable objects to be converted to and from JSON
 * Supports Java Generics
 * Allows custom representations for objects
 * Support arbitrarily complex objects (with deep inheritance hierarchies and extensive 
   use of generic types)
 * Has much smaller size compared to similar libraries like Jackson or Gson.
   
#### Licensing utilities

If you are going to issue a license for your Niagara custom module, you can use Niagara
License Server to maintain your licenses. Unfortunately, Tridium does not offer any kind of
automation when creating a license. To enter a license you need to do everything by hand,
with lot of data entries and copy&paste work. It's time consuming and error-prone. That's 
why we developed our own licensing. Envas licensing uses PKI infrastructure and implements
the code for creating, signing and validation of Envas licenses. Licenses are issued 
as text files in the JSON format. It is up to you how you generate and deploy the licenses.
You can, for example, integrate the Envas licensing into your back office workflow - a customer 
buys your driver on your web site, on the payment callback you will generate a license 
that will be automatically sent to the customer or placed on your licensing server.  
 

Envas licensing utilities implement Baja interfaces and are compatible with the original 
Niagara framework. If you wish to move away from Tridium licensing to Envas licensing, 
you need to change only one row of your code obtaining the license manager. Check the test
project code for more details how to use the utility. Example of a feature check in Niagara:   

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

#### Module signing

Starting from Niagara 4.7 Tridium requires module signing when using reflection. Because both utilities, 
Message Bus and JSON, are using reflection, you have to sign the module.

Tridium uses a keystore for storing the keys and the signing certificates. The keystore location is 
`%user_home%\.tridium\security\<vendor>_signing.jks`. The keystore password and primary key passwords of
all keystone entries are stored in the profile file `%user_home%\.tridium\security\<vendor>_signing.xml`. 
If the keystore and the XML profile do not exist, they will be created for you when the jar task runs 
for the first time and will be filled with the random generated values. You can use the 
[Keystore Explorer](https://keystore-explorer.org) for the keystore entry review. 

For production sites, the usage of the certificates signed by a Certification Authority is highly adviced. 
Using the cheapest [Comodo certificates](https://codesigncert.com/comodocodesigning) is OK. Because 
the Niagara System Trust Store stores the Comodo CA root certificate, you do not have to deploy your 
CA-signed certificate along with your module. 

A short how to for self-signed certificates (OK for development):

1. In the Gradle file `envasCommons-rt.gradle` replace the `cert-alias` with a name identifying your
   code signing entry (e.g. private/public key pair) in the keystore. Example:
   
```
niagaraModule {
    preferredSymbol = "env"
    moduleName = "envasCommons"
    runtimeProfile = "rt"
    certAlias = "mycompany-code-sign"
}
```     

2. Execute the Gradle `jar` task. In the directory `~/.tridium/security` you will find the following 
   new keystore with your new private/public keypair and the XML file describing your security profile with
   the keystore and private key passwords.

   ```
   mycompany_signing.jks
   mycompany_signing.xml
   ``` 
   
3. Use the [Keystore Explorer](https://keystore-explorer.org) to review the keystore entries 
   and to export the self-signed certificate.
   
4. Import the exported certificate into your Workbench and into any station platform running `envasCommons`.
