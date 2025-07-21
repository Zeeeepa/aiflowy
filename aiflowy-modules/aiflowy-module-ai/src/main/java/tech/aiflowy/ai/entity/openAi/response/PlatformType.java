
package tech.aiflowy.ai.entity.openAi.response;

public enum PlatformType {
    DEEPSEEK("deepseek"),
    OPENAI("openai"),
    BAILIAN("aliyun"),        // 阿里百炼
    VOLCENGINE("volcengine"),     // 火山引擎
    QIANFAN("baidu"),        // 百度千帆
    SPARK("spark"),          // 讯飞星火
    GITEE_AI("gitee"),       // Gitee AI
    SILICONFLOW("siliconlow"),    // 硅基流动
    OLLAMA("ollama")          // Ollama
    ;

    private final String brand;

    PlatformType(String brand){
        this.brand = brand;
    }

    public static PlatformType getByBrand(String brand){
        PlatformType[] values = values();
        for (PlatformType type : values){
            if (type.brand.equals(brand)){
                return type;
            }
        }

        return null;
    }
}
