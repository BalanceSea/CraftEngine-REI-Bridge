# CraftEngine REI Bridge

CraftEngine REI Bridge 由一个 Paper 服务端插件和一个 Fabric 客户端模组组成。服务端读取
CraftEngine 的物品与配方数据，客户端将带有真实 `item_model`、自定义模型数据和名称的
物品注册到 REI，并显示精确的合成、锻造和酿造配方。

- 作者：MoutainSeaL
- QQ：3643203568
- QQ 群：342097496

## 支持版本

| Minecraft | Java | Fabric API | REI API | 客户端产物 |
| --- | --- | --- | --- | --- |
| 1.21.11 | 21+ | 0.141.6 | 21.11.816 | `craftengine-rei-bridge-fabric-1.21.11-1.0.0.jar` |
| 26.1.2 | 25+ | 0.155.2 | 26.1.819 | `craftengine-rei-bridge-fabric-26.1.2-1.0.0.jar` |
| 26.2 | 25+ | 0.156.0 | 26.2.821 | `craftengine-rei-bridge-fabric-26.2-1.0.0.jar` |

三个游戏版本使用不同的 Minecraft/Fabric API，不能使用同一个客户端 jar。请按游戏版本
选择对应文件。

## 安装

服务端：

1. 使用对应游戏版本的 Paper，并安装 CraftEngine。
2. 将 `craftengine-rei-bridge-server-1.0.0.jar` 放入服务器 `plugins/`。

客户端：

1. 安装对应游戏版本的 Fabric Loader、Fabric API 和 Roughly Enough Items（REI）。
2. 将对应版本的 `craftengine-rei-bridge-fabric-*.jar` 放入客户端 `mods/`。

服务端和客户端必须同时安装本项目对应组件。客户端连接后会发送握手消息，服务端再按
30 KB 分包发送数据。CraftEngine 热重载时，在线玩家会自动收到新数据；REI 中旧一代
配方会被隐藏，新一代配方立即生效。

## 功能

- 将服务端已加载的全部 CraftEngine 自定义物品加入 REI 物品列表。
- 保留物品的 `custom_model_data`、`item_model` 与可翻译名称。
- 精确显示有序/无序合成的每一个格子，不退化成原版基础材料。
- 精确显示锻造模板、基础物品、附加物和结果。
- 显示 CraftEngine 酿造材料和结果。
- 同步其他 Bukkit 插件注册且涉及 CraftEngine 物品的合成与锻造配方。
- 断开服务器后清理同步物品，并隐藏上一服务器的配方。

锻造纹饰配方的结果由运行时基础盔甲、材料和纹饰共同决定，不存在唯一固定结果，因此
不导出为精确展示配方。

## 构建客户端

项目自带 Gradle Wrapper：

```powershell
.\gradlew.bat buildFabricAll
```

产物位于各自的 `fabric-*/build/libs/`。只构建单个版本可运行：

```powershell
.\gradlew.bat :fabric-1.21.11:build
.\gradlew.bat :fabric-26.1.2:build
.\gradlew.bat :fabric-26.2:build
```

## 构建服务端

CraftEngine 是商业插件，仓库不会分发或下载它。将你合法取得的
`craft-engine-paper-plugin-*.jar` 放入 `server/libs/`，然后运行：

```powershell
.\gradlew.bat :server:build
```

服务端产物位于 `server/build/libs/`。

本项目不使用 Shadow，也不把 CraftEngine、REI、Fabric API 或其他依赖打包进产物。
Fabric 端依赖由 Fabric Loader 提供；Paper 端只使用 Paper 与 CraftEngine 提供的 API，
当前没有需要 LibaryLoader 下载的额外运行库。

## 命令

- `/cereibridge reload`：重新构建服务端同步缓存。
- `/cereibridge resync`：向执行者或全部在线玩家重新发送数据。
- `/cereibridge info`：显示四类同步数据的缓存大小。

权限：`craftengine-rei-bridge.admin`，默认仅 OP 拥有。

## 许可

项目使用 MIT License，并参考了同为 MIT License 的
[TH2403y/CE-JEI-Bridge](https://github.com/TH2403y/CE-JEI-Bridge)。详细归属见 `NOTICE`。
