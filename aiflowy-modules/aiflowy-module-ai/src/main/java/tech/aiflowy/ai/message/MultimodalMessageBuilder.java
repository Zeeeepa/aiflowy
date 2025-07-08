package tech.aiflowy.ai.message;

import com.agentsflex.core.llm.functions.Function;
import com.agentsflex.core.message.HumanImageMessage;
import com.agentsflex.core.message.HumanMessage;
import com.agentsflex.core.message.Message;
import com.agentsflex.core.prompt.ImagePrompt;
import com.agentsflex.core.react.ReActMessageBuilder;
import com.agentsflex.core.react.ReActStep;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import tech.aiflowy.common.util.Maps;
import tech.aiflowy.common.web.exceptions.BusinessException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultimodalMessageBuilder extends ReActMessageBuilder {

    private static final Logger logger = LoggerFactory.getLogger(MultimodalMessageBuilder.class);

    @Override
    public Message buildStartMessage(String prompt, List<Function> functions, String userQuery) {

        Map<String,Object> parsePrompt = (Map<String, Object>) JSON.parse(prompt);

        String realPrompt = (String) parsePrompt.get("prompt");

        List<String> fileList =(List<String>) parsePrompt.get("fileList");


        HumanMessage humanMessage = new HumanMessage(realPrompt);


        humanMessage.setMetadataMap(
                Maps.of("type",1)
                        .set("fileList",fileList)
                        .set("user_input",userQuery)
        );

        return humanMessage;
    }


    @Override
    public Message buildActionErrorMessage(ReActStep step, Exception e) {
        return super.buildActionErrorMessage(step, e);
    }


    @Override
    public Message buildJsonParserErrorMessage(Exception e, ReActStep step) {
        return super.buildJsonParserErrorMessage(e, step);
    }


    @Override
    public Message buildObservationMessage(ReActStep step, Object result) {
        return super.buildObservationMessage(step, result);
    }
}
