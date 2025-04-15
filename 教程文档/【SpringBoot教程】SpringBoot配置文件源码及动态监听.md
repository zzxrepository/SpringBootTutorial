# 自动配置原理

- https://www.cnblogs.com/tkuang/p/16127388.html

# SpringBoot配置文件的加载原理源码分析

### 加载ApplicationListener实现类监听器

在了解了SpringBoot使用的全局配置文件后，我们来思考一个问题，就是该配置文件是如何生效的呢？也就是当我们在配置文件里配置了一些属性，比如配置了server.port = 8088，那么SpringBoot是如何加载该配置文件使得SpringBoot的启动监听端口为8088的呢？

这里初步做一个介绍，更细致的加载过程可以参考下文的源码解析来学习。

SpringBoot对于配置文件的加载是利用ConfigFileApplicationListener监听器来完成的。

对于每一个SpringBoot项目，都会有一个项目主程序启动类，在该类的`main`方法中调用`SpringApplication.run();`方法来启动SpringBoot程序，在启动过程中，便会有SpringBoot的一系列内部加载和初始化过程，因此该类对于我们的分析尤为重要。

点击进入到`SpringApplication`的主类中，观察其构造函数:

![在这里插入图片描述](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/6f63f26881634176b1338bd19c9769cb~tplv-k3u1fbpfcp-zoom-in-crop-mark:1512:0:0:0.awebp) 可以看到在构造器中对监听器进行了设置。其中`getSpringFactoriesInstances`方法用于从spring.factories文件中加载ApplicationListener实现类。该方法的加载原理如下所示，一直深入调用过程找到该方法，可以看到该方法主要就是去`META-INF/spring.factories`路径下加载spring.factories文件，并对文件进行解析。对该文件下的每一个接口及其实现类，以接口名为Key，包含的实现类名为value进行保存，形成一个Map<String, List>的数据结构进行返回。

![在这里插入图片描述](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/85d46be45a1d4f518eea6245878a9ec9~tplv-k3u1fbpfcp-zoom-in-crop-mark:1512:0:0:0.awebp)

然后对调用过程回推，在对spring.factories文件加载完，并保存了文件下每个接口及其实现类的全限定类名后，`getSpringFactoriesInstances`方法会根据传递的参数`Class<T> type`来从上面得到的Map结构中拿出该接口名对应的所有实现类的集合。并对集合中的所有实现类，利用反射生成对应的实例进行保存： ![在这里插入图片描述](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/861c748a5f7c4db5a35ffe74a5b70385~tplv-k3u1fbpfcp-zoom-in-crop-mark:1512:0:0:0.awebp) 到这里我们可以理清构造函数中`setListeners((Collection) getSpringFactoriesInstances(ApplicationListener.class));`方法的过程和作用，即在SpringBoot启动初始化时，通过读取`META-INF/spring.factories`文件，并对其进行解析，生成示例化对象，然后从中取出`ApplicationListener`接口对应的所有实现类的实例对象，注入监听器中。

### ConfigFileApplicationListener监听器监听ApplicationEnvironmentPreparedEvent事件

经过上面的分析我们可以看到，SpringBoot在启动时会初始化一系列监听器，而这些监听器都是在ApplicationListener接口下的，因此我们取到`META-INF/spring.factories`看一下有哪些实现类: ![[外链图片转存失败,源站可能有防盗链机制,建议将图片保存下来直接上传(img-pTfWJZt4-1648785486571)(D:\备忘录\图片\SpringBoot配置文件加载原理\spring.factories.png)]](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/0fbad895b25143ec8bb4198b5741d489~tplv-k3u1fbpfcp-zoom-in-crop-mark:1512:0:0:0.awebp) 这里最关键的实现类就是`ConfigFileApplicationListener`，该监听器会监听ApplicationEnvironmentPreparedEvent事件，当监听到该事件后，会调用`load`方法，去上面说的四个默认路径检索配置文件，如果检索到了，则进行加载封装供上层方法调用。

这是它的一个大致的整体流程，接下来我们深入源码中，按步骤对其进行分析

### 发送事件与监听器的触发

### ApplicationStartingEvent事件

首先进入到`run`方法中： ![在这里插入图片描述](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/7c9f208a3f134492a24716ae748c06cf~tplv-k3u1fbpfcp-zoom-in-crop-mark:1512:0:0:0.awebp) 该方法中首先调用`getRunListeners`方法，同样是从spring.factories文件中加载SpringApplicationRunListeners接口下的实现类`org.springframework.boot.context.event.EventPublishingRunListener`，接下来调用该监听器的`starting()`方法 ![在这里插入图片描述](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/5887981e350c413f9b0c4a0803f1e963~tplv-k3u1fbpfcp-zoom-in-crop-mark:1512:0:0:0.awebp) 该`starting()`方法内，会创建一个`ApplicationStartingEvent`的事件，并利用`multicastEvent`方法进行广播该事件给应用中包含的所有监听器，这里的应用就是参数`this.application`，也就是现在的`SpringApplication`，它所包含的监听器也就是上文中最初加载的`ApplicationListener`下的11个监听器。可以看到，每个`event`对象下都包含一个`source`源，这个源表示了事件最初在其上发生的对象，这里的`source`源就是`SpringApplication`。 ![在这里插入图片描述](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f8a5a665b61e4324ab4d6f4f9f43cb65~tplv-k3u1fbpfcp-zoom-in-crop-mark:1512:0:0:0.awebp) 接下来，会进入到`SimpleApplicationEventMulticaster`类下的`multicastEvent`方法，这里比较重要的一个方法就是`getApplicationListeners`方法，该方法内部会根据该事件的类型，以及事件所包含的源里的监听器，筛选出对该事件感兴趣的监听器集合 ![在这里插入图片描述](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/2bc2e7bc149a4a7c9aaee9d4f5ff9555~tplv-k3u1fbpfcp-zoom-in-crop-mark:1512:0:0:0.awebp) 节选出来`getApplicationListeners`方法内的重要方法: `retrieveApplicationListeners`方法，该方法就是实际检索给定事件和源类型的应用程序侦听器，返回的`listeners`对象即包含了监听该事件的应用程序监听器集合。 ![在这里插入图片描述](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/8abe54c0e760494c9038a7f1d9ffcb46~tplv-k3u1fbpfcp-zoom-in-crop-mark:1512:0:0:0.awebp) 这里的`listeners`即包含了最初`ApplicationListeners`接口下的11个监听器。 ![在这里插入图片描述](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/906cedd3e0c84000877cc4ba630b97d1~tplv-k3u1fbpfcp-zoom-in-crop-mark:1512:0:0:0.awebp) 方法中的`supportsEvent`方法即判断给定监听器是否支持给定事件（或者说是否监听该事件）。由于目前这里的`eventType`表示的是`ApplicationStartingEvent`，该事件触发的监听器包括11个中的:

- `BackgroundPreinitializer`
- `org.springframework.boot.context.logging.LoggingApplicationListener`
- `org.springframework.boot.context.config.DelegatingApplicationListener`
- `org.springframework.boot.liquibase.LiquibaseServiceLocatorApplicationListener`

这里该事件还不触发`org.springframework.boot.context.config.ConfigFileApplicationListener`

最终当`getApplicationListeners`方法拿到监听器对象集合后，遍历得到每个监听器，然后调用`invokeListener(listener, event);`方法，再利用`listener.onApplicationEvent(event)`方法，通过调用相应监听器的`onApplicationEvent(event)`方法来唤醒监听器对象，执行相应的触发操作。 ![在这里插入图片描述](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/fbaf31c6821f4ad597c12417c1f41356~tplv-k3u1fbpfcp-zoom-in-crop-mark:1512:0:0:0.awebp)

### ApplicationEnvironmentPreparedEvent事件

执行完相应监听器的操作后，会继续回到`run`方法中执行`prepareEnvironment`方法，该方法同样是利用监听器和事件的机制，来触发监听完成环境准备的工作。 ![在这里插入图片描述](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/0a4c9b1dc508425fad5e5bfb05623668~tplv-k3u1fbpfcp-zoom-in-crop-mark:1512:0:0:0.awebp) 这里的`listeners`仍然是`EventPublishingRunListener`，因此这里的`prepareEnvironment`相当于是调用了该监听器的不同方法，来产生不同的事件类型，可以看到，这一次创建的事件类型为`ApplicationEnvironmentPreparedEvent`，也就是我们最开始说的加载配置文件的监听器所监听的事件类型，因此到这里我们就离探究配置文件加载原理又近了一步。创建该事件类型后，同样是利用`multicastEvent`将该事件广播给该应用程序下的所有监听器，其实它的流程就跟上面是一样的了，只是产生的事件不同。 ![在这里插入图片描述](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b64f58efe71e431ea38ab84b56221879~tplv-k3u1fbpfcp-zoom-in-crop-mark:1512:0:0:0.awebp) 因此，这里不在赘述该事件的触发流程，同样的是在`retrieveApplicationListeners`方法里的`supportEvent`方法中，筛选出支持`ApplicationEnvironmentPreparedEvent`事件的监听器集合并返回，而这次触发的监听器就包括了`org.springframework.boot.context.config.ConfigFileApplicationListener`监听器。由于监听器的真正执行是通过调用`listener.onApplicationEvent(event)`方法来执行的，因此我们从该方法开始分析： ![在这里插入图片描述](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/1edac0d6a2d54d99ad7f355434a41dc0~tplv-k3u1fbpfcp-zoom-in-crop-mark:1512:0:0:0.awebp) 这里`loadPostProcessors`方法就是从`spring.factories`中加载`EnvironmentPostProcessor`接口对应的实现类，并把当前对象也添加进去(因为`ConfigFileApplicationListener`也实现了`EnvironmentPostProcessor`接口，所以可以添加)。因此在下方遍历时，会访问该类下的`postProcessEnvironment`方法。

接下来进入到`postProcessEnvironment`方法 ![在这里插入图片描述](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/b5a5f5df5d74473c9cad9d7d2423eb6f~tplv-k3u1fbpfcp-zoom-in-crop-mark:1512:0:0:0.awebp) 接下来就是要分析最重要的`Loader`方法 ![在这里插入图片描述](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/8613d7e7c4db424d9089311da7711fd8~tplv-k3u1fbpfcp-zoom-in-crop-mark:1512:0:0:0.awebp) 该方法中，首先`SpringFactoriesLoader.loadFactories`从`spring.factories`中加载`PropertySourceLoader`接口对应的实现类，也就是 ![在这里插入图片描述](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/4bd0731295a0485983f92595cfd4fb05~tplv-k3u1fbpfcp-zoom-in-crop-mark:1512:0:0:0.awebp) 这两个实现类分别用于加载文件名后缀为properties和yaml的文件。

接下来最核心的方法就是红框中的`load`方法，这里会最终加载我们的配置文件，因此我们进行深入探究：

```scss
scss代码解读复制代码private void load(Profile profile, DocumentFilterFactory filterFactory, DocumentConsumer consumer) {
            getSearchLocations().forEach((location) -> {
                boolean isFolder = location.endsWith("/");
                Set<String> names = isFolder ? getSearchNames() : NO_SEARCH_NAMES;
                names.forEach((name) -> load(location, name, profile, filterFactory, consumer));
            });
        }
```

首先调用了`getSearchLocations`方法

```java
java代码解读复制代码//获得加载配置文件的路径
//可以通过spring.config.location配置设置路径，如果没有配置，则使用默认
//默认路径由DEFAULT_SEARCH_LOCATIONS指定：

// CONFIG_ADDITIONAL_LOCATION_PROPERTY = "spring.config.additional-location"
// CONFIG_LOCATION_PROPERTY = "spring.config.location";
// DEFAULT_SEARCH_LOCATIONS = "classpath:/,classpath:/config/,file:./,file:./config/"
private Set<String> getSearchLocations() {
			Set<String> locations = getSearchLocations(CONFIG_ADDITIONAL_LOCATION_PROPERTY);
			if (this.environment.containsProperty(CONFIG_LOCATION_PROPERTY)) {
				locations.addAll(getSearchLocations(CONFIG_LOCATION_PROPERTY));
			}
			else {
				locations.addAll(
						asResolvedSet(ConfigFileApplicationListener.this.searchLocations, DEFAULT_SEARCH_LOCATIONS));
			}
			return locations;
		}
```

该方法用于获取配置文件的路径，如果利用`spring.config.location`指定了配置文件路径，则根据该路径进行加载。否则则根据默认路径加载，而默认路径就是我们最初提到的那四个路径。接下来，再深入`asResolvedSet`方法内部分析一下

```typescript
typescript代码解读复制代码private Set<String> asResolvedSet(String value, String fallback) {
   List<String> list = Arrays.asList(StringUtils.trimArrayElements(StringUtils.commaDelimitedListToStringArray(
         (value != null) ? this.environment.resolvePlaceholders(value) : fallback)));
   Collections.reverse(list);
   return new LinkedHashSet<>(list);
}
```

这里的`value`表示`ConfigFileApplicationListener`初始化时设置的搜索路径，而`fallback`就是`DEFAULT_SEARCH_LOCATIONS`默认搜索路径。`StringUtils.trimArrayElements(StringUtils.commaDelimitedListToStringArray`()）方法就是以逗号作为分隔符对`"classpath:/,classpath:/config/,file:./,file:./config/"`进行切割，并返回一个字符数组。而这里的`Collections.reverse(list);`之后，就是体现优先级的时候了，先被扫描到的配置文件会优先生效。

这里我们拿到搜索路径之后，`load`方法里对每个搜索路径进行遍历，首先调用了`getSearchNames()`方法

```kotlin
kotlin代码解读复制代码// 返回所有要检索的配置文件前缀
// CONFIG_NAME_PROPERTY = "spring.config.name"
// DEFAULT_NAMES = "application"
private Set<String> getSearchNames() {
            if (this.environment.containsProperty(CONFIG_NAME_PROPERTY)) {
                String property = this.environment.getProperty(CONFIG_NAME_PROPERTY);
                return asResolvedSet(property, null);
            }
            return asResolvedSet(ConfigFileApplicationListener.this.names, DEFAULT_NAMES);
        }
```

该方法中如果我们通过`spring.config.name`设置了要检索的配置文件前缀，会按设置进行加载，否则加载默认的配置文件前缀即`application`。

拿到所有需要加载的配置文件前缀后，则遍历每个需要加载的配置文件，进行搜索加载，加载过程如下：

```typescript
typescript代码解读复制代码

private void load(String location, String name, Profile profile, DocumentFilterFactory filterFactory,
                DocumentConsumer consumer) {
            //下面的if分支默认是不走的，除非我们设置spring.config.name为空或者null
            //或者是spring.config.location指定了配置文件的完整路径，也就是入参location的值
            if (!StringUtils.hasText(name)) {
                for (PropertySourceLoader loader : this.propertySourceLoaders) {
                    //检查配置文件名的后缀是否符合要求，
                    //文件名后缀要求是properties、xml、yml或者yaml
                    if (canLoadFileExtension(loader, location)) {
                        load(loader, location, profile, filterFactory.getDocumentFilter(profile), consumer);
                        return;
                    }
                }
                throw new IllegalStateException("File extension of config file location '" + location
                        + "' is not known to any PropertySourceLoader. If the location is meant to reference "
                        + "a directory, it must end in '/'");
            }
            Set<String> processed = new HashSet<>();
            //propertySourceLoaders属性是在Load类的构造方法中设置的，可以加载文件后缀为properties、xml、yml或者yaml的文件
            for (PropertySourceLoader loader : this.propertySourceLoaders) {
                for (String fileExtension : loader.getFileExtensions()) {
                    if (processed.add(fileExtension)) {
                        loadForFileExtension(loader, location + name, "." + fileExtension, profile, filterFactory,
                                consumer);
                    }
                }
            }
        }
```

关注下面的两个`for`循环，`this.propertySourceLoaders`既包含了上面提到的两个`PropertiesPropertySourceLoader`和`YamlPropertySourceLoader`，`PropertiesPropertySourceLoader`可以加载文件扩展名为`properties`和`xml`的文件，`YamlPropertySourceLoader`可以加载文件扩展名为`yml`和`yaml`的文件。获取到搜索路径、文件名和扩展名后，就可以到对应的路径下去检索配置文件并加载了。

```java
java代码解读复制代码private void loadForFileExtension(PropertySourceLoader loader, String prefix, String fileExtension,
      Profile profile, DocumentFilterFactory filterFactory, DocumentConsumer consumer) {
   DocumentFilter defaultFilter = filterFactory.getDocumentFilter(null);
   DocumentFilter profileFilter = filterFactory.getDocumentFilter(profile);
   if (profile != null) {
      // Try profile-specific file & profile section in profile file (gh-340)
       //在文件名上加上profile值，之后调用load方法加载配置文件，入参带有过滤器，可以防止重复加载
      String profileSpecificFile = prefix + "-" + profile + fileExtension;
      load(loader, profileSpecificFile, profile, defaultFilter, consumer);
      load(loader, profileSpecificFile, profile, profileFilter, consumer);
      // Try profile specific sections in files we've already processed
      for (Profile processedProfile : this.processedProfiles) {
         if (processedProfile != null) {
            String previouslyLoaded = prefix + "-" + processedProfile + fileExtension;
            load(loader, previouslyLoaded, profile, profileFilter, consumer);
         }
      }
   }
   // Also try the profile-specific section (if any) of the normal file
    //加载不带profile的配置文件
   load(loader, prefix + fileExtension, profile, profileFilter, consumer);
}
java代码解读复制代码// 加载配置文件
private void load(PropertySourceLoader loader, String location, Profile profile, DocumentFilter filter,
                DocumentConsumer consumer) {
            try {
                //调用Resource类到指定路径加载配置文件
                // location比如file:./config/application.properties
                Resource resource = this.resourceLoader.getResource(location);
                if (resource == null || !resource.exists()) {
                    if (this.logger.isTraceEnabled()) {
                        StringBuilder description = getDescription("Skipped missing config ", location, resource,
                                profile);
                        this.logger.trace(description);
                    }
                    return;
                }
                if (!StringUtils.hasText(StringUtils.getFilenameExtension(resource.getFilename()))) {
                    if (this.logger.isTraceEnabled()) {
                        StringBuilder description = getDescription("Skipped empty config extension ", location,
                                resource, profile);
                        this.logger.trace(description);
                    }
                    return;
                }
                String name = "applicationConfig: [" + location + "]";
                //读取配置文件内容，将其封装到Document类中，解析文件内容主要是找到
                //配置spring.profiles.active和spring.profiles.include的值
                List<Document> documents = loadDocuments(loader, name, resource);
                //如果文件没有配置数据，则跳过
                if (CollectionUtils.isEmpty(documents)) {
                    if (this.logger.isTraceEnabled()) {
                        StringBuilder description = getDescription("Skipped unloaded config ", location, resource,
                                profile);
                        this.logger.trace(description);
                    }
                    return;
                }
                List<Document> loaded = new ArrayList<>();
                //遍历配置文件，处理里面配置的profile
                for (Document document : documents) {
                    if (filter.match(document)) {
                        //将配置文件中配置的spring.profiles.active和
                        //spring.profiles.include的值写入集合profiles中，
                        //上层调用方法会读取profiles集合中的值，并读取对应的配置文件
                        //addActiveProfiles方法只在第一次调用时会起作用，里面有判断
                        addActiveProfiles(document.getActiveProfiles());
                        addIncludedProfiles(document.getIncludeProfiles());
                        loaded.add(document);
                    }
                }
                Collections.reverse(loaded);
                if (!loaded.isEmpty()) {
                    loaded.forEach((document) -> consumer.accept(profile, document));
                    if (this.logger.isDebugEnabled()) {
                        StringBuilder description = getDescription("Loaded config file ", location, resource, profile);
                        this.logger.debug(description);
                    }
                }
            }
            catch (Exception ex) {
                throw new IllegalStateException("Failed to load property source from location '" + location + "'", ex);
            }
        }
```

该方法首先调用`this.resourceLoader.getResource(location);`用来判断location路径下的文件是否存在，如果存在，会调用`loadDocuments`方法对配置文件进行加载：

```ini
ini代码解读复制代码private List<Document> loadDocuments(PropertySourceLoader loader, String name, Resource resource)
                throws IOException {
            DocumentsCacheKey cacheKey = new DocumentsCacheKey(loader, resource);
            List<Document> documents = this.loadDocumentsCache.get(cacheKey);
            if (documents == null) {
                List<PropertySource<?>> loaded = loader.load(name, resource);
                documents = asDocuments(loaded);
                this.loadDocumentsCache.put(cacheKey, documents);
            }
            return documents;
        }
```

再内部根据不同的`PropertySourceLoader`调用相应的`load`方法和`loadProperties(resource)`方法

```typescript
typescript代码解读复制代码public List<PropertySource<?>> load(String name, Resource resource) throws IOException {
        Map<String, ?> properties = loadProperties(resource);
        if (properties.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections
                .singletonList(new OriginTrackedMapPropertySource(name, Collections.unmodifiableMap(properties), true));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map<String, ?> loadProperties(Resource resource) throws IOException {
        String filename = resource.getFilename();
        if (filename != null && filename.endsWith(XML_FILE_EXTENSION)) {
            return (Map) PropertiesLoaderUtils.loadProperties(resource);
        }
        return new OriginTrackedPropertiesLoader(resource).load();
    }
```

由于我们目前的配置文件只有`application.properties`，也就是文件结尾不是以`xml`作为扩展名。因此`loadProperties`方法会进入到`new OriginTrackedPropertiesLoader`。因此再进入到`new OriginTrackedPropertiesLoader(resource).load();`。（不要急 就快到了）

```java
Map<String, OriginTrackedValue> load(boolean expandLists) throws IOException {
        try (CharacterReader reader = new CharacterReader(this.resource)) {
            Map<String, OriginTrackedValue> result = new LinkedHashMap<>();
            StringBuilder buffer = new StringBuilder();
            while (reader.read()) {
                String key = loadKey(buffer, reader).trim();
                if (expandLists && key.endsWith("[]")) {
                    key = key.substring(0, key.length() - 2);
                    int index = 0;
                    do {
                        OriginTrackedValue value = loadValue(buffer, reader, true);
                        put(result, key + "[" + (index++) + "]", value);
                        if (!reader.isEndOfLine()) {
                            reader.read();
                        }
                    }
                    while (!reader.isEndOfLine());
                }
                else {
                    OriginTrackedValue value = loadValue(buffer, reader, false);
                    put(result, key, value);
                }
            }
            return result;
        }
    }
scss代码解读复制代码CharacterReader(Resource resource) throws IOException {
            this.reader = new LineNumberReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.ISO_8859_1));
        }

private String loadKey(StringBuilder buffer, CharacterReader reader) throws IOException {
        buffer.setLength(0);
        boolean previousWhitespace = false;
        while (!reader.isEndOfLine()) {
            // 判断读取到的字节是否为'=' 或者为 ':'，如果是则直接返回读取都的buffer内容
            if (reader.isPropertyDelimiter()) {
                reader.read();
                return buffer.toString();
            }
            if (!reader.isWhiteSpace() && previousWhitespace) {
                return buffer.toString();
            }
            previousWhitespace = reader.isWhiteSpace();
            buffer.append(reader.getCharacter());
            reader.read();
        }
        return buffer.toString();
    }

private OriginTrackedValue loadValue(StringBuilder buffer, CharacterReader reader, boolean splitLists)
            throws IOException {
        buffer.setLength(0);
        while (reader.isWhiteSpace() && !reader.isEndOfLine()) {
            reader.read();
        }
        Location location = reader.getLocation();
        while (!reader.isEndOfLine() && !(splitLists && reader.isListDelimiter())) {
            buffer.append(reader.getCharacter());
            reader.read();
        }
        Origin origin = new TextResourceOrigin(this.resource, location);
        return OriginTrackedValue.of(buffer.toString(), origin);
    }
```

终于，我们看见了曙光。在这个方法里，首先`CharacterReader`方法将我们的resource也就是配置文件转为了输入流，然后利用`reader.read()`进行读取，在`loadKey`方法中我们看到，这里判断读取到的是否为'=' 或者为 ':'，也就是我们在配置文件中以'='或者':'分割的key-value。因此看到这里，我们可以直观的感受到这里应该是读取配置文件，并切分key和value的地方。

最终，对配置文件读取完成后，会将其以key-value的形式封装到一个Map集合中进行返回，然后封装到`OriginTrackedMapPropertySource`中作为一个`MapPropertySource`对象。再层层往上回退发现会最终封装成一个`asDocuments(loaded);`Document对象。最后回到最上层的`load`方法中，`loadDocuments(loader, name, resource);`方法即返回我们加载好的配置文件Document对象集合。并对集合中的每一个配置文件document对象进行遍历，调用`loaded.forEach((document) -> consumer.accept(profile, document));`

## 整理和总结

经过我们上面比较长篇大论的分析，我们已经知道配置文件是如何被检索以及如何被加载的了，接下来，我们对上面的流程进行一下总结和分析：

1. SpringBoot在启动加载时，会利用事件-监听器模式，就像发布-订阅模式，在不同的阶段利用不同的事件唤醒相应的监听器执行对应的操作。对于配置文件加载关键的监听器是`ConfigFileApplicationListener`，该监听器会监听`ApplicationEnvironmentPreparedEvent`事件。
2. 每个事件event都会包含一个`source`源来表示该事件最先发生在其上的对象，`ApplicationEnvironmentPreparedEvent`事件包含的`source`源是`SpringApplication`，包含了一组`listeners`监听器。SpringBoot会根据事件对监听器进行筛选，只筛选出那些支持该事件的监听器，并调用方法唤醒这些监听器执行相应逻辑。
3. 当`ApplicationEnvironmentPreparedEvent`事件发生时，会唤醒`ConfigFileApplicationListener`监听器执行相应逻辑。最主要的加载方法`load`中，首先会获取到配置文件的搜索路径。如果设置了`spring.config.location`则会去指定目录下搜索，否则就去默认的搜索目录下`classpath:/,classpath:/config/,file:./,file:./config/`。
4. 拿到所有待搜索目录后，遍历每个目录获取需要加载的配置文件。如果指定了`spring.config.name`，则加载指定名称的配置文件。否则使用默认的`application`作为配置文件的前缀名。然后，会利用`PropertiesPropertySourceLoader`和`YamlPropertySourceLoader`加载后缀名为`properties、xml、yml或者yaml`的文件。
5. 拿到文件目录和文件名后，就可以去对应的路径下加载配置文件了。核心的过程是利用输入流读取配置文件，并根据读到的分隔符进行判断来切分配置文件的key和value。并将内容以key-value键值对的形式封装成一个`OriginTrackedMapPropertySource`，最后再将一个个配置文件封装成`Document`。最后遍历这些`Documents`，调用`consumer.accept(profile, document));`供上层调用访问。

下面用流程图梳理一下整个加载过程中的关键步骤： 

![在这里插入图片描述](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/100acdd012284267ab98e4f5242dba5a~tplv-k3u1fbpfcp-zoom-in-crop-mark:1512:0:0:0.awebp)

上面是自己关于这个问题阅读源码过程中的一些观点和想法，还是会有不够细致的地方，也可能有理解不够深刻或者错误的地方，还希望各位指正，一起通过阅读源码提升自己的代码水平！





# 参考文献

- https://juejin.cn/post/7083325770755489800