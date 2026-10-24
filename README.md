# 房贷计算器 - Android 课程作业

**姓名**：葛昭阳
**学号**：2406100109
**班级**：计算机241班
**课程**：金融系统应用开发与实践

## 项目简介

一款简洁的房贷计算工具，支持等额本息和等额本金两种还款方式的月供计算，并展示前 12 期还款明细。

## 功能特性

- 输入贷款金额、年利率
- 选择贷款年限（10-30 年）
- 选择还款方式（等额本息 / 等额本金）
- 可选包含房贷保险
- 展示月供、总利息、总还款额
- ListView 展示前 12 期还款明细（本金/利息拆分）

## 技术要点

- **2 个 Activity**：MainActivity（输入） + ResultActivity（结果）
- **Intent 传值**：主页面将计算结果通过 `putExtra()` 传递给结果页
- **控件覆盖**：TextView、EditText、Button、ImageButton、RadioGroup、CheckBox、Spinner、ListView
- **自定义 Adapter**：使用 SimpleAdapter 绑定还款明细数据
- **学生信息硬编码**：所有页面顶部静态显示姓名、学号、班级

## 如何运行

1. 用 Android Studio 打开本工程
2. 等待 Gradle Sync 完成
3. 连接手机或启动模拟器
4. 点击 Run（绿色三角）或 Build → Generate Signed Bundle/APK 生成 APK

## 页面结构

```
MainActivity（主页面）
├── 学生信息 TextView（硬编码）
├── 贷款金额 EditText
├── 年利率 EditText
├── 贷款年限 Spinner
├── 还款方式 RadioGroup（等额本息 / 等额本金）
├── 附加选项 CheckBox × 2
├── 说明 ImageButton
└── 计算 Button → Intent 传值 → ResultActivity

ResultActivity（结果页）
├── 学生信息 TextView（硬编码）
├── 汇总信息 TextView × 4
├── 还款明细 ListView（前 12 期）
└── 返回 Button
```
