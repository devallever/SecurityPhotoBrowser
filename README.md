# 私密相册浏览器

这是一款私密相册浏览器，主要内容包括：图片视频导入导出，相册管理，图片视频浏览

- 密码管理
  - 创建密码
  - 修改密码
  - 支持指纹（未实现）
- 导入：文件加密
- 导出：文件解密
- 相册管理：
  - 创建相册
  - 删除相册
  - 重命名相册
- 图片视频浏览


# 异或加解密算法

对一字节进行两次异或操作后得到原来字节

# 定义文件头部

```
魔法值	        4 字节
原始数据偏移量	    4
原始数据长度	    8
时间戳	        8
原始路径的长度	    4
原始路径       	n
加密路径的长度	    4
加密路径	        n
秘钥	            1

```

# 加解密原理

- 给定一个源文件
- 获取文件原始信息
- 创建文件头
- 向文件头写入源文件信息
- 利用随机秘钥对源文件字节进行异或，秘钥保存在文件头
- 创建加密文件

# 解密原理
