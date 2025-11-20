


# 智能命理助手小程序架构设计文档 (Architecture Design)

**文档版本**: V1.0  
**最后更新**: 2025-11-20  
**设计目标**: 构建高可用、易扩展、符合行业规范的 AI 驱动命理测算系统。

---

## 1. 总体架构设计 (System Overview)

### 1.1 架构图 (Mermaid)

```mermaid
graph TD
    User[用户 (微信小程序)] -->|HTTPS/JSON| LB[负载均衡/Nginx]
    LB -->|转发| APIGateway[API 网关 / Spring Boot]
    
    subgraph "后端服务 (Java Spring Boot)"
        Controller[Web 层 (REST API)]
        Service[Service 层 (业务逻辑)]
        Manager[Manager 层 (第三方集成)]
        Common[公共模块 (Result/Exception/Utils)]
        
        Controller --> Service
        Service --> Manager
    end
    
    subgraph "外部服务"
        DeepSeek[DeepSeek API (推理引擎)]
        WeChat[微信服务器 (登录鉴权)]
    end

    Manager -->|HTTP Request (Prompt)| DeepSeek
    Manager -->|Login Code| WeChat
    
    Service -->|数据清洗/组装| Controller
```

### 1.2 设计原则
1.  **前后端分离**：Uni-app 负责页面交互与渲染，Java 后端负责业务逻辑与 AI 交互。
2.  **RESTful 风格**：接口遵循资源导向设计，使用标准 HTTP 动词。
3.  **阿里巴巴开发规范**：后端代码严格遵守《阿里巴巴 Java 开发手册》，包括命名、分层、异常处理等。
4.  **AI 驱动核心**：不使用传统算法库进行排盘，而是通过精细化的 Prompt 工程，让 DeepSeek 返回结构化 JSON 数据，由后端解析后返回前端。

---

## 2. 前端架构设计 (Frontend - Uni-app)

### 2.1 技术栈
*   **框架**: Uni-app (Vue 3 + Vite 版本)
*   **CSS 框架**: 采用主流框架，要求能适配小程序，手机端网页
*   **状态管理**: Pinia (管理用户输入、测算结果缓存)。
*   **网络请求**: `luch-request` (基于 Promise 的请求库，支持拦截器)。
*   **UI 组件**: 原生组件为主，配合自定义组件实现“磨砂玻璃”和“水墨”效果。

### 2.2 目录结构
```text
src/
├── api/                # 接口统一管理
│   └── bazi.js         # 测算相关接口
├── common/             # 公共资源
│   ├── css/            # 全局样式 (Tailwind 引入)
│   └── utils/          # 工具函数 (日期格式化等)
├── components/         # 自定义组件
│   ├── BaziCard.vue    # 八字展示卡片
│   ├── Typewriter.vue  # AI 打字机效果组件
│   └── Loading.vue     # 太极加载动画
├── pages/              # 页面视图
│   ├── index/index.vue # 首页输入
│   ├── result/index.vue# 结果页
│   └── ai/index.vue    # AI 详情页
├── static/             # 静态资源 (背景图 background.jpg)
├── store/              # Pinia 状态管理
└── main.js             # 入口文件
```

### 2.3 关键交互逻辑
1.  **表单提交**: 用户输入阳历日期和时间 -> 前端校验 -> 调用后端 `POST /api/v1/calculation`。
2.  **Loading 状态**: 由于 DeepSeek 推理需要 3-10 秒，前端在发起请求后需立即显示全屏“太极旋转”加载动画，且设置请求超时时间至少为 30s。
3.  **结果渲染**: 后端返回的数据是结构化的（包含八字、五行、Markdown 文本），前端需解析 JSON 渲染图表，并使用 `mp-html` 或自定义解析器渲染 Markdown 文本。

---

## 3. 后端架构设计 (Backend - Java)

### 3.1 技术栈
*   **语言**: Java 17 或 21 (LTS)
*   **框架**: Spring Boot 3.x
*   **HTTP 客户端**: OkHttp 3 或 Retrofit 2 (用于调用 DeepSeek)
*   **工具库**: Hutool (辅助工具), Lombok (简化代码), Jackson (JSON 处理)
*   **规范**: Alibaba Java Coding Guidelines

### 3.2 模块分层 (遵循阿里规范)
```text
com.company.project
├── common             # 公共模块
│   ├── result         # 统一响应结果 (Result.java)
│   ├── exception      # 全局异常处理
│   └── constant       # 常量定义
├── config             # 配置类 (DeepSeekConfig, WebMvcConfig)
├── controller         # 控制层 (REST API)
├── service            # 业务逻辑接口
│   └── impl           # 业务逻辑实现
├── manager            # 外部接口防腐层 (DeepSeekManager)
├── model              # 数据模型
│   ├── dto            # 数据传输对象 (前端传入)
│   ├── vo             # 视图对象 (返回前端)
│   └── entity         # 实体对象 (如需存库)
└── Application.java
```

### 3.3 统一响应规约 (Result.java)
所有接口必须返回如下格式：
```json
{
  "code": 200,
  "success": true,
  "message": "操作成功",
  "data": { ... }
}
```

### 3.4 核心业务流程
1.  **接收请求**: Controller 接收 `CalculationDTO` (date, time)。
2.  **Prompt 组装**: Service 层构建发给 DeepSeek 的 Prompt。
    *   *Prompt 策略*: 强制要求 DeepSeek 返回 **JSON 格式**，而非纯文本。
3.  **AI 推理**: Manager 层调用 DeepSeek API。
4.  **数据清洗**: Service 层将 DeepSeek 返回的 JSON 字符串解析为 Java 对象，处理异常（如 AI 返回格式错误）。
5.  **响应返回**: 组装 `CalculationVO` 返回给前端。

---

## 4. API 接口定义 (RESTful)

**Base URL**: `/api/v1`

### 4.1 核心测算接口

*   **URL**: `/bazi/calculate`
*   **Method**: `POST`
*   **描述**: 提交出生信息，获取排盘及分析结果。
*   **Request Body (CalculationDTO)**:
    ```java
    public class CalculationDTO {
        @NotNull(message = "出生日期不能为空")
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "日期格式需为 yyyy-MM-dd")
        private String birthDate; // 阳历: 2025-11-20

        @NotNull(message = "出生时辰不能为空")
        private String birthTime; // 例如: "子时" 或 "23:00"
    }
    ```
*   **Response Body (Result\<CalculationVO\>)**:
    ```json
    {
      "code": 200,
      "data": {
        "solarDate": "2025-11-20",
        "lunarDate": "二〇二五年十月初一",
        "bazi": {
          "year": "乙巳",
          "month": "丁亥",
          "day": "癸巳",
          "time": "壬子"
        },
        "wuxing": {
          "gold": 0,
          "wood": 1,
          "water": 4,
          "fire": 3,
          "earth": 0,
          "missing": ["金", "土"]
        },
        "zodiac": "蛇",
        "analysis": {
          "character": "日主癸水...",
          "suggestion": "建议佩戴...",
          "poetry": "行到水穷处..."
        }
      }
    }
    ```

---

## 5. DeepSeek 集成方案 (Core Logic)

这是本架构最关键的部分。为了让 AI 充当“计算器”并返回前端可用的字段，必须使用 **JSON Mode** 或 **Structured Output** 策略。

### 5.1 Manager 层实现 (`DeepSeekManager.java`)

```java
@Component
public class DeepSeekManager {
    
    @Value("${deepseek.api.key}")
    private String apiKey;
    
    private static final String API_URL = "https://api.deepseek.com/chat/completions";

    public String fetchBaziAnalysis(String prompt) {
        // 使用 OkHttp 发起请求
        // 设置 "response_format": { "type": "json_object" } 
        // 确保 AI 返回严格的 JSON
    }
}
```

### 5.2 System Prompt 设计
**Service 层构建的 Prompt 模板**：

```text
Role: 你是一位精通中国传统命理学（三命通会、渊海子平）与现代心理学的资深大师。同时你是一个数据处理引擎。

Task: 根据用户提供的【阳历出生日期】和【出生时辰】，进行八字排盘和深度分析。

Input: 
- Date: {birthDate}
- Time: {birthTime}

Constraints:
1. 必须严格按照 JSON 格式输出，不要包含任何 Markdown 标记（如 ```json）。
2. 准确进行阳历转农历计算。
3. 准确计算四柱（年、月、日、时）。
4. 统计五行个数（金木水火土），并列出缺失项。

Output JSON Structure:
{
  "lunarDate": "农历日期字符串",
  "bazi": { "year": "干支", "month": "干支", "day": "干支", "time": "干支" },
  "wuxingCount": { "金": 0, "木": 0, "水": 0, "火": 0, "土": 0 },
  "missingElements": ["缺少的五行数组"],
  "zodiac": "生肖",
  "aiAnalysis": {
     "character": "100字以内的性格侧写，语气温和",
     "suggestion": "100字以内的五行补救建议（颜色、方位、饰品）",
     "poetry": "一句适合用户心境的古诗词"
  }
}
```

---

## 6. 异常处理与稳定性

1.  **DeepSeek 调用失败/超时**:
    *   后端捕获 `SocketTimeoutException`。
    *   返回友好错误码 (e.g., `5001: "天机晦涩，请稍后再试"`).
    *   前端接收到此错误码，提示用户重试。
2.  **JSON 解析失败**:
    *   虽然指定了 JSON Mode，但 LLM 偶尔可能抽风。
    *   后端需在解析失败时，进行一次 **Retry (重试)**，再次调用 DeepSeek。
3.  **数据准确性风控**:
    *   *风险*: LLM 对万年历的转换可能存在细微偏差。
    *   *建议*: 在 V2.0 版本中，建议使用 Java 库 (`lunar-java`) 进行刚性的排盘计算（农历、四柱），仅将 "性格分析" 和 "建议" 交给 DeepSeek 生成。但在 MVP 阶段，为了满足需求文档中的“调用 API 获得信息”，我们全权委托给 AI，但在 Prompt 中强调准确性。

---

## 7. 部署架构

*   **服务器**: 阿里云 ECS / 腾讯云 CVM。
*   **域名**: 需备案，并配置 SSL 证书 (微信小程序强制 HTTPS)。
*   **构建**:
    *   后端: Maven 打包 jar -> Docker 镜像 -> 运行。
    *   前端: HBuilderX 发行 -> 微信开发者工具 -> 上传审核。

---

## 8. 开发规范检查清单 (Checklist)

*   [ ] **Controller**: 仅负责参数接收和响应封装，不写业务逻辑。
*   [ ] **Service**: 负责 Prompt 拼接、调用 Manager、异常捕获。
*   [ ] **DTO/VO**: 严禁直接返回 Entity，必须经过 DTO/VO 转换。
*   [ ] **Magic Number**: 代码中不出现魔法值，统一定义在 Constant 类。
*   [ ] **Log**: 关键节点（如调用 AI 前、收到 AI 响应后）必须打印 Logback 日志，方便排查 Prompt 效果。