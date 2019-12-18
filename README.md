> #### ~~ 一个轻量图床程序 ~~

        ps : 没有后台管理，一个jar包跑起来就能用。

> #### 1、功能简单介绍

- 支持阿里云、腾讯云、七牛、百度云存储

- 可选图片压缩，效果和tinypng一样（可选压缩速度、质量配置）

- 可选计算图片md5开启极速上传（需要Redis）

- 百度图片鉴黄

- 细节配置：临时目录、图片存储目录、目录前缀、网站标题、关键词、
允许上传文件的字节头、最大文件大小限制、是否压缩图片、信任反代ip、记录上传信息、是否鉴黄等

> 所需环境：jdk-1.8、redis、pngquant

```shell script
// 安装Java环境 正常 Centos 即可安装 OpenJdk1.8

yum install -y java
```

```shell script
yum install -y redis
```

```shell script
// 安装 pngquant 小提示事先需要有 libpng 1.6 or 更高版本

git clone --recursive https://github.com/kornelski/pngquant.git
cd pngquant
make
make install

//还是不会就直接去看文档吧 https://pngquant.org/install.html
```


> Redis 和 pngquant 是可选的，开启极速秒传需要 Redis，开启普通压缩需要 pngquant

#### 2、更详细的配置启动 application.properties

```shell script

# 绑定的ip ，默认 0.0.0.0 即可
# 如果设置 127.0.0.1 只能本地的反代可以访问
server.address=0.0.0.0
# 运行端口
server.port=8080

spring.redis.host=127.0.0.1
## Redis服务器连接端口
spring.redis.port=6379
## Redis服务器连接密码（默认为空）
spring.redis.password=

website.title=毕加索图床
website.keywords=毕加索图床,免费图床,免费图片外链,高速外链图床
## 前端上传完成后显示的图片域名
website.upload.picurl=http://xxx.com:8080
# 允许上传的文件头字节
website.upload.file.headers=FFD8FF,89504E47,47494638,52494646,424D
# 上传文件大小限制
# 单位字节 10M
upload.file.maxsize=10485760
# 文件临时存储目录，以便进行其他操作
website.upload.tempdir=/temp/
# 文件存储目录
website.upload.dir=/upload/
# 例如 /${website.upload.dir}}/${website.upload.url.prefix}/{日期}/xxxx-xxxx-xxxx.jpg
# 默认不需要改动
website.upload.url.prefix=files
# 是否启用极速秒传 1 开启、2 关闭，该功能需要安装Redis
file.check.md5=0
# 设置安全的反代服务器ip,作用就是非指定的ip记录访问ip，安全ip记录反代头部ip
# 多个ip用,隔开
server.proxy.safeip=
# 记录上传信息（文件路径，大小，时间，上传者ip等）至本地数据库 1开启、0 关闭
upload.info.write=1
# 压缩图片 1 开启、0 关闭
compress.mode=1
# 使用pngquant进行图片压缩处理 需安装
#compress.pngquant.path=C://pngquant/pngquant.exe
compress.pngquant.path=/usr/local/bin/pngquant
# 数字越大越快，文件越大，一般默认即可
compress.pngquant.speed=2
# 压缩质量区间 默认即可
compress.pngquant.quality=50-90
# 0 不开启同步至云存储
# 1 阿里云OSS 、2 腾讯云COS 、3 七牛云、4 百度云BOS
file.yun=0
file.yun.ali.accessKeyId=sXwyTcfPDH1jaG1
file.yun.ali.accessKeySecret=hUIR5MXm0mrNMxarqmXzTGbFy6d1
file.yun.alioss.endpoint=http://oss-cn-beijing.aliyuncs.com
file.yun.alioss.bucketName=mydiskbucket
# 腾讯Cos
file.yun.cos.secretId=AKIDEIEyWJblqlU6YoEkPQHvNdhaHo2
file.yun.cos.secretKey=MW7Wj4FIBuVuDOROCoKfolY9Ykk2
file.yun.cos.region=ap-beijing
file.yun.cos.bucketName=mysata-1252937574
# 七牛云
file.yun.qiniu.secretId=bJ4lPjhQ4hgOoU5p8m1zVRbjxBUu3d_4Ojr2
file.yun.qiniu.secretKey=BV8dOj8j2HfORT2YI-zRRdHbKasoLyq_2
file.yun.qiniu.bucketName=myboke
#百度Bos
file.yun.baidu.accessKeyId=f44bd4370fdb42e7b5b590be42
file.yun.baidu.secretAccessKey=7a6f481faef64a8d85ac40295a2
file.yun.baidu.bucketName=test212319
#上传文件的存储类型
## 1 常规 、2 低频 、3 冷存储
file.yun.baidu.type=1
# 开启图片鉴黄 1 开启、2 关闭
file.yun.check=1
# 鉴黄检测到色情图片是否删除图片，1 开启、0 关闭
checked.delete=1
## 百度鉴黄
baidu.aip.appid=18036440
baidu.aip.apiKey=yvPeZIAlO8bzt4t5HTUzQ2
baidu.aip.secretKey=Zj34GxDMjeZw1KV9k3CVg91jFGHD2
# api 密码 用来授权api访问的 不使用api建议随便写个随机字符
server.apikey=123456

# 可以写上html 例如统计代码 <script src='//aaa.js></script>
website.header=

# 是否保存本地 1 保存、0 不保存
# 这种是开启云存储的情况下是否本地也保留文件
# 对于一个小鸡来说盘小，一般同步到云存储后删除本地
# 该属性只对开启了云存储后有效
upload.local=1
```

> #### 补充Api
>
>查询记录   GET http://127.0.0.1:8080/api/_all/list?apikey={apikey}&page=1
>
> 删除数据库内容 GET http://127.0.0.1:8080/_delete/list?apikey={apikey}&id={id}
>
> id 可选 没有为删除全部

ps : 这里的删除这是删除数据库内容，并不会删除硬盘和云存储上的内容


> ##### 将上面的配置全部复制 新建一个 application.properties 和 jar 放一起，启动jar会自动读取配置 （注意要 utf-8编码）