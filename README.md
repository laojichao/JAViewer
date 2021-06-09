## JAViewer
质感设计 更优雅的驾车体验

### 官方Telegram交流群
点击加入 [JAViewer 官方步行街](https://t.me/joinchat/Bp7eL0ehwp4WI-GxWxitQg) 【禁止车辆通行】

## 变更内容
基于原版 2.0.0-Alpha-1 修改了指向地址，增加欧美区，修复了其他BUG。

> 由于签名不同，需要卸载原版再安装，收藏不会丢。通常情况下无需手动修改properties.json，即装即用。

下载地址：https://github.com/SeanChengN/JAViewer/releases

## 已知问题
#### Q1. 无法在线播放（由于原作者搭建的源 api.rekonquer.com 失效，导致无法在线播放）
A1. 考虑使用 Avgle API，但官方不允许嵌入式播放器，只能打开网页播放以保证其广告收入。
> 建议直接在[Avgle](https://avgle.com/)上看，打开app多此一举。
#### Q2. 无法保存收藏（由于 Androd Q 的文件权限变更，导致无法保存配置文件）
A2. 已修复，如果仍然无法保存的，请卸载后重新安装并再次赋予权限。
#### Q3. 界面白屏（由于在线网站的网址变更，也可能是梯子的问题）
A3. 切换无码再切回有码，应用将自动更新网址。搭梯子/换线路，网站被墙，所以要搭梯子。
#### Q4. 卡在启动页（卡logo，应用在启动时会联网检查数据，如果无法获取则卡在启动页）
A4. 必定与网络有关，搭梯子/换线路/换梯子，参考[issues](https://github.com/SeanChengN/JAViewer/issues/9)
#### Q5. 无法打开旧收藏（由于在线网站的网址变更，而旧收藏的网址不会同步更新，导致无法打开收藏）
A5. 用带有文本查找替换功能的应用（比如MT管理器）打开 properties.json 文件（通常在根目录 JAViewer 文件夹内），查找 avmoo.asia 全部替换为 avmask.com 并保存，然后重启应用。**以后网址再改变的话，也是同样操作，我会在此处标注需要替换的内容。**
> 嫌麻烦的话，删掉旧收藏再重新收藏也行。

`avmoo.cyou > avmoo.casa` 2021/06/09

`avsox.cyou > avsox.website` 2020/11/27

`avmoo.host > avmoo.cyou` 2020/11/27

`avmoo.host > avmoo.site` 2020/10/4

`avsox.icu > avsox.cyou` 2020/8/24

`avsox.host > avsox.icu` 2020/8/12

`avmask.com > avmoo.host` 2020/4/16

`avsox.asia > avsox.host` 2020/1/26

`avmoo.asia > avmask.com` 2019/9/19
