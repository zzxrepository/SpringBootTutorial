- https://blog.csdn.net/u011709538/article/details/131396367

- https://blog.csdn.net/zhuizhu761303826/article/details/132492950
- https://blog.csdn.net/weixin_45983377/article/details/125559797





```shell
docker run -d \
--name nacos \
-e JVM_XMS=256m -e JVM_XMX=256m \
--env-file ./nacos/custom.env \
-p 8848:8848 \
-p 9848:9848 \
-p 9849:9849 \
--restart=always \
--network=hm-net \
nacos/nacos-server
```

