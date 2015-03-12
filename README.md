solr-maxparams
==============

This is a very simple SearchComponent that will allow you to specify max values for the `start` and `rows` parameters in any Search Request Handler. The code is dead simple but I hope it helps someone.

This search component basically will check the start/rows parameters against the configured maximum values, if any of this values is greater than the configured value an exception will be thrown and an 400 HTTP error code will be returned along with the `Your start or rows parameter has exceeded the allowed values`message. This will make easy to catch this HTTP error code in the application layer and handle it gracefully.

You can read this [blog post](https://jorgelbg.wordpress.com/2015/03/12/adding-some-safeguard-measures-to-solr-with-a-searchcomponent/) about the code and how it works for more details.

Building
--------

Execute:

```bash
$ mvn package
```

And then copy the generated `jar` in the `target` directory in the `lib` folder of your collection or somewhere when Solr can find it and load it.

Configuration
-------------
Once the `jar` is located by Solr, then you just need no configure a new `searchComponent` in your `solrconfig.xml` file:

```xml
<searchComponent name="max-parameters" class="cu.uci.solr.MaxParamsSearchComponent">
    <str name="rows">10</str>
    <str name="start">2</str>
    <str name="overwriteParams">true</str>
</searchComponent>
```
Now just go to the `requestHandler` where you want to use the component and add it to the `first-components` configuration option:

```xml
<arr name="first-components">
    <str>max-parameters</str>
</arr>
```

Configuration options
---------------------
This search component has only 3 options to configure:
* `rows`: This option will allow you to specify the maximum value for the `rows` parameter.
* `start`: This option will allow you to specify the maximum value for the `start` parameter.
* `overwriteParams`: This option will allow you to rewrite the original query if the maximum values are exceeded, in this case no exception or HTTP error code will be returned, but the query will be executed silently using the specified values in the configuration.
