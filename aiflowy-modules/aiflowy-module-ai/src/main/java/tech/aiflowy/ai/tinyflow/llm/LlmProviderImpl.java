package tech.aiflowy.ai.tinyflow.llm;

import dev.tinyflow.agentsflex.provider.AgentsFlexLlm;
import dev.tinyflow.core.llm.Llm;
import dev.tinyflow.core.llm.LlmProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tech.aiflowy.ai.entity.AiLlm;
import tech.aiflowy.ai.service.AiLlmService;

import javax.annotation.Resource;
import java.math.BigInteger;

@Component
public class LlmProviderImpl implements LlmProvider {

    private static final Logger log = LoggerFactory.getLogger(LlmProviderImpl.class);
    @Resource
    private AiLlmService aiLlmService;

    @Override
    public Llm getChatModel(Object modelId) {
        AiLlm aiLlm = aiLlmService.getLlmInstance(new BigInteger(modelId.toString()));
        if (aiLlm == null) {
            log.error("LlmProviderImpl.getChatModel: modelId not found: {}", modelId);
            return null;
        }
        AgentsFlexLlm llm = new AgentsFlexLlm();
        llm.setChatModel(aiLlm.toChatModel());
        return llm;
    }
}
