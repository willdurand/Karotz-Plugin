Karotz Plugin (Jenkins)
=======================

This [Jenkins](http://jenkins-ci.org/) plugin aims to publish build results to your [Karotz](http://www.karotz.com/).


Installation
------------

1. First, create an accout via http://www.karotz.com/login.
2. Then, create an application via http://www.karotz.com/lab/app/form.
3. Finally, create the application package with the following `descriptor.xml`:

``` xml
<version>1.0</version>
    <accesses>
        <access>tts</access>
        <access>ears</access>
        <access>led</access>
        <access>multimedia</access>
    </accesses>
    <deployment>external</deployment>
    <parameters>
        <parameter key="showInstallUuid" value="true"/>
    </parameters>
</version>
```

Credits
-------

* William Durand
* Seiji Sogabe
